package be.swop.groep11.main.ui;

import be.swop.groep11.main.*;
import be.swop.groep11.main.controllers.AdvanceTimeController;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.IllegalCommandException;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    // TODO: documentatie van main methode... hoe?
    public static void main(String[] args) {

        // maak een nieuwe CommandLineInterface aan
        CommandLineInterface cli = new CommandLineInterface();

        // lees commando's
        cli.run();
    }

    /**
     * Constructor om een nieuwe commandline gebruikersinterface te maken.
     * Maakt een nieuw TaskMan object aan en initialiseert de controllers.
     */
    public CommandLineInterface() {
        this.br = new BufferedReader(new InputStreamReader(System.in));
        this.exit = false;

        // maak een nieuwe taskMan aan
        TaskMan taskMan = new TaskMan();
        ProjectRepository projectRepository = taskMan.getProjectRepository();

        // maak de controllers aan
        User user = new User("ROOT");
        this.projectController = new ProjectController(projectRepository, user, this);
//        this.taskController = new TaskController();
        this.advanceTimeController = new AdvanceTimeController(taskMan,this);
    }

    private void run(){
        try {

            while (! exit) {
                try {
                    String commandString = null;
                    commandString = br.readLine();
                    Command com = Command.getInput(commandString);
                    executeCommand(com);

                }catch (IllegalCommandException ec){
                    System.out.println(ec.getInput());
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
                System.out.println(Command.HELP.name());
                break;
            case NEWPROJECTS:
                getProjectController().createProject();
                break;
            case ADVANCETIME:
                getAdvanceTimeController().advanceTime();
                break;
            case UPDATETASK:
                System.out.println(Command.UPDATETASK.name());
                // TODO
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

    @Override
    public void printMessage(String message) {
        System.out.printf("\n" + message + "\n");
    }

    @Override
    public void printException(Exception e) {
        System.out.printf("\n" + e.getMessage() + "\n" + "Use case gestopt!" + "\n");
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
            System.out.println("Ongeldige invoer: moet een geheel getal zijn");
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
            System.out.println("Ongeldige invoer: moet een double zijn");
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
     * Implementeert requestDatum in UserInterface
     */
    @Override
    public LocalDateTime requestDatum(String request) throws CancelException {
        String input = requestInput(request + " formaat: yyyy-mm-dd hh:mm");
        checkCancel(input);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (! isValidDateTimeFormat(input, formatter)) {
            System.out.println("Ongeldig formaat");
            input = requestInput(request + " formaat: yyyy-mm-dd hh:mm");
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
        // TODO: meer info tonen
        String format = "%4s %-35s %-20s %n";
        System.out.printf(format, "nr.", "Naam", "Status");
        for (int i=0; i<projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf(format, i, project.getName(), project.getProjectStatus().name());
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
            throw new EmptyListException("Geen projecten aanwezig!");
        }
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
        // TODO: meer info tonen?
        String format = "%4s %-35s %-20s %n";
        System.out.printf(format, "nr.", "Omschrijving", "Status");
        for (int i=0; i<tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.printf(format, i, task.getDescription(), task.getStatus());
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
            throw new EmptyListException("Geen taken aanwezig!");
        }
        showTaskList(tasks);

        try {
            int nr = getNumberBetween(0, tasks.size());
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
        System.out.println("*** Project details ***");
        String format = "%-25s %s %n";
        System.out.printf(format, "Naam: ", project.getName());
        System.out.printf(format, "Beschrijving: ", project.getDescription());
        System.out.printf(format, "Status: ", project.getProjectStatus().name());
        System.out.printf(format, "Creation time: ",
                project.getCreationTime().getYear() + "-"
                +project.getCreationTime().getMonth() + "-"
                +project.getCreationTime().getDayOfMonth() + " "
                +project.getCreationTime().getHour() + ":"
                +project.getCreationTime().getMinute());
        System.out.printf(format, "Due time: ",
                project.getDueTime().getYear() + "-"
                        +project.getDueTime().getMonth() + "-"
                        +project.getDueTime().getDayOfMonth() + " "
                        +project.getDueTime().getHour() + ":"
                        +project.getDueTime().getMinute());
        // TODO
    }

    /**
     * Toont een tekstweergave van de details van een taak.
     * Implementeert showTaskDetails uit UserInterface
     */
    @Override
    public void showTaskDetails(Task task) {
        System.out.println("*** Taak details ***");
        String format = "%-25s %s %n";
        System.out.printf(format, "Beschrijving: ", task.getDescription());
        System.out.printf(format, "Status: ", task.getStatus().name());
        // TODO
    }

    private void checkCancel(String str) throws CancelException{
        if(Command.checkCancel(str)){
            throw new CancelException("Cancel nu!");
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
        System.out.println(request);
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
