package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.IProjectRepositoryMemento;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Bevat de stappen om de use case "Running a simulation" uit te voeren.
 * Dit wordt gedaan door de commando's "Start Simulation" en "End Simulation" af te handelen.
 */
public class SimulationController extends AbstractController {
    private LogonController logonController;

    /*
        * De te ondersteunen Use Case's:
        * Create Task
        * Plan Task
        * Delegate Task
        * Resolve Conflict
        * Show Projects
        * */
    public SimulationController (LogonController logonController, UserInterface userInterface) {
        super(userInterface);
        this.logonController = logonController;
        //Zelfde project repository als alle andere controllers, geen nood om actions(commands) te deligeren via simulatiecontroller.
    }
    //Store initial state
    private IProjectRepositoryMemento originalState;


    /**
     * Voert de stappen voor "Start Simulation" uit.
     */
    public void startSimulation() {
        // hou de huidige state van projectRepository bij
        BranchOffice bo = logonController.getBranchOffice();
        ProjectRepository projectRepository = bo.getProjectRepository();

        storeState(projectRepository);
        UserInterface ui = this.getUserInterface();
        ui.showHelp(this);
    }

    /**
     * Maakt de veranderingen die tijdens de simulatie gebeurd zijn defenitief.
     */
    public void realize() {
        //projectRepository bezit all alle veranderingen ...

        deActivate(this);
    }

    /**
     * Maakt de veranderingen die tijdens de simulatie gebeurd zijn ongedaan.
     */
    public void cancel() {
        BranchOffice bo = logonController.getBranchOffice();
        ProjectRepository projectRepository = bo.getProjectRepository();
        restoreState(projectRepository);
        getUserInterface().printMessage("Canceled Simulation");
        deActivate(this);
    }

    /**
     * Slaat de huidige staat van de projectRepository op.
     */
    private void storeState(ProjectRepository projectRepository) {
        this.originalState = projectRepository.createMemento();
    }

    /**
     * Hersteld de oude staat van de projectRepository.
     */
    private void restoreState(ProjectRepository projectRepository) {
        IProjectRepositoryMemento memento = this.originalState;
        if (memento != null) {
            projectRepository.setMemento(memento);
        }
    }

    /**
     * Set's this controller on top of stack in UI.
     */
    protected void activate(AbstractController controller) {
//        controllerStack.activateController(controller);
    }

    /**
     * Removes this controller from the stack in UI.
     */
    protected void deActivate(AbstractController controller) {
//        controllerStack.deActivateController(controller);
    }
}
