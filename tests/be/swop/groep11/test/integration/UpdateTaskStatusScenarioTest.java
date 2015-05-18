package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class UpdateTaskStatusScenarioTest {
    private ProjectRepository repository;
    private LocalDateTime now;
    private SystemTime systemTime;
    private TaskController taskController;
    private UserInterface mockedUI;
    private ResourceManager resourceManager;

    private ImmutableList<Project> projects;
    private ImmutableList<Task> tasks;
    private LogonController logonController;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        systemTime = new SystemTime(now);
        resourceManager = new ResourceManager();
        BranchOffice branchOffice = mock(BranchOffice.class);
        repository = new ProjectRepository(systemTime);
        repository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10));
        repository.getProjects().get(0).addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8),mock(IRequirementList.class));

        this.mockedUI = mock(UserInterface.class);

        this.logonController = mock(LogonController.class);
        when(logonController.hasIdentifiedDeveloper()).thenReturn(true);

        this.taskController = new TaskController(logonController,systemTime,mockedUI);

        projects = repository.getProjects();
        tasks = projects.get(0).getTasks();
    }

    @Test
    public void updateTask_validTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        when(mockedUI.selectFromList(anyListOf(String.class), anyObject())).thenReturn("EXECUTE");

        this.taskController = new TaskController(logonController,systemTime,mockedUI);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_CancelTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenThrow(new CancelException("cancel in test")).thenReturn(now.plusDays(1));
        when(mockedUI.selectFromList(anyListOf(String.class), anyObject())).thenThrow(new CancelException("cancel in test")).thenReturn("Niks doen");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        this.taskController = new TaskController(logonController,systemTime,mockedUI);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidStartAndDueTimeTest() throws Exception {
        //stubbing
        projects.get(0).addNewTask("description", 0.1, Duration.ofHours(1),mock(IRequirementList.class));
        tasks = projects.get(0).getTasks();
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now.plusDays(1)).thenReturn(now);
        when(mockedUI.selectFromList(anyListOf(String.class), anyObject())).thenReturn("EXECUTE").thenReturn("FINISH");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        this.taskController = new TaskController(logonController,systemTime,mockedUI);
        taskController.updateTask();
        taskController.updateTask();
    }

    /*
    Niet meer nodig omdat gebruiker nu alleen uit "FAIL", "FINISH", "EXECUTE", "Niks doen" kan kiezen?

    @Test (expected = StopTestException.class)
    public void updateTask_invalidNewStatusUnavailableTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        when(mockedUI.requestString(anyString())).thenReturn("UNAVAILABLE");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController = new TaskController(repository,systemTime,mockedUI, resourceManager);
        taskController.updateTask();
    }
    */

    /*
    Niet meer nodig omdat gebruiker nu alleen uit "FAIL", "FINISH", "EXECUTE", "Niks doen" kan kiezen?

    @Test (expected = StopTestException.class)
    public void updateTask_invalidNewStatusAvailableTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        when(mockedUI.requestString(anyString())).thenReturn("AVAILABLE");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController = new TaskController(repository,systemTime,mockedUI, resourceManager);
        taskController.updateTask();
    }
    */


}