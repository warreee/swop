package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.CreateProjectController;
import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.resource.IRequirementList;
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
    private UserInterface mockedUI;
    private CreateProjectController createProjectController;
    private LogonController logonController;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        projectRepository = new ProjectRepository(systemTime);
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8),mock(IRequirementList.class));
        mockedUI = mock(UserInterface.class);

        this.logonController = mock(LogonController.class);
        when(logonController.hasIdentifiedProjectManager()).thenReturn(true);

    }

    @Test
    public void createProject_validTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));

        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidNameTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("").thenReturn("Omschrijving");
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(IllegalArgumentException.class));

        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("Naam").thenReturn("");
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(IllegalArgumentException.class));

        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidCreationAndDueTimeTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mockedUI.requestDatum(anyString())).thenReturn(now.plusDays(1)).thenReturn(now);
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));

        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }

    @Test(expected = StopTestException.class)
    public void createProject_CancelNameTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenThrow(new CancelException("Cancel in test")).thenReturn("Omschrijving");
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));

        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }

    @Test(expected = StopTestException.class)
    public void createProject_CancelDescriptionTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("Naam").thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));

        //Cancel exception wordt opgevangen in de controller.
        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }
    @Test(expected = StopTestException.class)
    public void createProject_CancelCreateTimeTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mockedUI.requestDatum(anyString())).thenThrow(new CancelException("Cancel in test")).thenReturn(now.plusDays(1));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));

        //Cancel exception wordt opgevangen in de controller.
        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }
    @Test(expected = StopTestException.class)
    public void createProject_CancelDueTimeTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("Naam").thenReturn("Omschrijving");
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenThrow(new CancelException("Cancel in test"));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));

        //Cancel exception wordt opgevangen in de controller.
        this.createProjectController = new CreateProjectController(logonController,mockedUI);
        createProjectController.createProject();
    }

}
