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


    //TODO documentatie, als ook tmSystem naar SystemTime, als ook reduceren van duplicate code.
    public MainController(UserInterface userInterface,SystemTime systemTime,ProjectRepository projectRepository) {
        super(userInterface,systemTime);
        this.projectRepository = projectRepository;
    }

    @Override
    public void advanceTime() throws IllegalArgumentException {
        AbstractController controller = new AdvanceTimeController(getUserInterface(),getSysteTime());
        controller.activate();
        controller.advanceTime();
        controller.deActivate();
    }

    @Override
    public void createProject() throws IllegalArgumentException {
        AbstractController controller = new ProjectController(getProjectRepository(),new User("root"),getUserInterface(),getSysteTime());
        controller.activate();
        controller.createProject();
        controller.deActivate();
    }

    private ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    @Override
    public void createTask() throws IllegalArgumentException {
        AbstractController controller = new TaskController(getProjectRepository(),getUserInterface(),getSysteTime());
        controller.activate();
        controller.createTask();
        controller.deActivate();
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        AbstractController controller = new ProjectController(getProjectRepository(),new User("root"),getUserInterface(),getSysteTime());
        controller.activate();
        controller.showProjects();
        controller.deActivate();
    }

    @Override
    public void updateTask() throws IllegalArgumentException {
        AbstractController controller = new TaskController(getProjectRepository(),getUserInterface(),getSysteTime());
        controller.activate();
        controller.updateTask();
        controller.deActivate();
    }

    private SimulationController simController;

    @Override
    public void startSimulation() throws IllegalArgumentException {
        this.simController = new SimulationController(getSysteTime(),getProjectRepository(),getUserInterface());
        this.simController.activate();
        this.simController.startSimulation();
        //Mag niet deActivaten omdat de simulatie controller nog actief moet zijn, totdat end simulation is opgeroepen
        //controller.deActivate();
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
