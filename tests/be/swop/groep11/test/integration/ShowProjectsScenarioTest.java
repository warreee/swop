package be.swop.groep11.test.integration;

import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Ronald on 11/03/2015.
 */
public class ShowProjectsScenarioTest {

    private ProjectRepository projectRepository;
    private User user;

    @Before
    public void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        ResourceManager resourceManager = new ResourceManager();
        projectRepository = new ProjectRepository(systemTime,resourceManager);
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10),new User("TEST"));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));
    }

    @Test
    public void showProjects_validTest() throws Exception {
        ImmutableList<Project> projects = projectRepository.getProjects();
        ImmutableList<Task> tasks = projects.get(0).getTasks();

        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mocked.selectProjectFromList(projects)).thenReturn(projects.get(0));

        ProjectController projectController = new ProjectController(projectRepository, user,mocked );
        projectController.showProjects();
    }

    @Test
    public void showProjects_CancelSelectProjectTest() throws Exception {
        ImmutableList<Project> projects = projectRepository.getProjects();
        ImmutableList<Task> tasks = projects.get(0).getTasks();

        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mocked.selectProjectFromList(projects)).thenThrow(new CancelException("Cancel in Test"));

        //Cancel exception wordt opgevangen in de controller.
        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        projectController.showProjects();
    }

    @Test
    public void showProjects_CancelSelectTaskTest() throws Exception {
        ImmutableList<Project> projects = projectRepository.getProjects();
        ImmutableList<Task> tasks = projects.get(0).getTasks();

        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.selectTaskFromList(tasks)).thenThrow(new CancelException("Cancel in test"));
        when(mocked.selectProjectFromList(projects)).thenReturn(projects.get(0));


        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        //Cancel exception wordt opgevangen in de controller.
        projectController.showProjects();
    }



}
