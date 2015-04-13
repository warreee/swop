package be.swop.groep11.main.ui;

import be.swop.groep11.main.*;
import be.swop.groep11.main.TMSystem;
import be.swop.groep11.main.controllers.AdvanceTimeController;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.IllegalCommandException;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Commandline gebruikersinterface die UserInterface implementeert.
 */
public class CommandLineInterface implements UserInterface {

    private BufferedReader br;

    /**
     * Controllers
     */
    private ProjectController projectController;
    private TaskController taskController;
    private AdvanceTimeController advanceTimeController;

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

        // maak een nieuwe system aan
        TMSystem TMSystem = new TMSystem();
        ProjectRepository projectRepository = TMSystem.getProjectRepository();

        if (readYamlFile) {
            // run inputreader
            InputParser ir = new InputParser(projectRepository);
            try {
                ir.parseInputFile();
            } catch (FileNotFoundException e) {
                printMessage("Yaml file niet gevonden");
            }
        }

        // maak de controllers aan
        User user = new User("ROOT");
        this.projectController = new ProjectController(projectRepository, user, this);
        this.taskController = new TaskController(projectRepository, this);
        this.advanceTimeController = new AdvanceTimeController(TMSystem,this);
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

    private  void executeCommand(Command command) {
        switch (command) {
            case EXIT:
                exit = true;
                break;
            case HELP:
                printHelp();
                break;
            case CREATEPROJECT:
                getProjectController().createProject();
                break;
            case ADVANCETIME:
                getAdvanceTimeController().advanceTime();
                break;
            case UPDATETASK:
                getTaskController().updateTask();
                break;
            case CREATETASK:
                getTaskController().createTask();
                break;
            case SHOWPROJECTS:
                getProjectController().showProjects();
                break;
        }
    }

    private boolean exit;

    private void printHelp(){
        StringBuilder sb = new StringBuilder();
        for(Command cmd: Command.values()){
            sb.append(" | ");
            sb.append(cmd.getCommandStr());
        }
        sb.append(" | ");
        printMessage(sb.toString());
    }

    @Override
    public void printMessage(String message) {
        java.lang.System.out.printf(message + "\n");
    }

    @Override
    public void printException(Exception e) {
        java.lang.System.out.printf(e.getMessage() + "\n");
    }

    /**
     * Laat de gebruiker een geheel getal ingeven.
     * Implementeert requestNumber in UserInterface
     */
    @Override
    public int requestNumber(String request) throws CancelException {
        String input = requestInput(request);
        checkCancel(input);

        while (! isInteger(input)){
            java.lang.System.out.println("Ongeldige invoer: moet een geheel getal zijn");
            input = requestInput(request);
            checkCancel(input);
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
        checkCancel(input);

        while (! isDouble(input)){
            java.lang.System.out.println("Ongeldige invoer: moet een double zijn");
            input = requestInput(request);
            checkCancel(input);
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
        checkCancel(input);
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
        checkCancel(input);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (! isValidDateTimeFormat(input, formatter)) {
            java.lang.System.out.println("Ongeldig formaat");
            input = requestInput(request + " formaat: yyyy-mm-dd hh:mm");
            if (input.isEmpty())
                return null;
            checkCancel(input);
        }
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
        return dateTime;
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
        java.lang.System.out.printf(format, "Status: ", task.getStatus().name() + " " + finishedStatus);

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

    private void checkCancel(String str) throws CancelException{
        if(Command.checkCancel(str)){
            throw new CancelException("Canceled");
        }
    }

    private int getNumberBetween(int min,int max)throws IllegalInputException, CancelException{
        int num = requestNumber("Gelieve een getal tussen "  + min + " & " + max + " te geven.");
        if(!(num <= max && num >= min)){
            throw new IllegalInputException("Getal niet tussen "  + min + " & " + max + "");
        }
        return num;
    }

    private String requestInput(String request) {
        java.lang.System.out.println(request);
        String result = "";
        try {
            result = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Geeft de project controller voor deze command line gebruikersinterface
     */
    private ProjectController getProjectController() {
        return projectController;
    }

    /**
     * Geeft de task controller voor deze command line gebruikersinterface
     */
    private TaskController getTaskController() {
        return taskController;
    }

    public AdvanceTimeController getAdvanceTimeController() {
        return advanceTimeController;
    }
}
