package be.swop.groep11.test.unit;

import be.swop.groep11.main.controllers.AdvanceTimeController;
import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;

/**
 * Created by Ronald on 1/05/2015.
 */
public class AbstractControllerTest {

    private LocalDateTime now;
    private ProjectRepository projectRepository;
    private User user;
    private UserInterface mockedUI;
    private ProjectController projectController;
    private SystemTime systemTime;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        projectRepository = new ProjectRepository(systemTime, branchOffice);
        user = new User("Test");

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));
        mockedUI = mock(UserInterface.class);
        this.projectController = new ProjectController(projectRepository,mockedUI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void IllegalAdvanceTimeCall() throws Exception {
        projectController.advanceTime();
    }
    @Test(expected = IllegalArgumentException.class)
    public void IllegalStartSimulationCall() throws Exception {
        projectController.startSimulation();
    }
    @Test(expected = IllegalArgumentException.class)
    public void IllegalUpdateTaskCall() throws Exception {
        projectController.updateTask();
    }
    @Test(expected = IllegalArgumentException.class)
    public void IllegalCreateTaskCall() throws Exception {
        projectController.createTask();
    }
    @Test(expected = IllegalArgumentException.class)
    public void IllegalPlanTaskCall() throws Exception {
        projectController.planTask();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testIllegalCreateProjectCall() throws Exception {
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(systemTime, mockedUI);
        advanceTimeController.createProject();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalShowProjectsCall() throws Exception {
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(systemTime, mockedUI);
        advanceTimeController.showProjects();

    }
}