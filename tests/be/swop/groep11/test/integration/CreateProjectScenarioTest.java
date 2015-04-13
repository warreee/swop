package be.swop.groep11.test.integration;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.TMSystem;
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
        projectRepository = new TMSystem().getProjectRepository();
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10),new User("TEST"));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));
    }

    @Test
    public void createProject_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {

            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "Naam";
                } else if (i==1){
                    result = "Omschrijving";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now;
                } else if (j==1){
                    result = now.plusDays(1);
                }
                j++;
                return result;
            }
        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        projectController.createProject();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidNameTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {

            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "";
                } else if (i==1){
                    result = "Omschrijving";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now;
                } else if (j==1){
                    result = now.plusDays(1);
                }
                j++;
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
            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "Naam";
                } else if (i==1){
                    result = "";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now;
                } else if (j==1){
                    result = now.plusDays(1);
                }
                j++;
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
            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "Naam";
                } else if (i==1){
                    result = "Omschrijving";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now.plusDays(1);
                } else if (j==1){
                    result = now;
                }
                j++;
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
    public void createProject_CancelNameTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    throw new CancelException("Cancel");
                } else if (i==1){
                    result = "Omschrijving";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now;
                } else if (j==1){
                    result = now.plusDays(1);
                }
                j++;
                return result;
            }



        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }

    @Test
    public void createProject_CancelDescriptionTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "ProjectNaam";
                } else if (i==1){
                    throw new CancelException("Cancel");
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now;
                } else if (j==1){
                    result = now.plusDays(1);
                }
                j++;
                return result;
            }



        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
    @Test
    public void createProject_CancelCreateTimeTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "ProjectNaam";
                } else if (i==1){
                    result = "Omschrijving";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    throw new CancelException("Cancel");
                } else if (j==1){
                    result = now.plusDays(1);
                }
                j++;
                return result;
            }



        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
    @Test
    public void createProject_CancelDueTimeTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            private int i = 0;
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(i==0){
                    result = "ProjectNaam";
                } else if (i==1){
                    result = "Omschrijving";
                }
                i++;
                return result;
            }

            private int j = 0;
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(j==0){
                    result = now;
                } else if (j==1){
                    throw new CancelException("Cancel");
                }
                j++;
                return result;
            }



        };
        ProjectController projectController = new ProjectController(projectRepository,user,ui);
        //Cancel exception wordt opgevangen in de controller.
        projectController.createProject();
    }
}
