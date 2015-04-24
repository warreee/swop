package be.swop.groep11.main.core;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.CommandLineInterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.time.LocalTime;

/**
 * Created by Ronald on 22/04/2015.
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

        // TODO: geen temp domain objects toevoegen bij uiteindelijke versie van het programma!!!!!!!
        addTempDomainObjects();
    }
    private SystemTime systemTime;
    private CommandLineInterface cli;
    private ResourceManager resourceManager;
    private ProjectRepository projectRepository;
    private ActionBehaviourMapping actionBehaviourMapping;

    private MainController main;
    private TaskController taskController;
    private ProjectController projectController;
    private AdvanceTimeController advanceTimeController;
    private SimulationController simulationController;
    private PlanningController planningController;

    private void initDomainObjects(){
        // maak een nieuwe CommandLineInterface aan
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(java.lang.System.in));
        cli = new CommandLineInterface(bufferedReader);
        actionBehaviourMapping = new ActionBehaviourMapping(() -> cli.printMessage("Ongeldige action"));
        cli.setActionBehaviourMapping(actionBehaviourMapping);

        //maak een nieuwe system aan
        systemTime = new SystemTime();
        resourceManager = new ResourceManager();
        projectRepository = new ProjectRepository(systemTime);
    }

    private void initInputParser(boolean readYamlFile){
        if (readYamlFile) {
            // run inputreader
            InputParser inputParser = new InputParser(projectRepository,resourceManager );
            try {
                inputParser.parseInputFile();
            } catch (FileNotFoundException e) {
                System.out.println("Yaml file niet gevonden");
            }
        }
    }

    private void initControllers(){
        //Aanmaken van controllers
        taskController = new TaskController(projectRepository, systemTime,cli, resourceManager );
        projectController = new ProjectController(projectRepository, new User("ROOT"), cli );
        advanceTimeController = new AdvanceTimeController( systemTime, cli);
        simulationController = new SimulationController(actionBehaviourMapping, projectRepository, cli);
        planningController = new PlanningController(projectRepository,resourceManager, systemTime, cli);
        main = new MainController(actionBehaviourMapping, advanceTimeController,simulationController,projectController,taskController,planningController,cli );

    }

    private void initBehaviourMapping(){
        //Default strategies
        actionBehaviourMapping.addDefaultBehaviour(Action.EXIT, () -> {
            cli.printMessage("wants to exit");
            cli.wantsToExit();
        });
        actionBehaviourMapping.addDefaultBehaviour(Action.HELP, () -> cli.showHelp(actionBehaviourMapping.getActiveController()));
        //MainController
        actionBehaviourMapping.addActionBehaviour(main, Action.CREATETASK, main::createTask);
        actionBehaviourMapping.addActionBehaviour(main, Action.UPDATETASK, main::updateTask);
        actionBehaviourMapping.addActionBehaviour(main, Action.PLANTASK, main::planTask);
        actionBehaviourMapping.addActionBehaviour(main, Action.CREATEPROJECT, main::createProject);
        actionBehaviourMapping.addActionBehaviour(main, Action.SHOWPROJECTS, main::showProjects);
        actionBehaviourMapping.addActionBehaviour(main, Action.ADVANCETIME, main::advanceTime);
        actionBehaviourMapping.addActionBehaviour(main, Action.STARTSIMULATION, main::startSimulation);
        //ProjectController
        actionBehaviourMapping.addActionBehaviour(projectController, Action.SHOWPROJECTS, projectController::showProjects);
        actionBehaviourMapping.addActionBehaviour(projectController, Action.CREATEPROJECT, projectController::createProject);
        //TaskController
        actionBehaviourMapping.addActionBehaviour(taskController, Action.CREATETASK, taskController::createTask);
        actionBehaviourMapping.addActionBehaviour(taskController, Action.UPDATETASK, taskController::updateTask);
        //AdvanceTimeController
        actionBehaviourMapping.addActionBehaviour(advanceTimeController, Action.ADVANCETIME, advanceTimeController::advanceTime);
        //SimulationController
        actionBehaviourMapping.addActionBehaviour(simulationController, Action.CREATETASK, taskController::createTask);
        actionBehaviourMapping.addActionBehaviour(simulationController, Action.PLANTASK, taskController::planTask);
        actionBehaviourMapping.addActionBehaviour(simulationController, Action.SHOWPROJECTS, projectController::showProjects);
        actionBehaviourMapping.addActionBehaviour(simulationController, Action.REALIZESIMULATION, simulationController::realize);
        actionBehaviourMapping.addActionBehaviour(simulationController, Action.CANCEL, simulationController::cancel); //Cancel Simulation
    }

    private void runApp(){
        actionBehaviourMapping.activateController(main);
        // lees commando's
        cli.run();

    }

    private void addTempDomainObjects() {

        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter SWOP");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ward");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ronald");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Robin");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Arne");

        resourceManager.addNewResourceType("Auto");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Aston Martin Rapide");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Toyota Auris");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Rolls Royce Phantom");

        resourceManager.addNewResourceType("Koets", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 1");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 2");

    }
}