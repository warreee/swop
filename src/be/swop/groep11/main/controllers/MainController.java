package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.CommandStrategy;

import java.util.HashMap;

/**
 * Created by Ronald on 17/04/2015.
 */
public class MainController extends AbstractController {

    private final ProjectRepository projectRepository;
    private final SimulationController simulationController;
    private final ProjectController projectController;
    private final AdvanceTimeController advanceTimeController;
    private final TaskController taskController;

    //TODO documentatie, als ook tmSystem naar SystemTime, als ook reduceren van duplicate code.
    public MainController(UserInterface userInterface,SystemTime systemTime,ProjectRepository projectRepository) {
        super(userInterface,systemTime);
        this.projectRepository = projectRepository;
        this.advanceTimeController =  new AdvanceTimeController(getUserInterface(), getSystemTime());
        this.projectController = new ProjectController(getProjectRepository(),new User("root"),getUserInterface(), getSystemTime());
        this.taskController = new TaskController(getProjectRepository(),getUserInterface(), getSystemTime());
        this.simulationController = new SimulationController(getSystemTime(),getProjectRepository(),getUserInterface());
    }

    @Override
    public void advanceTime() throws IllegalArgumentException {
        advanceTimeController.activate();
        advanceTimeController.advanceTime();
        advanceTimeController.deActivate();
    }

    @Override
    public void createProject() throws IllegalArgumentException {
        projectController.activate();
        projectController.createProject();
        projectController.deActivate();
    }

    private ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    @Override
    public void createTask() throws IllegalArgumentException {
        taskController.activate();
        taskController.createTask();
        taskController.deActivate();
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        projectController.activate();
        projectController.showProjects();
        projectController.deActivate();
    }

    @Override
    public void updateTask() throws IllegalArgumentException {
        taskController.activate();
        taskController.updateTask();
        taskController.deActivate();
    }


    @Override
    public void startSimulation() throws IllegalArgumentException {
        simulationController.activate();
        simulationController.startSimulation();
        //Mag niet deActivaten omdat de simulatie controller nog actief moet zijn, totdat end simulation is opgeroepen
        //simulationController.deActivate();
    }

    public AdvanceTimeController getAdvanceTimeController() {
        return advanceTimeController;
    }
    public ProjectController getProjectController() {
        return projectController;
    }
    public SimulationController getSimulationController() {
        return simulationController;
    }
    public TaskController getTaskController() {
        return taskController;
    }

    public HashMap<Command,CommandStrategy> getCommandStrategies(){
        HashMap<Command,CommandStrategy> map = new HashMap<>(super.getCommandStrategies());
        map.put(Command.CREATETASK,this::createTask);
        map.put(Command.UPDATETASK, this::updateTask);
        map.put(Command.PLANTASK,this::planTask);
        map.put(Command.CREATEPROJECT,this::createProject);
        map.put(Command.SHOWPROJECTS,this::showProjects);
        map.put(Command.ADVANCETIME,this::advanceTime);
        map.put(Command.STARTSIMULATION,this::startSimulation);
        return map;
    }
}
