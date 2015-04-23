package be.swop.groep11.test.integration;

import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class CreateTaskScenarioTest {
    private LocalDateTime now;
    private ProjectRepository projectRepository;
    private TaskController taskController;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private ImmutableList<Project> projects;
    private ImmutableList<Task> tasks;
    private ResourceManager resourceManager;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.mockedUI = mock(UserInterface.class);

        systemTime = new SystemTime(now);
        projectRepository = new ProjectRepository(systemTime);
        resourceManager = new ResourceManager();

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10));
        this.taskController = new TaskController(projectRepository,systemTime,mockedUI, resourceManager);

        this.projects = projectRepository.getProjects();
        this.tasks = projects.get(0).getTasks();
    }

    @Test
    public void createTask_validTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));

        taskController.createTask();
    }

    @Test(expected = StopTestException.class)
    public void createTask_CancelBeschrijvingTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }

    @Test(expected = StopTestException.class)
    public void createTask_CancelDeviationTest() throws Exception {
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }

    @Test(expected = StopTestException.class)
    public void createTask_CancelDurationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }


    @Test (expected = StopTestException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDeviationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(-50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDurationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(-8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

}
