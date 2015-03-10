package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.ProjectStatus;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

/**
 * Created by warreee on 3/9/15.
 */
public class ProjectController {

    private UserInterface ui;

    public ProjectController(ProjectRepository projectRepository, UserInterface ui){
        this.projectRepository = projectRepository;
        this.ui = ui;
    }

    ProjectRepository projectRepository;

    /**
     * Voert de stappen voor de use case "Show Projects" uit.
     */
    public void showProjects() {
        // TODO: implementatie aanpassen
        ImmutableList<Project> projects = getAllProjects();
        ui.showProjectList(projects);
    }

    /**
     * Geeft een immutable lijst terug van alle projecten
     */
    public ImmutableList<Project> getAllProjects(){
        return projectRepository.getProjects();
    }

    public Project getProjectFromList (int index){
        return getAllProjects().get(index);
    }

    public Task getTaskFromProject(int index, Project project){
        return project.getTasks().get(index);
    }




}
