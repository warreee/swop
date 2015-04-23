package be.swop.groep11.test.integration;

import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
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

    private ImmutableList<Project> projects;
    private ImmutableList<Task> tasks;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        systemTime = new SystemTime(now);
        repository = new ProjectRepository(systemTime);
        repository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10));
        repository.getProjects().get(0).addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

        this.mockedUI = mock(UserInterface.class);
        this.taskController = new TaskController(repository,systemTime,mockedUI);

        projects = repository.getProjects();
        tasks = projects.get(0).getTasks();
    }

    @Test
    public void updateTask_validTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        when(mockedUI.requestString(anyString())).thenReturn("EXECUTE");

        taskController = new TaskController(repository,systemTime,mockedUI);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_CancelTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenThrow(new CancelException("cancel in test")).thenReturn(now.plusDays(1));
        when(mockedUI.requestString(anyString())).thenReturn("FINISHED");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController = new TaskController(repository,systemTime,mockedUI);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidStartAndDueTimeTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now.plusDays(1)).thenReturn(now);
        when(mockedUI.requestString(anyString())).thenReturn("FINISHED");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController = new TaskController(repository,systemTime,mockedUI);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidNewStatusUnavailableTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        when(mockedUI.requestString(anyString())).thenReturn("UNAVAILABLE");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController = new TaskController(repository,systemTime,mockedUI);
        taskController.updateTask();    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidNewStatusAvailableTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.requestDatum(anyString())).thenReturn(now).thenReturn(now.plusDays(1));
        when(mockedUI.requestString(anyString())).thenReturn("AVAILABLE");
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController = new TaskController(repository,systemTime,mockedUI);
        taskController.updateTask();
    }


}