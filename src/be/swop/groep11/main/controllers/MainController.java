package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.TMSystem;
import be.swop.groep11.main.User;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by Ronald on 17/04/2015.
 */
public class MainController extends AbstractController {

    private final ProjectRepository projectRepository;
    private final TMSystem tmSystem;

    public MainController(UserInterface userInterface,TMSystem tmSystem,ProjectRepository projectRepository) {
        super(userInterface);
        this.tmSystem = tmSystem;
        this.projectRepository = projectRepository;
    }

    @Override
    public void advanceTime() throws IllegalArgumentException {
        AbstractController controller = new AdvanceTimeController(getTMSystem(),getUserInterface());
        getUserInterface().addController(controller);
        controller.advanceTime();
        getUserInterface().removeController(controller);
    }

    @Override
    public void createProject() throws IllegalArgumentException {
        AbstractController controller = new ProjectController(getProjectRepository(),new User("root"),getUserInterface());
        getUserInterface().addController(controller);
        controller.createProject();
        getUserInterface().removeController(controller);
    }

    private ProjectRepository getProjectRepository() {
        return projectRepository;
    }
    private TMSystem getTMSystem() {
        return tmSystem;
    }

    @Override
    public void createTask() throws IllegalArgumentException {
        AbstractController controller = new TaskController(getProjectRepository(),getUserInterface());
        getUserInterface().addController(controller);
        controller.createTask();
        getUserInterface().removeController(controller);
    }

    @Override
    public void showHelp() throws IllegalArgumentException {
        getUserInterface().printMessage("Help!");
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        AbstractController controller = new ProjectController(getProjectRepository(),new User("root"),getUserInterface());
        getUserInterface().addController(controller);
        controller.showProjects();
        getUserInterface().removeController(controller);
    }

    @Override
    public void updateTask() throws IllegalArgumentException {
        AbstractController controller = new TaskController(getProjectRepository(),getUserInterface());
        getUserInterface().addController(controller);
        controller.updateTask();
        getUserInterface().removeController(controller);
    }

    @Override
    public void startSimulation() throws IllegalArgumentException {
        ProjectRepository repo = null; //Welke repository?
        AbstractController controller = new SimulationController(repo,getUserInterface());
        getUserInterface().addController(controller);
        controller.startSimulation();
        getUserInterface().removeController(controller);
    }
}
