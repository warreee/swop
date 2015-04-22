package be.swop.groep11.main.core;

import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.actions.ActionMapping;
import be.swop.groep11.main.ui.CommandLineInterface;
import be.swop.groep11.main.actions.Action;

import java.io.FileNotFoundException;

/**
 * Created by Ronald on 22/04/2015.
 */
public class App {

    public static void main(String[] args) {
        boolean readYamlFile = (args.length == 1 && args[0].equals("yaml"));

        // maak een nieuwe CommandLineInterface aan
        CommandLineInterface cli = new CommandLineInterface();
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
        actionMapping.addDefaultStrategy(Action.EXIT, () -> {cli.printMessage("wants to exit");cli.wantsToExit();});
        actionMapping.addDefaultStrategy(Action.HELP, () -> cli.showHelp(actionMapping.getActiveController()));
        //MainController
        actionMapping.addCommandStrategy(main, Action.CREATETASK, main::createTask);
        actionMapping.addCommandStrategy(main, Action.UPDATETASK, main::updateTask);
        actionMapping.addCommandStrategy(main, Action.PLANTASK,main::planTask);
        actionMapping.addCommandStrategy(main, Action.CREATEPROJECT,main::createProject);
        actionMapping.addCommandStrategy(main, Action.SHOWPROJECTS,main::showProjects);
        actionMapping.addCommandStrategy(main, Action.ADVANCETIME, main::advanceTime);
        actionMapping.addCommandStrategy(main, Action.STARTSIMULATION, main::startSimulation);
        //ProjectController
        actionMapping.addCommandStrategy(projectController, Action.SHOWPROJECTS, projectController::showProjects);
        actionMapping.addCommandStrategy(projectController, Action.CREATEPROJECT, projectController::createProject);
        //TaskController
        actionMapping.addCommandStrategy(taskController, Action.CREATETASK, taskController::createTask);
        actionMapping.addCommandStrategy(taskController, Action.UPDATETASK, taskController::updateTask);
        //AdvanceTimeController
        actionMapping.addCommandStrategy(advanceTimeController, Action.ADVANCETIME, advanceTimeController::advanceTime);
        //SimulationController
        actionMapping.addCommandStrategy(simulationController, Action.CREATETASK, taskController::createTask);
        actionMapping.addCommandStrategy(simulationController, Action.PLANTASK, taskController::planTask);
        actionMapping.addCommandStrategy(simulationController, Action.SHOWPROJECTS, projectController::showProjects);
        actionMapping.addCommandStrategy(simulationController, Action.REALIZESIMULATION, simulationController::realize);
        actionMapping.addCommandStrategy(simulationController, Action.CANCEL, simulationController::cancel); //Cancel Simulation
        // lees commando's
        cli.run();
    }
}