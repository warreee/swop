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

    private ProjectController projectsController;
    private TaskController taskController;

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
            }

            br.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommandLineInterface() {

        // maak een nieuwe taskMan aan
        TaskMan taskMan = new TaskMan();
        ProjectRepository projectRepository = taskMan.getProjectRepository();

        // maak de controllers aan
        projectsController = new ProjectController(projectRepository);
        taskController = new TaskController();
    }

    @Override
    public void showProjectList(ImmutableList<Project> projects) {
        String format = "%4s %-35s %-20s %n";
        System.out.printf(format, "nr.", "Naam", "Status");
        for (int i=0; i<projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf(format, i, project.getName(), project.getProjectStatus().name());
        }
    }

    @Override
    public int selectProjectFromList(ImmutableList<Project> projects) throws IOException {
        showProjectList(projects);
        Scanner in = new Scanner(System.in);
        int nr = in.nextInt();
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

}
