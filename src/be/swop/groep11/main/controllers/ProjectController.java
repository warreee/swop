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
 * Created by warreee on 3/9/15.
 */
public class ProjectController {

    private UserInterface ui;
    private final User user;

    public ProjectController(ProjectRepository projectRepository,User user, UserInterface ui){
        this.projectRepository = projectRepository;
        this.ui = ui;
        this.user = user;
    }

    ProjectRepository projectRepository;

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

    public void createProject(){
        try {
            String projectName = ui.requestString("Project naam");
            String description = ui.requestString("Project description");

            LocalDateTime creationTime = ui.requestDatum("Datum van aanmaken");
            LocalDateTime dueTime = ui.requestDatum("Deadline datum");
            projectRepository.addNewProject(projectName, description ,creationTime, dueTime, user);
        } catch (IllegalArgumentException e) {
            ui.printException(e);
            createProject();
        }catch (CancelException e) {
            ui.printException(e);
        }

    }


}
