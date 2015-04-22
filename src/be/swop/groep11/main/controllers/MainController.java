package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by Ronald on 17/04/2015.
 */
public class MainController extends AbstractController {

    private final SimulationController simulationController;
    private final ProjectController projectController;
    private final AdvanceTimeController advanceTimeController;
    private final TaskController taskController;
    private final PlanningController planningController;

    public MainController(UserInterface userInterface, AdvanceTimeController advanceTimeController, ProjectRepository projectRepository,
                          SimulationController simulationController, ProjectController projectController, TaskController taskController,PlanningController planningController) {
        super(userInterface);
        this.advanceTimeController = advanceTimeController;
        this.simulationController = simulationController;
        this.projectController = projectController;
        this.taskController = taskController;
        this.planningController = planningController;
    }

    @Override
    public void advanceTime() throws IllegalArgumentException {
        advanceTimeController.activate();
        advanceTimeController.advanceTime();
        advanceTimeController.deActivate();
    }

    @Override
    public void createProject() throws IllegalArgumentException {
        projectController.activate();
        projectController.createProject();
        projectController.deActivate();
    }

    @Override
    public void createTask() throws IllegalArgumentException {
        taskController.activate();
        taskController.createTask();
        taskController.deActivate();
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        projectController.activate();
        projectController.showProjects();
        projectController.deActivate();
    }

    @Override
    public void updateTask() throws IllegalArgumentException {
        taskController.activate();
        taskController.updateTask();
        taskController.deActivate();
    }


    @Override
    public void startSimulation() throws IllegalArgumentException {
        simulationController.activate();
        simulationController.startSimulation();
        //Mag niet deActivaten omdat de simulatie controller nog actief moet zijn, totdat end simulation is opgeroepen
        //simulationController.deActivate();
    }

    private AdvanceTimeController getAdvanceTimeController() {
        return advanceTimeController;
    }
    private ProjectController getProjectController() {
        return projectController;
    }
    private SimulationController getSimulationController() {
        return simulationController;
    }
    private TaskController getTaskController() {
        return taskController;
    }
    private PlanningController getPlanningController() {
        return planningController;
    }
}
