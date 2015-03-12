package be.swop.groep11.test.integration;

import be.swop.groep11.main.*;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class UpdateTaskStatusScenarioTest {
    private ProjectRepository repository;
    private LocalDateTime now;

    @Before
    public void setUp() throws Exception {
        repository = new ProjectRepository();
        now = LocalDateTime.now();
        repository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10),new User("TEST"));

    }

    @Test
    public void updateTask_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException{
                Project project = repository.getProjects().get(0);
                project.addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

                return project.getTasks().get(0);

            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Starttijd:")){
                    result = now;
                } else if(request.contentEquals("Eindtijd:")){
                    result = now.plusDays(1);
                }

                return result;
            }

            @Override
            public String requestString(String request) throws CancelException {
                return "FINISHED";
            }

            @Override
            public void printMessage(String message) {
                super.printMessage(message);
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.updateTask();
    }

    @Test
    public void updateTask_CancelTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException{
                Project project = repository.getProjects().get(0);
                project.addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

                return project.getTasks().get(0);

            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Starttijd:")){
                    throw new CancelException("Cancel");
                } else if(request.contentEquals("Eindtijd:")){
                    result = now.plusDays(1);
                }

                return result;
            }

            @Override
            public String requestString(String request) throws CancelException {
                return "FINISHED";
            }

            @Override
            public void printMessage(String message) {
                super.printMessage(message);
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        //Cancel exception wordt opgevangen in de controller.
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidStartAndDueTimeTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException{
                Project project = repository.getProjects().get(0);
                project.addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

                return project.getTasks().get(0);
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Starttijd:")){
                    result = now.plusDays(1);
                } else if(request.contentEquals("Eindtijd:")){
                    result = now;
                }
                return result;
            }

            @Override
            public String requestString(String request) throws CancelException {
                return "FINISHED";
            }

            @Override
            public void printMessage(String message) {
                super.printMessage(message);
            }

            @Override
            public void printException(Exception e) {
               throw new StopTestException("Cancel");
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidNewStatusUnavailableTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException{
                Project project = repository.getProjects().get(0);
                project.addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

                return project.getTasks().get(0);

            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Starttijd:")){
                    result = now.plusDays(1);
                } else if(request.contentEquals("Eindtijd:")){
                    result = now;
                }
                return result;
            }

            @Override
            public String requestString(String request) throws CancelException {
                return "UNAVAILABLE";
            }

            @Override
            public void printMessage(String message) {
                super.printMessage(message);
            }

            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.updateTask();
    }

    @Test (expected = StopTestException.class)
    public void updateTask_invalidNewStatusAvailableTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException{
                Project project = repository.getProjects().get(0);
                project.addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

                return project.getTasks().get(0);

            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Starttijd:")){
                    result = now.plusDays(1);
                } else if(request.contentEquals("Eindtijd:")){
                    result = now;
                }
                return result;
            }

            @Override
            public String requestString(String request) throws CancelException {
                return "AVAILABLE";
            }

            @Override
            public void printMessage(String message) {
                super.printMessage(message);
            }

            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        TaskController taskController = new TaskController(repository,ui);
        taskController.updateTask();
    }


}