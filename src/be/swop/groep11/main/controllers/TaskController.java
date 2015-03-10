package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.Duration;

/**
 * Created by warreee on 3/9/15.
 */
public class TaskController {

    Project project;
    UserInterface ui;

    public TaskController(Project project, UserInterface ui){
        this.project = project;
        this.ui = ui;
    }

    public void createTask(String description, int acceptableDeviation, Duration estimatedDuration){
        project.addNewTask(description, acceptableDeviation, estimatedDuration);
    }

    public ImmutableList<Task> getAllTasks(){
        return project.getTasks();
    }

    public void updateStatus(Task task, TaskStatus status){
        task.setStatus(status);
    }
}
