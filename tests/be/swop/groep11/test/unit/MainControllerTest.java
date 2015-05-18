package be.swop.groep11.test.unit;

import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 1/05/2015.
 */
public class MainControllerTest {

    private TaskController taskController;
    private ShowProjectsController showProjectsController;
    private AdvanceTimeController advanceTimeController;
    private SimulationController simulationController;
    private PlanningController planningController;
    private LogonController logonController;
    private UserInterface mockedUI;
    private MainController main;
    private ControllerStack abmMock;
    private DelegateTaskController delegateTaskController;

    @Before
    public void setUp() throws Exception {
        //maak een nieuwe system aan
        LocalDateTime now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        ResourceManager resourceManager = new ResourceManager();
        BranchOffice branchOffice = mock(BranchOffice.class);
        ProjectRepository projectRepository = new ProjectRepository(systemTime);

        abmMock = mock(ControllerStack.class);
        this.mockedUI = mock(UserInterface.class);
        mockedUI.setControllerStack(abmMock);


        taskController = mock(TaskController.class);
        showProjectsController = mock(ShowProjectsController.class);
        logonController = mock(LogonController.class);
        delegateTaskController = mock(DelegateTaskController.class);
        advanceTimeController = mock(AdvanceTimeController.class);

        simulationController = mock(SimulationController.class);
        planningController = mock(PlanningController.class);
        main = new MainController(
                mockedUI );
    }

    @Test
    public void advanceTime() throws Exception {
        //stubbing
        main.advanceTime();

        verify(abmMock).activateController(advanceTimeController);
        verify(advanceTimeController).advanceTime();
        verify(abmMock).deActivateController(advanceTimeController);

    }

    @Test
    public void createTask() throws Exception {
        main.createTask();

        verify(abmMock).activateController(taskController);
        verify(taskController).createTask();
        verify(abmMock).deActivateController(taskController);

    }

    @Test
    public void planTask() throws Exception {
        main.planTask();

        verify(abmMock).activateController(planningController);
        verify(planningController).planTask();
        verify(abmMock).deActivateController(planningController);

    }

    @Test
    public void createProject() throws Exception {
        main.createProject();


        verify(abmMock).activateController(showProjectsController);
        verify(showProjectsController).createProject();
        verify(abmMock).deActivateController(showProjectsController);

    }

    @Test
    public void showProjects() throws Exception {
        main.showProjects();

        verify(abmMock).activateController(showProjectsController);
        verify(showProjectsController).showProjects();
        verify(abmMock).deActivateController(showProjectsController);

    }

    @Test
    public void updateTask() throws Exception {
        main.updateTask();

        verify(abmMock).activateController(taskController);
        verify(taskController).updateTask();
        verify(abmMock).deActivateController(taskController);

    }

    @Test
    public void startSimulation() throws Exception {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                abmMock.deActivateController(simulationController);
                return null;
            }
        }).when(simulationController).cancel();

        main.startSimulation();
        simulationController.cancel();

        verify(abmMock).activateController(simulationController);
        verify(simulationController).startSimulation();
        verify(abmMock).deActivateController(simulationController);

    }
}