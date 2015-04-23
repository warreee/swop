package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by Ronald on 17/04/2015.
 */
public class MainController extends AbstractController {

    private final SimulationController simulationController;
    private final ProjectController projectController;
    private ActionBehaviourMapping actionBehaviourMapping;
    private final AdvanceTimeController advanceTimeController;
    private final TaskController taskController;
    private final PlanningController planningController;

    public MainController(ActionBehaviourMapping actionBehaviourMapping, AdvanceTimeController advanceTimeController, SimulationController simulationController,
                          ProjectController projectController, TaskController taskController, PlanningController planningController, UserInterface userInterface) {
        super(userInterface);
        this.actionBehaviourMapping = actionBehaviourMapping;
        this.advanceTimeController = advanceTimeController;
        this.simulationController = simulationController;
        this.projectController = projectController;
        this.taskController = taskController;
        this.planningController = planningController;
    }

    @Override
    public void advanceTime() throws IllegalArgumentException {
        activate(advanceTimeController);
        advanceTimeController.advanceTime();
        deActivate(advanceTimeController);
    }

    @Override
    public void createProject() throws IllegalArgumentException {
        activate(projectController);
        projectController.createProject();
        deActivate(projectController);
    }

    @Override
    public void createTask() throws IllegalArgumentException {
        activate(taskController);
        taskController.createTask();
        deActivate(taskController);
    }

    @Override
    public void planTask() throws IllegalArgumentException {
        activate(taskController);
        planningController.planTask();
        deActivate(taskController);
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        activate(projectController);
        projectController.showProjects();
        deActivate(projectController);
    }

    @Override
    public void updateTask() throws IllegalArgumentException {
        activate(taskController);
        taskController.updateTask();
        deActivate(taskController);
    }


    @Override
    public void startSimulation() throws IllegalArgumentException {
        activate(simulationController);
        simulationController.startSimulation();
        //Mag niet deActivaten omdat de simulatie controller nog actief moet zijn, totdat end simulation is opgeroepen
        //simulationController.deActivate();
    }

    /**
     * Set's this controller on top of stack in UI.
     */
    protected void activate(AbstractController controller) {
        actionBehaviourMapping.activateController(controller);
    }

    /**
     * Removes this controller from the stack in UI.
     */
    protected void deActivate(AbstractController controller) {
        actionBehaviourMapping.deActivateController(controller);
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
