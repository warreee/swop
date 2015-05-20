package be.swop.groep11.main;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ActionCondition;
import be.swop.groep11.main.actions.ActionProcedure;
import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.ui.CommandLineInterface;
import be.swop.groep11.main.util.InputParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private ControllerStack controllerStack;
    private Company company;

    private MainController main;
    private TaskController taskController;
    private ShowProjectsController showProjectsController;
    private CreateProjectController createProjectController;
    private AdvanceTimeController advanceTimeController;
    private SimulationController simulationController;
    private PlanningController planningController;
    private LogonController logonController;
    private DelegateTaskController delegateTaskController;

    private void initDomainObjects(){
        // maak een nieuwe CommandLineInterface aan
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(java.lang.System.in));
        cli = new CommandLineInterface(bufferedReader);
        controllerStack = new ControllerStack();
        cli.setControllerStack(controllerStack);

        //maak een nieuwe system aan
        systemTime = new SystemTime(LocalDateTime.MIN);

        ResourceTypeRepository typeRepository = new ResourceTypeRepository();
        company = new Company("company",typeRepository,systemTime);
    }

    private void initInputParser(boolean readYamlFile){
        if (readYamlFile) {
            // run inputreader
            InputParser inputParser = new InputParser(company);
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
        logonController = new LogonController(cli, company);

        taskController = new TaskController(logonController, systemTime,cli);
        showProjectsController = new ShowProjectsController(company, cli );
        createProjectController = new CreateProjectController(logonController, cli);
        advanceTimeController = new AdvanceTimeController( systemTime, cli);
        simulationController = new SimulationController(logonController, cli);
        planningController = new PlanningController(logonController, systemTime, cli);
        delegateTaskController = new DelegateTaskController(cli, company, logonController);
        main = new MainController(
                cli);
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
        ActionProcedure delegateTask = new ActionProcedure(delegateTaskController, delegateTaskController::delegateTask, logonController::hasIdentifiedProjectManager, true);

        ActionProcedure createProject = new ActionProcedure(showProjectsController, createProjectController::createProject, logonController::hasIdentifiedProjectManager);
        ActionProcedure showProjects = new ActionProcedure(showProjectsController, showProjectsController::showProjects, returnsTrue);

        ActionProcedure advanceTime = new ActionProcedure(advanceTimeController, advanceTimeController::advanceTime, returnsTrue);

        ActionProcedure logon = new ActionProcedure(logonController, logonController::logon, () -> !logonController.hasIdentifiedProjectManager(), false);
        ActionProcedure logout = new ActionProcedure(logonController, logonController::logOut, logonController::hasIdentifiedUserAtBranchOffice, true);

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
        controllerStack.addActionProcedure(logonController, Action.DELEGATETASK, delegateTask);
        controllerStack.addActionProcedure(logonController, Action.ADVANCETIME, advanceTime);
        controllerStack.addActionProcedure(logonController, Action.STARTSIMULATION, startSimulation);

        controllerStack.addActionProcedure(logonController, Action.LOGOUT, logout);
        controllerStack.addActionProcedure(logonController, Action.SHOWPROJECTS, showProjects);

        controllerStack.addActionProcedure(simulationController, Action.CREATETASK, createTask);
        controllerStack.addActionProcedure(simulationController, Action.PLANTASK, planTask);
        controllerStack.addActionProcedure(simulationController, Action.DELEGATETASK, delegateTask);
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
        ResourceTypeRepository resourceTypeRepository = company.getResourceTypeRepository();

        ProjectRepository projectRepository1 = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository1 = new ResourceRepository(resourceTypeRepository);
        ResourcePlanner resourcePlanner1 = new ResourcePlanner(resourceRepository1, systemTime);
        BranchOffice bo1 = new BranchOffice("Branch Office 1", "Leuven", projectRepository1, resourcePlanner1);
        company.addBranchOffice(bo1);

        ProjectRepository projectRepository2 = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository2 = new ResourceRepository(resourceTypeRepository);
        ResourcePlanner resourcePlanner2 = new ResourcePlanner(resourceRepository2, systemTime);
        BranchOffice bo2 = new BranchOfficeProxy(new BranchOffice("Branch Office 2", "Louvain-la-Neuve", projectRepository2, resourcePlanner2));
        company.addBranchOffice(bo2);


        bo1.addEmployee(new Developer("DevA", resourceTypeRepository.getDeveloperType()));
        bo1.addEmployee(new Developer("DevB", resourceTypeRepository.getDeveloperType()));
        bo1.addEmployee(new Developer("DevC", resourceTypeRepository.getDeveloperType()));
        bo1.addEmployee(new Developer("DevD", resourceTypeRepository.getDeveloperType()));
        bo1.addEmployee(new Developer("DevE", resourceTypeRepository.getDeveloperType()));
        bo1.addEmployee(new ProjectManager("PM1"));

        bo2.addEmployee(new Developer("DevF", resourceTypeRepository.getDeveloperType()));
        bo2.addEmployee(new Developer("DevG", resourceTypeRepository.getDeveloperType()));
        bo2.addEmployee(new Developer("DevH", resourceTypeRepository.getDeveloperType()));
        bo2.addEmployee(new Developer("DevI", resourceTypeRepository.getDeveloperType()));
        bo2.addEmployee(new ProjectManager("PM2"));
        bo2.addEmployee(new ProjectManager("PM3"));

        resourceTypeRepository.addNewResourceType("Auto");
        AResourceType auto = resourceTypeRepository.getResourceTypeByName("Auto");

        resourceRepository1.addResourceInstance(new Resource("Aston Martin Rapide", auto));
        resourceRepository1.addResourceInstance(new Resource("Toyota Auris", auto));
        resourceRepository1.addResourceInstance(new Resource("Rolls Royce Phantom", auto));

        resourceRepository2.addResourceInstance(new Resource("Auto 1",auto));
        resourceRepository2.addResourceInstance(new Resource("Auto 2",auto));
        resourceRepository2.addResourceInstance(new Resource("Auto 3",auto));
        resourceRepository2.addResourceInstance(new Resource("Auto 4", auto));

        resourceTypeRepository.addNewResourceType("CarWash", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        AResourceType carWash = resourceTypeRepository.getResourceTypeByName("CarWash");

        resourceRepository1.addResourceInstance(new Resource("CarWash A", carWash));
        resourceRepository1.addResourceInstance(new Resource("CarWash B", carWash));

        resourceRepository2.addResourceInstance(new Resource("CarWash C",carWash));
    }

}