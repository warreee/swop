package be.swop.groep11.main.core;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ActionMapping;
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
        ActionMapping actionMapping = new ActionMapping(cli,() -> cli.printMessage("Ongeldig command"));

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
        TaskController taskController = new TaskController(actionMapping,projectRepository, systemTime);
        ProjectController projectController = new ProjectController(projectRepository, new User("ROOT"), actionMapping);
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(actionMapping, systemTime);
        SimulationController simulationController = new SimulationController(actionMapping, projectRepository);
        PlanningController planningController = new PlanningController(actionMapping);
        //Aanmaken main controller
        MainController main = new MainController(actionMapping, advanceTimeController,simulationController,projectController,taskController,planningController);
        actionMapping.activateController(main);
        //Default strategies
        actionMapping.addDefaultStrategy(Action.EXIT, () -> {
            cli.printMessage("wants to exit");
            cli.wantsToExit();
        });
        actionMapping.addDefaultStrategy(Action.HELP, () -> cli.showHelp(actionMapping.getActiveController()));
        //MainController
        actionMapping.addActionStrategy(main, Action.CREATETASK, main::createTask);
        actionMapping.addActionStrategy(main, Action.UPDATETASK, main::updateTask);
        actionMapping.addActionStrategy(main, Action.PLANTASK, main::planTask);
        actionMapping.addActionStrategy(main, Action.CREATEPROJECT, main::createProject);
        actionMapping.addActionStrategy(main, Action.SHOWPROJECTS, main::showProjects);
        actionMapping.addActionStrategy(main, Action.ADVANCETIME, main::advanceTime);
        actionMapping.addActionStrategy(main, Action.STARTSIMULATION, main::startSimulation);
        //ProjectController
        actionMapping.addActionStrategy(projectController, Action.SHOWPROJECTS, projectController::showProjects);
        actionMapping.addActionStrategy(projectController, Action.CREATEPROJECT, projectController::createProject);
        //TaskController
        actionMapping.addActionStrategy(taskController, Action.CREATETASK, taskController::createTask);
        actionMapping.addActionStrategy(taskController, Action.UPDATETASK, taskController::updateTask);
        //AdvanceTimeController
        actionMapping.addActionStrategy(advanceTimeController, Action.ADVANCETIME, advanceTimeController::advanceTime);
        //SimulationController
        actionMapping.addActionStrategy(simulationController, Action.CREATETASK, taskController::createTask);
        actionMapping.addActionStrategy(simulationController, Action.PLANTASK, taskController::planTask);
        actionMapping.addActionStrategy(simulationController, Action.SHOWPROJECTS, projectController::showProjects);
        actionMapping.addActionStrategy(simulationController, Action.REALIZESIMULATION, simulationController::realize);
        actionMapping.addActionStrategy(simulationController, Action.CANCEL, simulationController::cancel); //Cancel Simulation
        // lees commando's
        cli.run();

    }
}