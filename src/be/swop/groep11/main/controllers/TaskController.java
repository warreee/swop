package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.ui.CancelException;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.Duration;

/**
 * Created by warreee on 3/9/15.
 */
public class TaskController {

    private Project project;
    private UserInterface ui;

    public TaskController(Project project, UserInterface ui){

        this.project = project;
        this.ui = ui;
    }

    public void createTask(){
        try {
            String description = ui.requestString("Taak beschrijving");
            Double acceptableDeviation = ui.requestDouble("Taak: toegestane afwijking");
            Duration estimatedDuration = Duration.ofHours(Integer.valueOf(ui.requestNumber("Taak: geschatte duur")).longValue());
            project.addNewTask(description, acceptableDeviation, estimatedDuration);

        } catch (CancelException e) {
            ui.printException(e);
        }
    }

    public void showTasks(){
        try{
            ImmutableList<Task> tasks = project.getTasks();
            Task task = ui.selectTaskFromList(tasks);
            ui.showTaskDetails(task);
        } catch (CancelException | EmptyListException e) {
            ui.printException(e);
        }
    }

    public ImmutableList<Task> getAllTasks(){
        return project.getTasks();
    }

    public void updateStatus(Task task, TaskStatus status){
        task.setStatus(status);
    }
}
