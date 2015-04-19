package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.ProjectRepositoryMemento;
import be.swop.groep11.main.TMSystem;
import be.swop.groep11.main.User;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by Ronald on 17/04/2015.
 */
public class MainController extends AbstractController {

    private final ProjectRepository projectRepository;
    private final TMSystem tmSystem;

    private ProjectRepositoryMemento storedProjectRepository;

    //TODO documentatie, als ook tmSystem naar SystemTime, als ook reduceren van duplicate code.

    public MainController(UserInterface userInterface,TMSystem tmSystem,ProjectRepository projectRepository) {
        super(userInterface);
        this.tmSystem = tmSystem;
        this.projectRepository = projectRepository;
    }

    @Override
    public void advanceTime() throws IllegalArgumentException {
        AbstractController controller = new AdvanceTimeController(getTMSystem(),getUserInterface());
        controller.activate();
        controller.advanceTime();
        controller.deActivate();
    }

    @Override
    public void createProject() throws IllegalArgumentException {
        AbstractController controller = new ProjectController(getProjectRepository(),new User("root"),getUserInterface());
        controller.activate();
        controller.createProject();
        controller.deActivate();
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
        controller.activate();
        controller.createTask();
        controller.deActivate();
    }

    @Override
    public void showHelp() throws IllegalArgumentException {
        getUserInterface().printMessage("Help!");
    }

    @Override
    public void showProjects() throws IllegalArgumentException {
        AbstractController controller = new ProjectController(getProjectRepository(),new User("root"),getUserInterface());
        controller.activate();
        controller.showProjects();
        controller.deActivate();
    }

    @Override
    public void updateTask() throws IllegalArgumentException {
        AbstractController controller = new TaskController(getProjectRepository(),getUserInterface());
        controller.activate();
        controller.updateTask();
        controller.deActivate();
    }

    @Override
    public void startSimulation() throws IllegalArgumentException {
        AbstractController controller = new SimulationController(this,getProjectRepository(),getUserInterface());
        controller.activate();
        controller.startSimulation();
        controller.deActivate();
    }

    @Override
    public void endSimulation() throws IllegalArgumentException {
        /*TODO endSimulation kan niet gebeuren in de Main Controller, aangezien er eerst een simulatie gestart moet zijn.
         Moet exception gooien. Dus default implementatie via AbstractController */
        AbstractController controller = new SimulationController(this, getProjectRepository(), getUserInterface());
        controller.activate();
        controller.endSimulation();
        controller.deActivate();
    }

    public ProjectRepositoryMemento getStoredProjectRepository() {
        return storedProjectRepository;
    }

    public void setStoredProjectRepository(ProjectRepositoryMemento storedProjectRepository) {
        this.storedProjectRepository = storedProjectRepository;
    }
}
