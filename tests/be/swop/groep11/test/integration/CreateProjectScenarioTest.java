package be.swop.groep11.test.integration;

import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class CreateProjectScenarioTest {
    private LocalDateTime now;
    private ProjectRepository projectRepository;
    private User user;
    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        ResourceManager resourceManager = new ResourceManager();
        projectRepository = new ProjectRepository(systemTime,resourceManager);
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10), new User("TEST"));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));
    }

    @Test
    public void createProject_validTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString("some arg")).thenReturn("Naam").thenReturn("Omschrijving");
        when(mocked.requestDatum("some arg")).thenReturn(now).thenReturn(now.plusDays(1));

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidNameTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenReturn("").thenReturn("Omschrijving");
        when(mocked.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Cancel")).when(mocked).printException(any());

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mocked.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Cancel")).when(mocked).printException(any());

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidCreationAndDueTimeTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mocked.requestDatum(anyString())).thenReturn(now.plusDays(1)).thenReturn(now);
        doThrow(new StopTestException("Cancel")).when(mocked).printException(any());

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        projectController.createProject();
    }

    @Test
    public void createProject_CancelNameTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenThrow(new CancelException("Cancel in test")).thenReturn("Omschrijving");
        when(mocked.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }

    @Test
    public void createProject_CancelDescriptionTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenReturn("Naam").thenThrow(new CancelException("Cancel in test"));
        when(mocked.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
    @Test
    public void createProject_CancelCreateTimeTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mocked.requestDatum(anyString())).thenThrow(new CancelException("Cancel in test")).thenReturn(now.plusDays(1));

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
    @Test
    public void createProject_CancelDueTimeTest() throws Exception {
        UserInterface mocked = mock(UserInterface.class);
        //stubbing
        when(mocked.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mocked.requestDatum(anyString())).thenReturn(now).thenThrow(new CancelException("Cancel in test"));

        ProjectController projectController = new ProjectController(projectRepository,user,mocked);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
}
