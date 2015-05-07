package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Controller die gebruikt wordt wanneer er nog geen usecase geselecteerd is.
 */
public class MainController extends AbstractController {

    private final SimulationController simulationController;
    private final ProjectController projectController;
    private ActionBehaviourMapping actionBehaviourMapping;
    private final AdvanceTimeController advanceTimeController;
    private final TaskController taskController;
    private final PlanningController planningController;

    /**
     * Maakt een nieuwe instantie aan van deze controller.
     * @param actionBehaviourMapping Mapt per controller de beschikbare acties op de juiste actie.
     * @param advanceTimeController De controller voor de usecase advance time.
     * @param simulationController De controller voor de usecase simulate.
     * @param projectController De controller voor de usecase new project.
     * @param taskController De controller voor de usecases new task en update task.
     * @param planningController De controller voor de usecase planning.
     * @param userInterface Een concrete implementatie van de UserInterfaceInterface.
     */
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

    /**
     * Voert de usecase advanceTime uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void advanceTime() throws IllegalArgumentException {
        activate(advanceTimeController);
        advanceTimeController.advanceTime();
        deActivate(advanceTimeController);
    }

    /**
     * Voert de usecase createProject uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void createProject() throws IllegalArgumentException {
        activate(projectController);
        projectController.createProject();
        deActivate(projectController);
    }

    /**
     * Voert de usecase createTask uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void createTask() throws IllegalArgumentException {
        activate(taskController);
        taskController.createTask();
        deActivate(taskController);
    }

    /**
     * Voer de usecase planTask uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void planTask() throws IllegalArgumentException {
        activate(planningController);
        planningController.planTask();
        deActivate(planningController);
    }

    /**
     * Voert de usecase showProjects uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void showProjects() throws IllegalArgumentException {
        activate(projectController);
        projectController.showProjects();
        deActivate(projectController);
    }

    /**
     * Voert de usecase updateTask uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void updateTask() throws IllegalArgumentException {
        activate(taskController);
        taskController.updateTask();
        deActivate(taskController);
    }

    /**
     * Voert de usecase simulation uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void startSimulation() throws IllegalArgumentException {
        activate(simulationController);
        simulationController.startSimulation();
        //Mag niet deActivaten omdat de simulatie controller nog actief moet zijn, totdat end simulation is opgeroepen
        //simulationController.deActivate();
    }

    /**
     * Positioneerd deze controller boven op de stack in de UI.
     */
    protected void activate(AbstractController controller) {
        actionBehaviourMapping.activateController(controller);
    }

    /**
     * Verwijderd deze controller van de stack in de UI.
     */
    protected void deActivate(AbstractController controller) {
        actionBehaviourMapping.deActivateController(controller);
    }

}
