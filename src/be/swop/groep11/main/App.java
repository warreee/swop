package be.swop.groep11.main;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.CommandLineInterface;
import be.swop.groep11.main.util.InputParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

/**
 * Klasse verantwoordelijk voor BootStrapping.
 */
public class App {

    public static void main(String[] args) {
        boolean readYamlFile = (args.length == 1 && args[0].equals("yaml"));
        App app = new App(readYamlFile);
        app.runApp();
    }

    /**
     * Constructor voor een nieuwe instantie van App.
     * @param readYamlFile  Boolean die bepaalt of de yaml file wordt ingelezen of niet.
     */
    public App(boolean readYamlFile) {
        initDomainObjects();
        initInputParser(readYamlFile);
        initControllers();
        initBehaviourMapping();
    }
    private SystemTime systemTime;
    private CommandLineInterface cli;
    private ResourceManager resourceManager;
    private ProjectRepository projectRepository;
    private ControllerStack controllerStack;

    private MainController main;
    private TaskController taskController;
    private ProjectController projectController;
    private AdvanceTimeController advanceTimeController;
    private SimulationController simulationController;
    private PlanningController planningController;
    private LogonController logonController;

    private void initDomainObjects(){
        // maak een nieuwe CommandLineInterface aan
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(java.lang.System.in));
        cli = new CommandLineInterface(bufferedReader);
        controllerStack = new ControllerStack(() -> cli.printMessage("Ongeldige action"));
        cli.setControllerStack(controllerStack);

        //maak een nieuwe system aan
        systemTime = new SystemTime(LocalDateTime.MIN);
        resourceManager = new ResourceManager();
        projectRepository = new ProjectRepository(systemTime);
    }

    private void initInputParser(boolean readYamlFile){
        if (readYamlFile) {
            // run inputreader
            InputParser inputParser = new InputParser(projectRepository, resourceManager, systemTime);
            try {
                inputParser.parseInputFile();
            } catch (FileNotFoundException e) {
                System.out.println("Yaml file niet gevonden");
            }
        }else{
            addTempDomainObjects();
        }
    }

    private void initControllers(){
        //Aanmaken van controllers
        taskController = new TaskController(projectRepository, systemTime,cli,resourceManager);
        projectController = new ProjectController(projectRepository, cli );
        advanceTimeController = new AdvanceTimeController( systemTime, cli);
        simulationController = new SimulationController(controllerStack, projectRepository, cli);
        planningController = new PlanningController(projectRepository,resourceManager, systemTime, cli);
        logonController = new LogonController(cli);
        main = new MainController(controllerStack, advanceTimeController,simulationController,projectController,
                taskController, planningController, logonController, cli);
    }

    private void initBehaviourMapping(){
        //Default strategies
        controllerStack.addDefaultBehaviour(Action.EXIT, () -> {
            cli.printMessage("wants to exit");
            cli.wantsToExit();
        });
        controllerStack.addDefaultBehaviour(Action.HELP, () -> cli.showHelp(controllerStack.getActiveController()));
        //MainController
        controllerStack.addActionBehaviour(main, Action.CREATETASK, main::createTask);
        controllerStack.addActionBehaviour(main, Action.UPDATETASK, main::updateTask);
        controllerStack.addActionBehaviour(main, Action.PLANTASK, main::planTask);
        controllerStack.addActionBehaviour(main, Action.CREATEPROJECT, main::createProject);
        controllerStack.addActionBehaviour(main, Action.SHOWPROJECTS, main::showProjects);
        controllerStack.addActionBehaviour(main, Action.ADVANCETIME, main::advanceTime);
        controllerStack.addActionBehaviour(main, Action.STARTSIMULATION, main::startSimulation);
        controllerStack.addActionBehaviour(main, Action.LOGON, main::logon);
        //ProjectController
        controllerStack.addActionBehaviour(projectController, Action.SHOWPROJECTS, projectController::showProjects);
        controllerStack.addActionBehaviour(projectController, Action.CREATEPROJECT, projectController::createProject);
        //TaskController
        controllerStack.addActionBehaviour(taskController, Action.CREATETASK, taskController::createTask);
        controllerStack.addActionBehaviour(taskController, Action.UPDATETASK, taskController::updateTask);
        //AdvanceTimeController
        controllerStack.addActionBehaviour(advanceTimeController, Action.ADVANCETIME, advanceTimeController::advanceTime);
        //SimulationController
        controllerStack.addActionBehaviour(simulationController, Action.CREATETASK, taskController::createTask);
        controllerStack.addActionBehaviour(simulationController, Action.PLANTASK, planningController::planTask);
        controllerStack.addActionBehaviour(simulationController, Action.SHOWPROJECTS, projectController::showProjects);
        controllerStack.addActionBehaviour(simulationController, Action.REALIZESIMULATION, simulationController::realize);
        controllerStack.addActionBehaviour(simulationController, Action.CANCEL, simulationController::cancel);//Cancel Simulation
        //LogonController
        controllerStack.addActionBehaviour(logonController, Action.LOGON, logonController::logon);
    }

    private void runApp(){
        controllerStack.activateController(main);
        // lees commando's
        cli.run();
    }


    private void addTempDomainObjects() {
        /* TODO dit werkt niet meer want ResourceManager > ResourceTypeRepository + ... (en hier moeten nu ook branch offices worden aangemaakt)

        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "DevA");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "DevB");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "DevC");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "DevD");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "DevE");

        resourceManager.addNewResourceType("Auto");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Aston Martin Rapide");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Toyota Auris");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Rolls Royce Phantom");

        resourceManager.addNewResourceType("CarWash", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("CarWash"), "car wash A");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("CarWash"), "car wash B");

        */

    }

}