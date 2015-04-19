package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.ProjectRepositoryMemento;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.CancelException;

/**
 * Bevat de stappen om de use case "Running a simulation" uit te voeren.
 * Dit wordt gedaan door de commando's "Start Simulation" en "End Simulation" af te handelen.
 */
public class SimulationController extends AbstractController {
    public SimulationController(MainController mainController, ProjectRepository projectRepository, UserInterface userInterface) {
        super(userInterface);
        this.mainController = mainController;
        this.projectRepository = projectRepository;
    }

    private MainController mainController;
    private ProjectRepository projectRepository;

    /**
     * Voert de stappen voor "Start Simulation" uit.
     */
    public void startSimulation() {
        UserInterface ui = this.getUserInterface();
        try {
            ui.startSimulationMode();
            // hou de huidige state van projectRepository bij
            storeState();
        } catch (CancelException e) {
            ui.endSimulationMode();
            getUserInterface().printException(e);
        }
    }

    public void endSimulation() {
        UserInterface ui = this.getUserInterface();
        try {
            String command = ui.requestString("Wat wil je doen met de huidige simulatie?\n"
                                                + "continue = verdergaan met de simulatie\n"
                                                + "cancel   = stoppen met de simulatie (en dus niet realiseren)\n"
                                                + "realize  = de simulatie realiseren");
            if (command.equalsIgnoreCase("continue")) {
                // doe niets, want de simulatie is al gestart
            }
            else if (command.equalsIgnoreCase("realize")) {
                // realiseer de simulatie
                this.realizeState();
                ui.endSimulationMode();
                ui.printMessage("Simulatie werd gerealiseerd");
            }
        } catch (CancelException e) {
            restoreState();
            ui.endSimulationMode();
            ui.printMessage("Simulatie werd niet gerealiseerd");
        }
    }

    private void storeState() {
        this.mainController.setStoredProjectRepository(projectRepository.createMemento());
    }

    private void restoreState() {
        ProjectRepositoryMemento memento = this.mainController.getStoredProjectRepository();
        if (memento != null) {
            this.projectRepository.setMemento(memento);
        }
    }

    private void realizeState() {
        this.mainController.setStoredProjectRepository(null);
    }
}
