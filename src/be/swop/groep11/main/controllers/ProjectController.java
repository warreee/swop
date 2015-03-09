package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.ProjectStatus;
import be.swop.groep11.main.Task;
import com.google.common.collect.ImmutableList;

/**
 * Created by warreee on 3/9/15.
 */
public class ProjectController {

    public ProjectController(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }

    ProjectRepository projectRepository;

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
