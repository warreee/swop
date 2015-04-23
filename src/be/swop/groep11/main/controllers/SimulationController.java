package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.IProjectRepositoryMemento;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Bevat de stappen om de use case "Running a simulation" uit te voeren.
 * Dit wordt gedaan door de commando's "Start Simulation" en "End Simulation" af te handelen.
 */
public class SimulationController extends AbstractController {
    /*
    * De te ondersteunen Use Case's:
    * Create Task
    * Plan Task
    * Resolve Conflict
    * Show Projects
    * */
    public SimulationController(ActionBehaviourMapping actionBehaviourMapping, ProjectRepository projectRepository, UserInterface userInterface) {
        super(userInterface);
        this.actionBehaviourMapping = actionBehaviourMapping;
        this.projectRepository = projectRepository;
        //Zelfde project repository als alle andere controllers, geen nood om actions(commands) te deligeren via simulatiecontroller.
    }
    //Store initial state
    private IProjectRepositoryMemento originalState;
    private ActionBehaviourMapping actionBehaviourMapping;
    private ProjectRepository projectRepository;


    /**
     * Voert de stappen voor "Start Simulation" uit.
     */
    public void startSimulation() {
        // hou de huidige state van projectRepository bij
        storeState();
        UserInterface ui = this.getUserInterface();
        ui.showHelp(this);
    }

/*    public void endSimulation() {
      *//*  try {
            String command = ui.requestString("Wat wil je doen met de huidige simulatie?\n"
                                                + "continue = verdergaan met de simulatie\n"
                                                + "cancel   = stoppen met de simulatie (en dus niet realiseren)\n"
                                                + "realize  = de simulatie realiseren");
            if (command.equalsIgnoreCase("continue")) {
                // doe niets, want de simulatie is al gestart
            }
            else if (command.equalsIgnoreCase("realize")) {
                // realiseer de simulatie
//                this.realizeState();
                ui.printMessage("Simulatie werd gerealiseerd");
            }
        } catch (CancelException e) {
            restoreState();
            ui.printMessage("Simulatie werd niet gerealiseerd");
        }*//*
        deActivate(this);
    }*/

    public void realize() {
        //projectRepository bezit all alle veranderingen ...

        deActivate(this);
    }
    public void cancel() {
        restoreState();
        getUserInterface().printMessage("Canceled Simulation");
        deActivate(this);
    }

    private void storeState() {
        this.originalState = projectRepository.createMemento();
    }

    private void restoreState() {
        IProjectRepositoryMemento memento = this.originalState;
        if (memento != null) {
            this.projectRepository.setMemento(memento);
        }
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
}
