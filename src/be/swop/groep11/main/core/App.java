package be.swop.groep11.main.core;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.ui.CommandLineInterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * Created by Ronald on 22/04/2015.
 */
public class App {

    public static void main(String[] args) {
        boolean readYamlFile = (args.length == 1 && args[0].equals("yaml"));

        // maak een nieuwe CommandLineInterface aan
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(java.lang.System.in));
        CommandLineInterface cli = new CommandLineInterface(bufferedReader);
        ActionBehaviourMapping actionBehaviourMapping = new ActionBehaviourMapping(cli,() -> cli.printMessage("Ongeldig command"));

        //maak een nieuwe system aan
        SystemTime systemTime = new SystemTime();
        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        //TODO temp resourceManager?
        ResourceManager resourceManager = new ResourceManager();

        if (readYamlFile) {
            // run inputreader
            InputParser inputParser = new InputParser(projectRepository,resourceManager );
            try {
                inputParser.parseInputFile();
            } catch (FileNotFoundException e) {
                System.out.println("Yaml file niet gevonden");
            }
        }

        //Aanmaken van controllers
        TaskController taskController = new TaskController(actionBehaviourMapping,projectRepository, systemTime);
        ProjectController projectController = new ProjectController(projectRepository, new User("ROOT"), actionBehaviourMapping);
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(actionBehaviourMapping, systemTime);
        SimulationController simulationController = new SimulationController(actionBehaviourMapping, projectRepository);
        PlanningController planningController = new PlanningController(actionBehaviourMapping);
        //Aanmaken main controller
        MainController main = new MainController(actionBehaviourMapping, advanceTimeController,simulationController,projectController,taskController,planningController);
        actionBehaviourMapping.activateController(main);
        //Default strategies
        actionBehaviourMapping.addDefaultStrategy(Action.EXIT, () -> {
            cli.printMessage("wants to exit");
            cli.wantsToExit();
        });
        actionBehaviourMapping.addDefaultStrategy(Action.HELP, () -> cli.showHelp(actionBehaviourMapping.getActiveController()));
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
        // lees commando's
        cli.run();
    }
}