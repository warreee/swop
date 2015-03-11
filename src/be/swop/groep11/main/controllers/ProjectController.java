package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.User;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;

/**
 * Bevat de stappen om de use cases "Show Projects" en "Create Project" uit te voeren.
 */
public class ProjectController {

    private UserInterface ui;
    private final User user;
    private ProjectRepository projectRepository;

    /**
     * Constructor om een nieuwe project controller te maken.
     * @param projectRepository Project repository om projecten aan toe te voegen
     * @param user Gebruiker die projecten aanmaakt
     * @param ui Gebruikersinterface
     */
    public ProjectController(ProjectRepository projectRepository,User user, UserInterface ui){
        this.projectRepository = projectRepository;
        this.ui = ui;
        this.user = user;
    }

    /**
     * Voert de stappen voor de use case "Show Projects" uit.
     */
    public void showProjects() {
        try {
            ImmutableList<Project> projects = projectRepository.getProjects();
            Project project = ui.selectProjectFromList(projects);
            ui.showProjectDetails(project);

            ImmutableList<Task> tasks = project.getTasks();
            Task task = ui.selectTaskFromList(tasks);
            ui.showTaskDetails(task);
        } catch (CancelException | EmptyListException e) {
            ui.printException(e);
        }
    }

    /**
     * Voert de stappen voor de use case "Create Project" uit.
     */
    public void createProject(){
        try {
            String projectName = ui.requestString("Project naam:");
            String description = ui.requestString("Project beschrijving:");
            LocalDateTime creationTime = ui.requestDatum("Creation time:");
            LocalDateTime dueTime = ui.requestDatum("Due time:");

            projectRepository.addNewProject(projectName, description ,creationTime, dueTime, user);
            ui.printMessage("Taak toegevoegd");
        } catch (IllegalArgumentException e) {
            ui.printException(e);
            createProject();
        } catch (CancelException e) {
            ui.printException(e);
        }

    }


}
