package be.swop.groep11.test.integration;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.User;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.ui.commands.CancelException;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

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
        projectRepository = new ProjectRepository();
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10),new User("TEST"));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));
    }

    @Test
    public void createProject_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("Project naam:")){
                    result = "Naam";
                } else if (request.contentEquals("Project beschrijving:")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Creation time:")){
                    result = now;
                } else if (request.contentEquals("Due time:")){
                    result = now.plusDays(1);
                }
                return result;
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidNameTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("Project naam:")){
                    result = "";
                } else if (request.contentEquals("Project beschrijving:")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Creation time:")){
                    result = now;
                } else if (request.contentEquals("Due time:")){
                    result = now.plusDays(1);
                }
                return result;
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("Project naam:")){
                    result = "Naam";
                } else if (request.contentEquals("Project beschrijving:")){
                    result = "";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Creation time:")){
                    result = now;
                } else if (request.contentEquals("Due time:")){
                    result = now.plusDays(1);
                }
                return result;
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidCreationAndDueTimeTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("Project naam:")){
                    result = "Naam";
                } else if (request.contentEquals("Project beschrijving:")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("Creation time:")){
                    result = now.plusDays(1);
                } else if (request.contentEquals("Due time:")){
                    result = now;
                }
                return result;
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.createProject();
    }

    @Test
    public void createProject_CancelTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = requestInput(request);
                if(request.contentEquals("Project naam:")){
                    result = "Naam";
                } else if (request.contentEquals("Project beschrijving:")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                requestInput(request);
                LocalDateTime result = null;
                if(request.contentEquals("Creation time:")){
                    result = now.plusDays(1);
                } else if (request.contentEquals("Due time:")){
                    result = now;
                }
                return result;
            }

            private String requestInput(String request)throws CancelException{
                throw new CancelException("Cancel");
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
}
