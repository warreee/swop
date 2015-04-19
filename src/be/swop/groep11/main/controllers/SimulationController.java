package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.ProjectRepositoryMemento;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.CancelException;

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
    *
    * */


    public SimulationController(MainController mainController, ProjectRepository projectRepository, UserInterface userInterface) {
        super(userInterface);
        this.mainController = mainController;
        this.projectRepository = projectRepository;



        this.taskController = new TaskController(projectRepository,getUserInterface());
        this.projectController = new ProjectController(projectRepository,new User("Simulation"),getUserInterface());

    }
    //Store initial state
    private ProjectRepositoryMemento originalState;


    private MainController mainController;
    private ProjectRepository projectRepository;
    private TaskController taskController;
    private ProjectController projectController;

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
            /**
             *  Mogelijk alternatief: defini�ren van nieuwe Command's in de Command Enum
             *  Die overeenkomen met de Continue,cancel en realize.
             *  Bijvoorbeeld: SIM_Continue, SIM_Cancel, SIM_Realize
             *  Bepaal voor ieder van deze Commands een CommandStrategy
             *          CommandStrategyAlternatief<SimulationController> simContinue = SimulationController::simContinue;
             *          CommandStrategyAlternatief<SimulationController> simCancel = SimulationController::simCancel;
             *          CommandStrategyAlternatief<SimulationController> simRealize = SimulationController::simRealize;
             */

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


        //alternatief
        this.originalState = projectRepository.createMemento();
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


    @Override
    public void createTask() throws IllegalArgumentException {
        this.taskController.activate();
        this.taskController.createTask();
        this.taskController.deActivate();
    }

    @Override
    public void planTask() throws IllegalArgumentException {
        super.planTask();
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        this.projectController.activate();
        this.projectController.showProjects();
        this.projectController.deActivate();
    }

    @Override
    public void showHelp() throws IllegalArgumentException {
//       Mogelijke help functie
        getUserInterface().printMessage("Simulatiemodus gestart\n" +
                "Mogelijke commando's zijn nu:\n" +
                "show projects\n" +
                "create task\n" +
                "plan task\n" +
                "cancel\n" +
                "help\n" +
                "exit");
    }

    //TODO onderstaande voorbeeld is niet volledig,
    public void simContinue() {

    }

    public void simRealize() {

    }

    public void simCancel() {

    }
}
