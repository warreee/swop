package be.swop.groep11.main.ui;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskMan;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.ui.commands.Command;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

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

    // TODO: documentatie van main methode... hoe?
    public static void main(String[] args) {

        // maak een nieuwe CommandLineInterface aan
        CommandLineInterface cli = new CommandLineInterface();

        // lees commando's

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            boolean exit = false;
            while (! exit) {
                String commandString = br.readLine();
                Command command = Command.getCommand(commandString);
                executeCommand(command, cli);
            }

            br.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(Command command, CommandLineInterface cli) {
        switch (command) {
            case SHOWPROJECTS:
                 cli.getProjectController().showProjects();
                 break;
        }
    }

    /**
     * Constructor om een nieuwe commandline gebruikersinterface te maken.
     * Maakt een nieuw TaskMan object aan en initialiseert de controllers.
     */
    public CommandLineInterface() {

        // maak een nieuwe taskMan aan
        TaskMan taskMan = new TaskMan();
        ProjectRepository projectRepository = taskMan.getProjectRepository();

        // maak de controllers aan
        projectController = new ProjectController(projectRepository, this);
        taskController = new TaskController();
    }

    /**
     * Toont een tekstweergave van een lijst projecten.
     * Implementeert showProjectList in UserInterface
     */
    @Override
    public void showProjectList(ImmutableList<Project> projects) {
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
    public int selectProjectFromList(ImmutableList<Project> projects) throws IOException {
        showProjectList(projects);
        int nr = Integer.parseInt(br.readLine());
        return nr;
    }

    @Override
    public Map<String, String> showForm(String... fields) {
        return null;
    }

    @Override
    public void showTaskList(ImmutableList<Task> tasks) {

    }

    @Override
    public int selectTaskFromList(ImmutableList<Task> tasks) {
        return 0;
    }

    @Override
    public void showProjectDetails(Project project) {

    }

    @Override
    public void showTaskDetails(Task task) {

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
}
