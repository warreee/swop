package be.swop.groep11.main.ui;

import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

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

        //maak een nieuwe system aan
        SystemTime systemTime = new SystemTime();
        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        //Aanmaken van controllers
        TaskController taskController = new TaskController(projectRepository, cli, systemTime);
        ProjectController projectController = new ProjectController(projectRepository, new User("ROOT"), cli);
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(cli, systemTime);
        SimulationController simulationController = new SimulationController(cli, projectRepository);
        PlanningController planningController = new PlanningController(cli);
        //Aanmaken main controller
        MainController main = new MainController(cli, advanceTimeController,projectRepository,simulationController,projectController,taskController,planningController);

        ActionMapping actionMapping = cli.getActionMapping();
        actionMapping.activateController(main);

        actionMapping.addCommandStrategy(main, Command.CREATETASK, main::createTask);
        actionMapping.addCommandStrategy(main, Command.UPDATETASK, main::updateTask);
        actionMapping.addCommandStrategy(main, Command.PLANTASK,main::planTask);
        actionMapping.addCommandStrategy(main, Command.CREATEPROJECT,main::createProject);
        actionMapping.addCommandStrategy(main, Command.SHOWPROJECTS,main::showProjects);
        actionMapping.addCommandStrategy(main, Command.ADVANCETIME, main::advanceTime);
        actionMapping.addCommandStrategy(main, Command.STARTSIMULATION, main::startSimulation);

        actionMapping.addCommandStrategy(projectController, Command.SHOWPROJECTS, projectController::showProjects);
        actionMapping.addCommandStrategy(projectController, Command.CREATEPROJECT, projectController::createProject);

        actionMapping.addCommandStrategy(taskController, Command.CREATETASK, taskController::createTask);
        actionMapping.addCommandStrategy(taskController, Command.UPDATETASK, taskController::updateTask);

        actionMapping.addCommandStrategy(advanceTimeController, Command.ADVANCETIME, advanceTimeController::advanceTime);

        actionMapping.addCommandStrategy(simulationController, Command.CREATETASK, taskController::createTask);
        actionMapping.addCommandStrategy(simulationController, Command.UPDATETASK, taskController::updateTask);
        actionMapping.addCommandStrategy(simulationController, Command.PLANTASK, taskController::planTask);
        actionMapping.addCommandStrategy(simulationController, Command.SHOWPROJECTS, projectController::showProjects);
        actionMapping.addCommandStrategy(simulationController, Command.REALIZESIMULATION, simulationController::realize);
        actionMapping.addCommandStrategy(simulationController, Command.CANCEL, simulationController::cancel); //Cancel Simulation


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

    private ActionMapping actionMapping = new ActionMapping(this);

    @Override
    public ActionMapping getActionMapping() {
        return actionMapping;
    }

    private void executeCommand(Command command) {
        actionMapping.executeAction(command);//
    }

    @Override
    public void printMessage(String message) {
        System.out.printf(message + "\n");
    }

    @Override
    public void printException(Exception e) {
        System.out.printf(e.getMessage() + "\n");
    }

    /**
     * Toont een tekstweergave van een lijst projecten.
     * Implementeert showProjectList in UserInterface
     */
    @Override
    public void showProjectList(ImmutableList<Project> projects) {
/*        String format = "%4s %-35s %-20s %-20s %n";
        System.out.printf(format, "nr.", "Naam", "Status", "");
        for (int i=0; i<projects.size(); i++) {
            Project project = projects.get(i);
            String overTime = (project.isOverTime())? "over time" : "on time";
            System.out.printf(format,i,project.getName(),project.getProjectStatus().name(),"("+overTime+")"); // TODO: hier is nog geen methode voor in Project!!!!!!!
        }*/
    }

    /**
     * Toont een tekstversie van een lijst van taken aan de gebruiker
     * @param tasks Lijst van taken
     */
    @Override
    public void showTaskList(ImmutableList<Task> tasks) {

/*        String format = "%4s %-35s %-20s %-15s %-35s %n";
        System.out.printf(format, "nr.", "Omschrijving", "Status", "On time?", "Project");
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

            System.out.printf(format, i, task.getDescription() + asterix, task.getStatus(),
                    onTime, task.getProject().getName());
        }*/
    }

    /**
     * Laat de gebruiker een taak selecteren uit een lijst van taken
     * en geeft het nummer van de geselecteerde taak in de lijst terug.
     * Implementeert selectTaskFromList in UserInterface
     */
    @Override
    public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
        String format = "%-35s %-20s %-15s %-35s %n";
        System.out.println(String.format(format, "Omschrijving", "Status", "On time?"));

        return selectFromList(tasks, (task -> {
            String asterix = (task.isUnacceptablyOverTime())? "*": "";
            String onTime = task.isOverTime()?"nee ("+Math.round(task.getOverTimePercentage()*100)+"%)":"ja";

            return String.format(format, task.getDescription() + asterix, task.getStatus(),onTime);
        }));
    }

    /**
     * Laat de gebruiker een project selecteren uit een lijst van projecten
     * en geeft het nummer van het geselecteerde project in de lijst terug.
     * Implementeert selectProjectFromList in UserInterface
     */
    @Override
    public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException{
        return selectFromList(projects, (project -> {
            String overTime = (project.isOverTime()) ? "over time" : "on time";
            return String.format("%-35s %-20s %-20s %n", project.getName(), project.getProjectStatus().name(), "(" + overTime + ")");
        }));
//
//        if(projects.isEmpty()){
//            throw new EmptyListException("Geen projecten aanwezig");
//        }
//
//        java.lang.System.out.println("Kies een project:");
//        showProjectList(projects);
//
//        try {
//            int nr = getNumberBetween(0, projects.size()-1);
//            Project proj = projects.get(nr);
//            return proj;
//        }
//        catch (IllegalInputException e) {
//            return selectProjectFromList(projects);
//        }
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
        System.out.println("*** Taak details ***");
        String format = "%-25s %s %n";
        System.out.printf(format, "Beschrijving: ", task.getDescription());

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
        System.out.printf(format, "Status: ", task.getStatusString() + " " + finishedStatus); // TODO: hier stond getStatus.name(), we hadden toch nooit een functie .name() ?

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
        System.out.printf(format, "Geschatte duur: ", estimatedDurationHours + "u" + estimatedDurationMinutes + "min");

        double acceptDevPercent = task.getAcceptableDeviation()*100;
       System.out.printf(format, "Aanvaardbare afwijking: ", acceptDevPercent+"%");

        if (task.getStartTime() != null) {
           System.out.printf(format, "Starttijd: ", task.getStartTime().format(formatter));
        }

        if (task.getEndTime() != null) {
            System.out.printf(format, "Eindtijd: ", task.getEndTime().format(formatter));
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

    /**
     * Laat de gebruiker een geheel getal ingeven.
     * Implementeert requestNumber in UserInterface
     */
    @Override
    public int requestNumber(String request) throws CancelException {
        return getIntFromUser.getUserInput(request);
    }

    /**
     * Laat de gebruiker een double ingeven.
     * Implementeert requestDouble in UserInterface
     */
    @Override
    public double requestDouble(String request) throws CancelException {
        return getDoubleFromUser.getUserInput(request);
    }

    /**
     * Laat de gebruiker een string ingeven.
     * Implementeert requestString in UserInterface
     */
    @Override
    public String requestString(String request) throws CancelException {
        return getStringFromUser.getUserInput(request);
    }

    /**
     * Laat de gebruiker een dataum ingeven.
     * Indien de gebruikers niets invult, geeft deze methode null terug.
     * Implementeert requestDatum in UserInterface
     */
    @Override
    public LocalDateTime requestDatum(String request) throws CancelException {
        return getDateFromUser.getUserInput(request);
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
        String response = getStringFromUser.apply(request);
        boolean correct = false;
        Double result = null;
        do{
            try {
                result = new Double(response);
                correct = true;
            } catch (NumberFormatException e) {
                printMessage("Verkeerde input, probeer opnieuw.");
                correct = false;
            }
        }while(!correct);

        return result;
    };
    //Throws NumberFormatException
    userInput<Integer> getIntFromUser = request -> {
        String response = getStringFromUser.apply(request);
        boolean correct = false;
        Integer result = null;
        do{
            try {
                result = new Integer(response);
                correct = true;
            } catch (NumberFormatException e) {
                printMessage("Verkeerde input, probeer opnieuw.");
                correct = false;
            }
        }while(!correct);

        return result;
    };

    userInput<LocalDateTime> getDateFromUser = request -> {
        LocalDateTime result = null;
        boolean correct = false;
        do{
            String response = getStringFromUser.apply(request + " formaat: yyyy-mm-dd hh:mm");
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
    public <T extends Number & Comparable<T>> T numberBetween(userInput<T> userInput,T min,T max)throws CancelException{
        boolean correct = false;
        T response = null;
        do{
            try {
                response = userInput.apply("Gelieve een getal tussen " + min + " & " + max + " te geven.");
                correct = ((response.compareTo(min) > 0 ||response.compareTo(min) == 0) && (response.compareTo(max) < 1 ||response.compareTo(max) == 0));
            } catch (NumberFormatException e) {
                correct = false;
            }
            if(!correct){
                printMessage("Verkeerde input, probeer opnieuw.");
            }
        }while(!correct);
        return response;
    }



    //TODO selectMultipleFromList
    /**
     * Laat de gebruiker een element kiezen uit de gegeven lijst.
     * @param tList             De lijst waaruit men kan kiezen
     * @param listEntryPrinter  De manier waarop ieder element wordt voorgesteld
     * @param <T>               Het type van het geslecteerde element
     * @return                  Geeft het element dat de gebruiker selecteerde.
     * @throws CancelException  indien gebruiker Command.CANCEL ingeeft als invoer. Of indien de gegeven lijst leeg is.
     */
    @Override
    public <T> T selectFromList(List<T> tList,Function<T,String> listEntryPrinter)throws CancelException{
        Function<List<T>,T> listSelector = list -> {
            if(list == null || list.isEmpty()){ throw new CancelException("Lege lijst.");}
            int max = list.size()-1;
            int min = 0;
            int selection = numberBetween(getIntFromUser,min,max);

            return list.get(selection);
        };
        Consumer<List<T>> listPrint = list -> {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i< list.size(); i++){
                sb.append(String.format("%3d. %s\n", i, listEntryPrinter.apply(list.get(i))));
            }
            System.out.println(sb.toString());
        };

        listPrint.accept(tList);
        T selection = listSelector.apply(tList);
        return selection;
    }

    @Override
    public <T> T requestUserInput(String request,userInput<T> userInput) throws CancelException {
        return userInput.getUserInput(request);
    }

    public void showHelp(AbstractController abstractController) throws IllegalArgumentException{
        ArrayList<Command> list = new ArrayList<>();
        //TODO fix show help, nu met actionMapping

        for(Map.Entry<Command,CommandStrategy> entry : abstractController.getCommandStrategies().entrySet()){
            if(!entry.getValue().equals(abstractController.getInvalidStrategy())){
                list.add(entry.getKey());
            }
        }
        StringBuilder sb = new StringBuilder();
        for(Command cmd : list){
            sb.append(String.format("%-2s%s%1s", "|", cmd.getCommandStr()," "));
        }
        sb.append("|");
        printMessage(sb.toString());
    }
}
