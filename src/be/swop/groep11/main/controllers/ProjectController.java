package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.CommandStrategy;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Bevat de stappen om de use cases "Show Projects" en "Create Project" uit te voeren.
 */
public class ProjectController extends AbstractController {

    private final User user;
    private ProjectRepository projectRepository;

    /**
     * Constructor om een nieuwe project controller te maken.
     * @param projectRepository Project repository om projecten aan toe te voegen
     * @param user Gebruiker die projecten aanmaakt
     * @param ui Gebruikersinterface
     */
    public ProjectController(ProjectRepository projectRepository, User user, UserInterface ui,SystemTime systemTime){
        super(ui,systemTime);
        this.projectRepository = projectRepository;
        this.user = user;
    }

    /**
     * Voert de stappen voor de use case "Show Projects" uit.
     */
    public void showProjects() {
        try {
            ImmutableList<Project> projects = projectRepository.getProjects();

//            Project project = getUserInterface().selectFromList(projects, (proj -> {
//                String overTime = (proj.isOverTime()) ? "over time" : "on time";
//                return String.format("%-35s %-20s %-20s %n", proj.getName(), proj.getProjectStatus().name(), "(" + overTime + ")");
//            }));
            Project project =  getUserInterface().selectProjectFromList(projects);
            getUserInterface().showProjectDetails(project);

            ImmutableList<Task> tasks = project.getTasks();
            Task task =  getUserInterface().selectTaskFromList(tasks);
            getUserInterface().showTaskDetails(task);
        } catch (CancelException | EmptyListException e) {
            getUserInterface().printException(e);
        }
    }

    /**
     * Voert de stappen voor de use case "Create Project" uit.
     */
    public void createProject(){
        try {
            String projectName =  getUserInterface().requestString("Project naam");
            String description =  getUserInterface().requestString("Project beschrijving");
            LocalDateTime creationTime =  getUserInterface().requestDatum("Creation time");
            LocalDateTime dueTime =  getUserInterface().requestDatum("Due time");

            projectRepository.addNewProject(projectName, description ,creationTime, dueTime, user);
            getUserInterface().printMessage("Project toegevoegd");
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            createProject();
        } catch (CancelException e) {
            getUserInterface().printException(e);
        }

    }

    @Override
    public HashMap<Command, CommandStrategy> getCommandStrategies() {
        HashMap<Command,CommandStrategy> map = new HashMap<>(super.getCommandStrategies());
        map.put(Command.SHOWPROJECTS,this::showProjects);
        map.put(Command.CREATEPROJECT,this::createProject);
        return map;
    }

}
