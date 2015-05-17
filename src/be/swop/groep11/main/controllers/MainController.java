package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Controller die gebruikt wordt wanneer er nog geen usecase geselecteerd is.
 */
public class MainController extends AbstractController {

    private final SimulationController simulationController;
    private final ShowProjectsController showProjectsController;
    private ControllerStack controllerStack;
    private final AdvanceTimeController advanceTimeController;
    private final TaskController taskController;
    private final PlanningController planningController;
    private final LogonController logonController;
    private final DelegateTaskController delegateTaskController;

    /**
     * Maakt een nieuwe instantie aan van deze controller.
     * @param controllerStack Mapt per controller de beschikbare acties op de juiste actie.
     * @param advanceTimeController De controller voor de usecase advance time.
     * @param simulationController De controller voor de usecase simulate.
     * @param showProjectsController De controller voor de usecase new project.
     * @param taskController De controller voor de usecases new task en update task.
     * @param planningController De controller voor de usecase planning.
     * @param userInterface Een concrete implementatie van de UserInterfaceInterface.
     */
    public MainController(ControllerStack controllerStack, AdvanceTimeController advanceTimeController,
                          SimulationController simulationController, ShowProjectsController showProjectsController,
                          TaskController taskController, PlanningController planningController,
                          LogonController logonController, DelegateTaskController delegateTaskController, UserInterface userInterface) {
        super(userInterface);
        this.controllerStack = controllerStack;
        this.advanceTimeController = advanceTimeController;
        this.simulationController = simulationController;
        this.showProjectsController = showProjectsController;
        this.taskController = taskController;
        this.planningController = planningController;
        this.logonController = logonController;
        this.delegateTaskController = delegateTaskController;
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
        activate(showProjectsController);
        showProjectsController.createProject();
        deActivate(showProjectsController);
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
        activate(showProjectsController);
        showProjectsController.showProjects();
        deActivate(showProjectsController);
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
     * Voert de usecase logon uit.
     * @throws IllegalArgumentException Wordt gegooid als er ergens een fout optreed die niet herstelbaar is.
     */
    @Override
    public void logon() throws IllegalArgumentException {
        activate(logonController);
        logonController.logon();
        deActivate(logonController);
    }

    /**
     * Positioneerd deze controller boven op de stack in de UI.
     */
    protected void activate(AbstractController controller) {
        controllerStack.activateController(controller);
    }

    /**
     * Verwijderd deze controller van de stack in de UI.
     */
    protected void deActivate(AbstractController controller) {
        controllerStack.deActivateController(controller);
    }

}
