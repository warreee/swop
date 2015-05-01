package be.swop.groep11.test.integration;

import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Ronald on 1/05/2015.
 */
public class MainControllerTest {

    private TaskController taskController;
    private ProjectController projectController;
    private AdvanceTimeController advanceTimeController;
    private SimulationController simulationController;
    private PlanningController planningController;
    private UserInterface mockedUI;
    private MainController main;
    private ActionBehaviourMapping abmMock;

    @Before
    public void setUp() throws Exception {
        //maak een nieuwe system aan
        LocalDateTime now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        ResourceManager resourceManager = new ResourceManager();
        ProjectRepository projectRepository = new ProjectRepository(systemTime);

        abmMock = mock(ActionBehaviourMapping.class);
        this.mockedUI = mock(UserInterface.class);
        mockedUI.setActionBehaviourMapping(abmMock);


        taskController = new TaskController(projectRepository, systemTime,mockedUI, resourceManager );
        projectController = new ProjectController(projectRepository, mockedUI );
        advanceTimeController = new AdvanceTimeController( systemTime, mockedUI);
        simulationController = new SimulationController(abmMock, projectRepository, mockedUI);
        planningController = new PlanningController(projectRepository,resourceManager, systemTime, mockedUI);
        main = new MainController(abmMock, advanceTimeController,simulationController,projectController,taskController,planningController,mockedUI );
    }

    @Test
    public void advanceTime() throws Exception {
        //stubbing
//        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
//        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        verify(abmMock).activateController(advanceTimeController);
        verify(abmMock).deActivateController(advanceTimeController);

    }

    @Test
    public void createTask() throws Exception {
        verify(abmMock).activateController(taskController);
        verify(abmMock).deActivateController(taskController);

    }

    @Test
    public void planTask() throws Exception {
        verify(abmMock).activateController(taskController);
        verify(abmMock).deActivateController(taskController);

    }

    @Test
    public void createProject() throws Exception {

        verify(abmMock).activateController(projectController);
        verify(abmMock).deActivateController(projectController);

    }

    @Test
    public void showProjects() throws Exception {
        verify(abmMock).activateController(projectController);
        verify(abmMock).deActivateController(projectController);

    }

    @Test
    public void updateTask() throws Exception {
        verify(abmMock).activateController(taskController);
        verify(abmMock).deActivateController(taskController);

    }

    @Test
    public void startSimulation() throws Exception {
        verify(abmMock).activateController(simulationController);
//        verify(abmMock).deActivateController(taskController);

    }
}