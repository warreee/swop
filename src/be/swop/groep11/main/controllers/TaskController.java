package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bevat de stappen om de use cases "Create Task" en "Update Task" uit te voeren.
 */
public class TaskController {

    private ProjectRepository projectRepository;
    private UserInterface ui;

    /**
     * Constructor om een nieuwe task controller te maken.
     * @param ui Gebruikersinterface
     */
    public TaskController(ProjectRepository projectRepository, UserInterface ui) {
        this.ui = ui;
    }

    /**
     * Voert de stappen voor de use case "Create Task" uit.
     */
    public void createTask(){
        try {
            String description = ui.requestString("Beschrijving:");
            Double acceptableDeviation = ui.requestDouble("Aanvaardbare afwijking in procent:") / 100;
            Duration estimatedDuration = Duration.ofHours(Integer.valueOf(ui.requestNumber("Geschatte duur:")).longValue());
            ui.printMessage("Kies een project waartoe de taak moet behoren:");
            Project project = ui.selectProjectFromList(projectRepository.getProjects());

            project.addNewTask(description, acceptableDeviation, estimatedDuration);
        }
        catch (IllegalArgumentException e) {
            ui.printException(e);
        }
        catch (CancelException e) {
            ui.printException(e);
        }
    }

    /**
     * Voert de stappen voor de use case "Update Task" uit.
     */
    public void updateTask() {
        try {
            Task task = ui.selectTaskFromList(this.getAllTasks());

            LocalDateTime startTime = ui.requestDatum("Starttijd:");
            LocalDateTime endTime = ui.requestDatum("Eindtijd:");
            String status = ui.requestString("Status:");

            task.setStartTime(startTime);
            task.setEndTime(endTime);
            task.setNewStatus(TaskStatus.valueOf(status));
        }
        catch (IllegalArgumentException e) {
            ui.printException(e);
        }
        catch (CancelException e) {
            ui.printException(e);
        }
    }

    /*
    public void showTasks(){
        try{
            ImmutableList<Task> tasks = project.getTasks();
            Task task = ui.selectTaskFromList(tasks);
            ui.showTaskDetails(task);
        } catch (CancelException | EmptyListException e) {
            ui.printException(e);
        }
    }
    */

    public ImmutableList<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<Task>();
        ImmutableList<Project> projects = projectRepository.getProjects();
        for (Project project : projects) {
            tasks.addAll(project.getTasks());
        }
        return ImmutableList.copyOf(tasks);
    }

}
