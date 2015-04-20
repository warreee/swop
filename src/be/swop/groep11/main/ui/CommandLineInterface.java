package be.swop.groep11.main.ui;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.controllers.MainController;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.ProjectRepositoryMemento;
import be.swop.groep11.main.core.TMSystem;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.CommandStrategy;
import be.swop.groep11.main.ui.commands.IllegalCommandException;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Commandline gebruikersinterface die UserInterface implementeert.
 */
public class CommandLineInterface implements UserInterface {

    private BufferedReader br;


//TODO cleanup nodig, move main to new app class?
    /**
     * Run de CommandLineInterface
     * Wanneer als argument "yaml" wordt opgegeven, wordt het programma met het bestand input/input.tman
     * geïnitialiseerd.
     * @param args De systeem argumenten
     */
    public static void main(String[] args) {
        boolean readYamlFile = false;
        if (args.length == 1 && args[0].equals("yaml"))
            readYamlFile = true;

        // maak een nieuwe CommandLineInterface aan
        CommandLineInterface cli = new CommandLineInterface(readYamlFile);
        // maak een nieuwe system aan
        TMSystem TMSystem = new TMSystem();
        ProjectRepository projectRepository = TMSystem.getProjectRepository();
        //Aanmaken main controller
        MainController main = new MainController(cli, TMSystem, projectRepository);
        cli.addControllerToStack(main);
        // lees commando's
        cli.run();
    }

    /**
     * Constructor om een nieuwe commandline gebruikersinterface te maken.
     * Maakt een nieuw TaskMan object aan en initialiseert de controllers.
     * Leest indien nodig ook het yaml bestand in.
     * @param readYamlFile True als het yaml bestand moet ingelezen worden.
     */
    public CommandLineInterface(boolean readYamlFile) {
        this.br = new BufferedReader(new InputStreamReader(java.lang.System.in));
        this.exit = false;

        if (readYamlFile) {
            // run inputreader
            /*InputParser ir = new InputParser(projectRepository, );
            try {
                ir.parseInputFile();
            } catch (FileNotFoundException e) {
                printMessage("Yaml file niet gevonden");
            }*/
        }
    }

    private void run(){
        try {

            while (! exit) {
                try {
                    String commandString = br.readLine();
                    Command com = Command.getInput(commandString);
                    executeCommand(com);

                }catch (IllegalCommandException ec){
                       printException(ec);
                }
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean exit;


    private  void executeCommand(Command command) {
        CommandStrategy strat = currentCommandStrategies.get(getCurrentController()).get(command);
        if(strat != null) {
            strat.execute();
        }
    }

    private AbstractController getCurrentController() {
        return controllerStack.getLast();
    }

    @Override
    public void addControllerToStack(AbstractController abstractController){
        controllerStack.addLast(abstractController);
        currentCommandStrategies.put(getCurrentController(), getCurrentController().getCommandStrategies());
    }

    @Override
    public void removeControllerFromStack(AbstractController abstractController){
        controllerStack.remove(abstractController);
        currentCommandStrategies.remove(abstractController);
    }
    private HashMap<AbstractController,HashMap<Command,CommandStrategy>> currentCommandStrategies = new HashMap<>();

    /**
     * Lijst van controllers,
     */
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();
    /**
     * Houdt lijst van Controllers bij die "actief zijn".
     * De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit.
     *
     */
    private HashMap<Command,CommandStrategy> commandStrategies = new HashMap<>();



    @Override
    public void printMessage(String message) {
        java.lang.System.out.printf(message + "\n");
    }

    @Override
    public void printException(Exception e) {
        java.lang.System.out.printf(e.getMessage() + "\n");
    }


    private static boolean isValidDateTimeFormat(String string, DateTimeFormatter formatter) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(string,formatter);
            return true;
        }
        catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Toont een tekstweergave van een lijst projecten.
     * Implementeert showProjectList in UserInterface
     */
    @Override
    public void showProjectList(ImmutableList<Project> projects) {
        String format = "%4s %-35s %-20s %-20s %n";
        java.lang.System.out.printf(format, "nr.", "Naam", "Status", "");
        for (int i=0; i<projects.size(); i++) {
            Project project = projects.get(i);
            String overTime = "on time";
            if (project.isOverTime())
                overTime = "over time";
            java.lang.System.out.printf(format,
                    i,
                    project.getName(),
                    project.getProjectStatus().name(),
                    "("+overTime+")"); // TODO: hier is nog geen methode voor in Project!!!!!!!
        }
    }

    /**
     * Laat de gebruiker een project selecteren uit een lijst van projecten
     * en geeft het nummer van het geselecteerde project in de lijst terug.
     * Implementeert selectProjectFromList in UserInterface
     */
    @Override
    public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException{
        if(projects.isEmpty()){
            throw new EmptyListException("Geen projecten aanwezig");
        }

        java.lang.System.out.println("Kies een project:");
        showProjectList(projects);

        try {
            int nr = getNumberBetween(0, projects.size()-1);
            Project proj = projects.get(nr);
            return proj;
        }
        catch (IllegalInputException e) {
            return selectProjectFromList(projects);
        }
    }

    /**
     * Toont een tekstversie van een lijst van taken aan de gebruiker
     * @param tasks Lijst van taken
     */
    @Override
    public void showTaskList(ImmutableList<Task> tasks) {
        String format = "%4s %-35s %-20s %-15s %-35s %n";
        java.lang.System.out.printf(format, "nr.", "Omschrijving", "Status", "On time?", "Project");
        for (int i=0; i<tasks.size(); i++) {
            Task task = tasks.get(i);

            // add asterix?
            String asterix = "";
            if (task.isUnacceptablyOverTime()) {
                asterix = " *";
            }

            // on time?
            String onTime = "ja";
            if (task.isOverTime()) {
                long percentage = Math.round(task.getOverTimePercentage()*100);
                onTime = "nee ("+percentage+"%)";
            }

            java.lang.System.out.printf(format, i, task.getDescription() + asterix, task.getStatus(),
                    onTime, task.getProject().getName());
        }
    }

    /**
     * Laat de gebruiker een taak selecteren uit een lijst van taken
     * en geeft het nummer van de geselecteerde taak in de lijst terug.
     * Implementeert selectTaskFromList in UserInterface
     */
    @Override
    public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
        if(tasks.isEmpty()){
            throw new EmptyListException("Geen taken aanwezig");
        }

        java.lang.System.out.println("Kies een taak:");
        showTaskList(tasks);

        try {
            int nr = getNumberBetween(0, tasks.size()-1);
            Task task = tasks.get(nr);
            return task;
        }
        catch (IllegalInputException e) {
            return selectTaskFromList(tasks);
        }
    }

    /**
     * Toont een tekstweergave van de details van een project.
     * Implementeert showProjectDetails uit UserInterface
     */
    @Override
    public void showProjectDetails(Project project) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        java.lang.System.out.println("*** Project details ***");
        String format = "%-25s %s %n";
        java.lang.System.out.printf(format, "Naam: ", project.getName());
        java.lang.System.out.printf(format, "Beschrijving: ", project.getDescription());
        java.lang.System.out.printf(format, "Status: ", project.getProjectStatus().name());
        String onTime = "ja";
        if (project.isOverTime()) {
            onTime = "nee";
        }
        java.lang.System.out.printf(format, "Op tijd: ", onTime);
        java.lang.System.out.printf(format, "Creation time: ", project.getCreationTime().format(formatter));
        java.lang.System.out.printf(format, "Due time: ", project.getDueTime().format(formatter));
        java.lang.System.out.printf(format, "Geschatte eindtijd: ", project.getEstimatedEndTime().format(formatter));
    }

    /**
     * Toont een tekstweergave van de details van een taak.
     * Implementeert showTaskDetails uit UserInterface
     */
    @Override
    public void showTaskDetails(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        java.lang.System.out.println("*** Taak details ***");
        String format = "%-25s %s %n";
        java.lang.System.out.printf(format, "Beschrijving: ", task.getDescription());

        String finishedStatus = "";
        if (task.getFinishedStatus() == Task.FinishedStatus.EARLY) {
            finishedStatus = "early";
        }
        else if (task.getFinishedStatus() == Task.FinishedStatus.ONTIME) {
            finishedStatus = "on time";
        }
        else if (task.getFinishedStatus() == Task.FinishedStatus.OVERDUE) {
            finishedStatus = "late";
        }
        java.lang.System.out.printf(format, "Status: ", task.getStatusString() + " " + finishedStatus); // TODO: hier stond getStatus.name(), we hadden toch nooit een functie .name() ?

        // on time?
        String onTime = "ja";
        if (task.isOverTime()) {
            long percentage = Math.round(task.getOverTimePercentage()*100);
            onTime = "nee ("+percentage+"%)";
        }
        java.lang.System.out.printf(format, "Momenteel on time: ", onTime);

        Duration estimatedDuration = task.getEstimatedDuration();
        long estimatedDurationHours = estimatedDuration.getSeconds() / (60*60);
        long estimatedDurationMinutes = (estimatedDuration.getSeconds() % (60*60)) / 60;
        java.lang.System.out.printf(format, "Geschatte duur: ", estimatedDurationHours + "u" + estimatedDurationMinutes + "min");

        double acceptDevPercent = task.getAcceptableDeviation()*100;
        java.lang.System.out.printf(format, "Aanvaardbare afwijking: ", acceptDevPercent+"%");

        if (task.getStartTime() != null) {
            java.lang.System.out.printf(format, "Starttijd: ", task.getStartTime().format(formatter));
        }

        if (task.getEndTime() != null) {
            java.lang.System.out.printf(format, "Eindtijd: ", task.getEndTime().format(formatter));
        }

        java.lang.System.out.println("Afhankelijk van:");
        if (! task.getDependingOnTasks().isEmpty()) {
            showTaskList(ImmutableList.copyOf(task.getDependingOnTasks()));
        }
        else {
            java.lang.System.out.println("Deze taak hangt niet van andere taken af");
        }

        if (task.getAlternativeTask() != null) {
            java.lang.System.out.printf(format, "Alternatieve taak: ", task.getAlternativeTask().getDescription());
        }
    }

    private ProjectRepositoryMemento memento;

    /**
     * Laat de gebruiker een geheel getal ingeven.
     * Implementeert requestNumber in UserInterface
     */
    @Override
    public int requestNumber(String request) throws CancelException {
        String input = requestInput(request);
        resolveCancel(input);

        while (! isInteger(input)){
            java.lang.System.out.println("Ongeldige invoer: moet een geheel getal zijn");
            input = requestInput(request);
            resolveCancel(input);
        }
        return Integer.parseInt(input);
    }

    private static boolean isInteger(String string) {
        try {
            int d = Integer.parseInt(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Laat de gebruiker een double ingeven.
     * Implementeert requestDouble in UserInterface
     */
    @Override
    public double requestDouble(String request) throws CancelException {
        String input = requestInput(request);
        resolveCancel(input);

        while (! isDouble(input)){
            java.lang.System.out.println("Ongeldige invoer: moet een double zijn");
            input = requestInput(request);
            resolveCancel(input);
        }
        return Double.parseDouble(input);
    }

    private static boolean isDouble(String string) {
        try {
            double d = Double.parseDouble(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Laat de gebruiker een string ingeven.
     * Implementeert requestString in UserInterface
     */
    @Override
    public String requestString(String request) throws CancelException {
        String input = requestInput(request);
        resolveCancel(input);
        return input;
    }

    /**
     * Laat de gebruiker een dataum ingeven.
     * Indien de gebruikers niets invult, geeft deze methode null terug.
     * Implementeert requestDatum in UserInterface
     */
    @Override
    public LocalDateTime requestDatum(String request) throws CancelException {
        String input = requestInput(request + " formaat: yyyy-mm-dd hh:mm");
        if (input.isEmpty())
            return null;
        resolveCancel(input);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (! isValidDateTimeFormat(input, formatter)) {
            java.lang.System.out.println("Ongeldig formaat");
            input = requestInput(request + " formaat: yyyy-mm-dd hh:mm");
            if (input.isEmpty())
                return null;
            resolveCancel(input);
        }
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
        return dateTime;
    }
    private void resolveCancel(String str) throws CancelException{
        if(Command.checkCancel(str)){
            throw new CancelException("Canceled");
        }
    }
    private void resolveEmpty(String string)throws CancelException{
        if(string == null || string.isEmpty()){
            throw new CancelException("Canceled omwille van lege response.");
        }
    }

    private int getNumberBetween(int min,int max)throws IllegalInputException, CancelException{
        int num = requestNumber("Gelieve een getal tussen " + min + " & " + max + " te geven.");
        if(!(num <= max && num >= min)){
            throw new IllegalInputException("Getal niet tussen "  + min + " & " + max + "");
        }
        return num;
    }

    /**
     *
     * @param request
     * @return
     * @throws CancelException Indien de gebruiker cancel ingeeft
     */
    private String requestInput(String request) {
        if(request.length() > 30){
            System.out.println(request);
        }else{
            System.out.printf("%30s%3s",request,":");
        }
        String result = "";
        try {
            result = br.readLine();
            resolveCancel(result);
            resolveEmpty(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    userInput<String> getStringFromUser = request -> {
        System.out.println(request);
        String result = "";
        try {
            result = br.readLine();
            resolveCancel(result);
            resolveEmpty(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    };
    //Throws NumberFormatException
    userInput<Double> getDoubleFromUser = request -> {
        String response = getStringFromUser.getUserInput(request);
        return new Double(response);
    };
    //Throws NumberFormatException
    userInput<Integer> getIntFromUser = request -> {
        String response = getStringFromUser.getUserInput(request);
        return new Integer(response);
    };



    /**
     * Vraag een getal aan de user tussen een min en max waarde.
     *
     * @param userInput de functie waarmee de invoer aan de gebruiker gevraagd wordt.
     * @param min       De minimum toegelaten waarde (inclusief)
     * @param max       De maximum toegelaten waarde (inclusief)
     * @param <T>       Het Type van het gevraagde getal tussen min en max.
     * @return          Een getal van Type <T> dat uit [min,max] komt.
     * @throws CancelException  gooi indien de gebruiker het Command.CANCEL in geeft.
     */
    private <T extends Number & Comparable<T>> T numberBetween(userInput<T> userInput,T min,T max)throws CancelException{
        boolean correct = false;
        T response = null;
        do{
            try {
                response = userInput.getUserInput("Gelieve een getal tussen " + min + " & " + max + " te geven.");
                correct = (response.compareTo(min) >= 0 && response.compareTo(max) <= 1);
            } catch (NumberFormatException e) {
                printMessage("Verkeerde input, probeer opnieuw.");
                correct = false;
            }
        }while(!correct);
        return response;
    }

    userInput<LocalDateTime> getDateFromUser = request -> {
        LocalDateTime result = null;
        boolean correct = false;
        do{
            String response = getStringFromUser.getUserInput(request + " formaat: yyyy-mm-dd hh:mm");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            try {
                result = LocalDateTime.parse(response, formatter);
                correct = true;
            } catch (Exception e) {
                printMessage("Verkeerde input, komt niet overeen met het formaat. Probeer opnieuw");
                correct = false;
            }
        }while(!correct);
        return result;
    };

    userInput<Task> getTaskFromList = request -> {

        return null;
    };


    private <T> void lllll(List<T> tList,ListSelector<T> listSelector,Consumer<T> listPrinter){
//        Function<List<T>,T> foo = tList1 -> null;
        ListSelector<T> selectTask = list -> {
            if(list == null || list.isEmpty()){ throw new EmptyListException("Lege lijst.");}
            int max = list.size();
            int min = 1;
            int selection = numberBetween(getIntFromUser,min,max);

            return list.get(selection-1);
        };
        tList.stream().forEachOrdered(listPrinter);
        T task = selectTask.select(tList);
    }




//    public <T> T selectOptionFromList(List<T> list,Consumer<T> listPrinter){
//        list.stream().forEachOrdered(listPrinter);
//
//        //Show list elements
//        //User enters nr associated with list element
//        //returns selected element
//        return null;
//    }


    private String format = "%-2s%s%-2s";
    public void showHelp(AbstractController abstractController) throws IllegalArgumentException{
        ArrayList<Command> list = new ArrayList<>();

        for(Map.Entry<Command,CommandStrategy> entry : abstractController.getCommandStrategies().entrySet()){
            if(!entry.getValue().equals(abstractController.getInvalidStrategy())){
                list.add(entry.getKey());
            }
        }

        StringBuilder sb = new StringBuilder();
        for(Command cmd : list){
            sb.append(String.format(format, "|", cmd.getCommandStr()," "));
        }
        sb.append("|");
        printMessage(sb.toString());
    }
}
