package be.swop.groep11.main.ui;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.User;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Commandline gebruikersinterface die UserInterface implementeert.
 */
public class CommandLineInterface implements UserInterface {

    public static void main(String[] args) {

        // maak een nieuwe CommandLineInterface aan
        CommandLineInterface cli = new CommandLineInterface();

        // TODO: add Controllers

        String format = "%4s %-35s %-20s %n";
        System.out.printf(format, 1, "Test taak", "STATUS");

        // test opstelling
        /*ProjectRepository repository = new ProjectRepository();
        User user = new User("Test gebruiker");
        repository.addNewProject("Test project 1", "Beschrijving van project 1.", LocalDateTime.now(), LocalDateTime.now().plusDays(14), user);
        repository.addNewProject("Test project 2", "Beschrijving van project 2.", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(6), user);*/
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
    public int selectProjectFromList(ImmutableList<Project> projects) {
        return 0;
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
