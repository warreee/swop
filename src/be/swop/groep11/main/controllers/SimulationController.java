package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.IProjectRepositoryMemento;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.resource.*;
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

    /**
     * Maakt een nieuwe instantie van een simulatie controller.
     * @param logonController de logoncontroller om de gebruiker te authentificeren.
     * @param userInterface De userinterface om alles aan de gebruiker te laten zien.
     */
    public SimulationController (LogonController logonController, UserInterface userInterface) {
        super(userInterface);
        this.logonController = logonController;
        //Zelfde project repository als alle andere controllers, geen nood om actions(commands) te deligeren via simulatiecontroller.
    }

    //Store initial state
    private IProjectRepositoryMemento projectRepositoryMemento;
    private IResourcePlannerMemento resourcePlannerMemento;


    /**
     * Voert de stappen voor "Start Simulation" uit.
     */
    public void startSimulation() {
        // hou de huidige state van projectRepository bij
        BranchOffice bo = logonController.getBranchOffice();
        ProjectRepository projectRepository = bo.getProjectRepository();
        ResourcePlanner resourcePlanner = bo.getResourcePlanner();

        storeState(projectRepository, resourcePlanner);
        UserInterface ui = this.getUserInterface();
        ui.showHelp(this);
    }

    /**
     * Maakt de veranderingen die tijdens de simulatie gebeurd zijn defenitief.
     */
    public void realize() {
        //projectRepository bezit all alle veranderingen ...
        this.projectRepositoryMemento = null;
        this.resourcePlannerMemento = null;
    }

    /**
     * Maakt de veranderingen die tijdens de simulatie gebeurd zijn ongedaan.
     */
    public void cancel() {
        BranchOffice bo = logonController.getBranchOffice();
        ProjectRepository projectRepository = bo.getProjectRepository();
        ResourcePlanner resourcePlanner = bo.getResourcePlanner();
        restoreState(projectRepository, resourcePlanner);
        getUserInterface().printMessage("Canceled Simulation");
    }

    /**
     * Slaat de huidige staat van de projectRepository op.
     */
    private void storeState(ProjectRepository projectRepository, ResourcePlanner resourcePlanner) {
        this.projectRepositoryMemento = projectRepository.createMemento();
        this.resourcePlannerMemento = resourcePlanner.createMemento();
    }

    /**
     * Hersteld de oude staat van de projectRepository.
     */
    private void restoreState(ProjectRepository projectRepository, ResourcePlanner resourcePlanner) {
        IProjectRepositoryMemento projectRepositoryMemento = this.projectRepositoryMemento;
        if (projectRepositoryMemento != null) {
            projectRepository.setMemento(projectRepositoryMemento);
        }

        IResourcePlannerMemento resourcePlannerMemento = this.resourcePlannerMemento;
        if (resourcePlannerMemento != null) {
            resourcePlanner.setMemento(resourcePlannerMemento);
        }
    }
}
