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
    public SimulationController(ActionBehaviourMapping actionBehaviourMapping,ProjectRepository projectRepository) {
        super(actionBehaviourMapping);
        this.projectRepository = projectRepository;
        //Zelfde project repository als alle andere controllers, geen nood om actions(commands) te deligeren via simulatiecontroller.
    }
    //Store initial state
    private IProjectRepositoryMemento originalState;
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

    public void endSimulation() {
        //TODO endSimulation kan weg
      /*  try {
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
        }*/
        deActivate();
    }

    public void realize() {
        //projectRepository bezit all alle veranderingen ...

        deActivate();
    }
    public void cancel() {
        restoreState();
        getUserInterface().printMessage("Canceled Simulation");
        deActivate();
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
}
