package be.swop.groep11.test.integration;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.User;
import be.swop.groep11.main.ui.commands.CancelException;
import org.junit.Before;
import org.junit.Test;

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
    }

    @Test
    public void createProject_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("name")){
                    result = "Naam";
                } else if (request.contentEquals("desc")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("create")){
                    result = now;
                } else if (request.contentEquals("due")){
                    result = now.plusDays(1);
                }
                return result;
            }
        };
        createProject(ui);
    }
    @Test (expected = IllegalArgumentException.class)
    public void createProject_invalidNameTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("name")){
                    result = "";
                } else if (request.contentEquals("desc")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("create")){
                    result = now;
                } else if (request.contentEquals("due")){
                    result = now.plusDays(1);
                }
                return result;
            }
        };
        createProject(ui);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("name")){
                    result = "Naam";
                } else if (request.contentEquals("desc")){
                    result = "";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("create")){
                    result = now;
                } else if (request.contentEquals("due")){
                    result = now.plusDays(1);
                }
                return result;
            }
        };
        createProject(ui);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createProject_invalidCreationAndDueTimeTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = "";
                if(request.contentEquals("name")){
                    result = "Naam";
                } else if (request.contentEquals("desc")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                LocalDateTime result = null;
                if(request.contentEquals("create")){
                    result = now.plusDays(1);
                } else if (request.contentEquals("due")){
                    result = now;
                }
                return result;
            }
        };
        createProject(ui);
    }

    @Test (expected = CancelException.class)
    public void createProject_CancelTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public String requestString(String request) throws CancelException {
                String result = requestInput(request);
                if(request.contentEquals("name")){
                    result = "Naam";
                } else if (request.contentEquals("desc")){
                    result = "Omschrijving";
                }
                return result;
            }

            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                requestInput(request);
                LocalDateTime result = null;
                if(request.contentEquals("create")){
                    result = now.plusDays(1);
                } else if (request.contentEquals("due")){
                    result = now;
                }
                return result;
            }

            private String requestInput(String request)throws CancelException{
                throw new CancelException("Cancel");
            }
        };
        createProject(ui);
    }


    private void createProject(EmptyTestUI ui){
        //Step 3
        String projectName = ui.requestString("name");
        String description = ui.requestString("desc");

        LocalDateTime creationTime = ui.requestDatum("create");
        LocalDateTime dueTime = ui.requestDatum("due");
        //Step 4
        projectRepository.addNewProject(projectName, description ,creationTime, dueTime, user);

        //TODO check if added correctly
    }


}
