package be.swop.groep11.test.integration;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.User;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class CreateTaskScenarioTest {
    private ProjectRepository repository;
    private LocalDateTime now;

    @Before
    public void setUp() throws Exception {
        repository = new ProjectRepository();
        now = LocalDateTime.now();
        repository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10),new User("TEST"));

    }

    @Test
    public void createTask_validTest() throws Exception {
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
        createTask(ui);
    }

    @Test (expected = CancelException.class)
    public void createTask_CancelBeschrijvingTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                requestInput();
                return "Beschrijving";
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

            private void requestInput() throws CancelException{
                throw new CancelException("Cancel");
            }
        };
        createTask(ui);
    }

    @Test (expected = CancelException.class)
    public void createTask_CancelDeviationTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                return "Beschrijving";
            }

            @Override
            public double requestDouble(String request) throws CancelException {
                cancel();
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

            private void cancel() throws CancelException{
                throw new CancelException("Cancel");
            }
        };
        createTask(ui);
    }

    @Test (expected = CancelException.class)
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
                cancel();
                return 8;
            }

            @Override
            public void printMessage(String message) {
            }

            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return projects.get(0);
            }

            private void cancel() throws CancelException{
                throw new CancelException("Cancel");
            }
        };
        createTask(ui);
    }


    @Test (expected = IllegalArgumentException.class)
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
        };
        createTask(ui);
    }

    @Test (expected = IllegalArgumentException.class)
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
        };
        createTask(ui);
    }

    @Test (expected = IllegalArgumentException.class)
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
        };
        createTask(ui);
    }

    private void createTask(EmptyTestUI ui){
        ImmutableList<Project> projects = repository.getProjects();

        //Step 2 & 3
        String description = ui.requestString("Beschrijving:");
        Double acceptableDeviation = ui.requestDouble("Aanvaardbare afwijking in procent:") / 100;
        Duration estimatedDuration = Duration.ofHours(Integer.valueOf(ui.requestNumber("Geschatte duur:")).longValue());
        ui.printMessage("Kies een project waartoe de taak moet behoren:");
        //Step 4
        Project project = ui.selectProjectFromList(projects);
        project.addNewTask(description, acceptableDeviation, estimatedDuration);

        //Check
        ImmutableList<Task> tasks = project.getTasks();
        Task task = tasks.get(tasks.size()-1);

        assertEquals(task.getDescription(),description);
        assertEquals(Double.valueOf(task.getAcceptableDeviation()),acceptableDeviation);
        assertEquals(task.getEstimatedDuration(),estimatedDuration);
    }
}
