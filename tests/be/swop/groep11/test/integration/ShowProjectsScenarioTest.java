package be.swop.groep11.test.integration;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.User;
import be.swop.groep11.main.TMSystem;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by Ronald on 11/03/2015.
 */
public class ShowProjectsScenarioTest {

    private LocalDateTime now;
    private ProjectRepository projectRepository;
    private User user;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        projectRepository = new TMSystem().getProjectRepository();
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10),new User("TEST"));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));
    }

    @Test
    public void showProjects_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
                return projectRepository.getProjects().get(0).getTasks().get(0);
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projectRepository.getProjects().get(0);
            }

        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.showProjects();
    }

    @Test
    public void showProjects_CancelSelectProjectTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
                return projectRepository.getProjects().get(0).getTasks().get(0);
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                throw new CancelException("Cancel");
            }
        };
        //Cancel exception wordt opgevangen in de controller.
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.showProjects();
    }

    @Test
    public void showProjects_CancelSelectTaskTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
                throw new CancelException("Cancel");
            }
            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projectRepository.getProjects().get(0);
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        //Cancel exception wordt opgevangen in de controller.
        projectController.showProjects();
    }



}
