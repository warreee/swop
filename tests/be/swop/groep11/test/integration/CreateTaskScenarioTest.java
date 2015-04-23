package be.swop.groep11.test.integration;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TMSystem;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Ronald on 11/03/2015.
 */
public class CreateTaskScenarioTest {
    private LocalDateTime now;
    private ProjectRepository projectRepository;
    private TaskController taskController;
    private UserInterface mockedUI;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.mockedUI = mock(UserInterface.class);

        SystemTime systemTime = new SystemTime(now);
        ResourceManager resourceManager = new ResourceManager();
        projectRepository = new ProjectRepository(systemTime);

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10), new User("TEST"));
        this.taskController = new TaskController(projectRepository,systemTime,mockedUI);
    }

    @Test
    public void createTask_validTest() throws Exception {
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(50);

        when(mockedUI.requestNumber(anyString())).thenReturn(8);

        when(mockedUI.selectProjectFromList(anyString())).thenReturn(now.plusDays(1));


        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "beschrijving Test";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                return 50;
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                return 8;
            }

            @Override
            public void printMessage(String message) {
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.createTask();

    }

    @Test
    public void createTask_CancelBeschrijvingTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                throw new CancelException("Cancel");
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                return 50;
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                return 8;
            }

            @Override
            public void printMessage(String message) {
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }

        };
        TaskController taskController = new TaskController(repository,ui);
        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }

    @Test
    public void createTask_CancelDeviationTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "Beschrijving";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                throw new CancelException("Cancel");
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                return 8;
            }

            @Override
            public void printMessage(String message) {
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }

    @Test
    public void createTask_CancelDurationTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "Beschrijving";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                return 50;
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                throw new CancelException("Cancel");
            }

            @Override
            public void printMessage(String message) {
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }


    @Test (expected = StopTestException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                return 50;
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                return 8;
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDeviationTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "Beschrijving";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                return -50;
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                return 8;
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDurationTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "Beschrijving";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                return 50;
            }

            @Override
            public int requestNumber(String request) throws CancelException {
                return -1;
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.createTask();
    }

}
