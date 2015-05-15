package be.swop.groep11.main;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ActionCondition;
import be.swop.groep11.main.actions.ActionProcedure;
import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.resource.ResourceRepository;
import be.swop.groep11.main.resource.ResourceTypeRepository;
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
        initActionProcedures();
    }
    private SystemTime systemTime;
    private CommandLineInterface cli;
    private ResourceManager resourceManager;
    private ProjectRepository projectRepository;
    private ControllerStack controllerStack;
    private Company company;

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
        controllerStack = new ControllerStack();
        cli.setControllerStack(controllerStack);

        //maak een nieuwe system aan
        systemTime = new SystemTime(LocalDateTime.MIN);

        ResourceTypeRepository typeRepository = new ResourceTypeRepository();
        company = new Company("company",typeRepository);

        projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(typeRepository);
        ResourcePlanner resourcePlanner = new ResourcePlanner(resourceRepository);

        BranchOffice bo = new BranchOffice("bo1","leuven",projectRepository,resourcePlanner);

        resourceManager = new ResourceManager();
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
        logonController = new LogonController(cli, company);
        main = new MainController(controllerStack, advanceTimeController,simulationController,projectController,
                taskController, planningController, logonController, cli);
    }

    private void initActionProcedures() {

        controllerStack.setInvalidActionProcedure(new ActionProcedure(() -> cli.printMessage("Ongeldige action"), () -> true));

        ActionCondition returnsTrue = () -> true;

        ActionProcedure defaultExit = new ActionProcedure(() -> {
            cli.printMessage("wants to exit");
            cli.wantsToExit();
        }, () -> true);
        ActionProcedure defaultHelp = new ActionProcedure(() -> cli.showHelp(controllerStack.getActiveController()), returnsTrue);


        ActionProcedure createTask = new ActionProcedure(taskController, taskController::createTask, logonController::hasIdentifiedProjectManager);
        ActionProcedure updateTask = new ActionProcedure(taskController, taskController::updateTask, logonController::hasIdentifiedDeveloper);
        ActionProcedure planTask = new ActionProcedure(planningController, planningController::planTask, logonController::hasIdentifiedProjectManager);
        ActionProcedure delegateTask = null;

        ActionProcedure createProject = new ActionProcedure(projectController, projectController::createProject, logonController::hasIdentifiedProjectManager);
        ActionProcedure showProjects = new ActionProcedure(projectController, projectController::showProjects, returnsTrue);

        ActionProcedure advanceTime = new ActionProcedure(advanceTimeController, advanceTimeController::advanceTime, returnsTrue);

        ActionProcedure logon = new ActionProcedure(logonController, logonController::logon, () -> !logonController.hasIdentifiedProjectManager(), false);
        ActionProcedure logout = new ActionProcedure(logonController, logonController::logOut, () -> logonController.hasIdentifiedUserAtBranchOffice(), true);

        ActionProcedure startSimulation = new ActionProcedure(simulationController, simulationController::startSimulation, logonController::hasIdentifiedProjectManager, false);
        ActionProcedure cancelSim = new ActionProcedure(simulationController, simulationController::cancel, logonController::hasIdentifiedProjectManager, true);
        ActionProcedure realizeSim = new ActionProcedure(simulationController, simulationController::realize, logonController::hasIdentifiedProjectManager, true);


        //set default ActionProcedures
        controllerStack.addDefaultActionProcedure(Action.EXIT, defaultExit);
        controllerStack.addDefaultActionProcedure(Action.HELP, defaultHelp);

        //set action procedure
        controllerStack.addActionProcedure(main, Action.LOGON, logon);
        controllerStack.addActionProcedure(main, Action.SHOWPROJECTS, showProjects);

        controllerStack.addActionProcedure(logonController, Action.CREATETASK, createTask);
        controllerStack.addActionProcedure(logonController, Action.UPDATETASK, updateTask);
        controllerStack.addActionProcedure(logonController, Action.PLANTASK, planTask);
        controllerStack.addActionProcedure(logonController, Action.CREATEPROJECT, createProject);
//        controllerStack.addActionProcedure(logonController, Action.DELEGATETASK, delegateTask);
        controllerStack.addActionProcedure(logonController, Action.ADVANCETIME, advanceTime);
        controllerStack.addActionProcedure(logonController, Action.STARTSIMULATION, startSimulation);

        controllerStack.addActionProcedure(logonController, Action.LOGOUT, logout);
        controllerStack.addActionProcedure(logonController, Action.SHOWPROJECTS, showProjects);

        controllerStack.addActionProcedure(simulationController, Action.CREATETASK, createTask);
        controllerStack.addActionProcedure(simulationController, Action.PLANTASK, planTask);
//        controllerStack.addActionProcedure(simulationController, Action.DELEGATETASK, delegateTask);
        controllerStack.addActionProcedure(simulationController, Action.REALIZESIMULATION, realizeSim);
        controllerStack.addActionProcedure(simulationController, Action.CANCEL, cancelSim);

        controllerStack.addActionProcedure(simulationController, Action.SHOWPROJECTS, showProjects);
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