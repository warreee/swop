package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;

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
        this.projectRepository = projectRepository;
        this.ui = ui;
    }

    /**
     * Voert de stappen voor de use case "Create Task" uit.
     */
    public void createTask(){
        try {
            Project project = ui.selectProjectFromList(projectRepository.getProjects());
            String description = ui.requestString("Beschrijving:");
            Double acceptableDeviation = ui.requestDouble("Aanvaardbare afwijking in procent:") / 100;
            Duration estimatedDuration = Duration.ofMinutes(Integer.valueOf(ui.requestNumber("Geschatte duur in minuten:")).longValue());

            List<Task> tasks = new ArrayList<Task>(project.getTasks());
            List<Task> selectedTasks = new ArrayList<Task>();
            while (ui.requestString("Voeg een afhankelijkheid toe? (y/N)").equals("y")) {
                if (tasks.isEmpty()) {
                    ui.printMessage("Geen taken om toe te voegen...");
                    break;
                }
                else {
                    ui.printMessage("Opmerking: de nieuwe taak zal van de hieronder gekozen taak afhangen.");
                    Task task = ui.selectTaskFromList(ImmutableList.copyOf(tasks));
                    tasks.remove(task);
                    selectedTasks.add(task);
                }
            }

            project.addNewTask(description, acceptableDeviation, estimatedDuration);
            // opm.: het toevoegen van afhankelijke taken kan nog geen fouten veroorzaken,
            // dus het is geen probleem dat de taak al gecreëerd is
            Task task = project.getTasks().get(project.getTasks().size()-1);
            for (Task dependingOn : selectedTasks) {
                task.addNewDependencyConstraint(dependingOn);
            }
            ui.printMessage("Taak toegevoegd");
        }
        catch (IllegalArgumentException e) {
            ui.printException(e);
            createTask();
        }
        catch (EmptyListException|CancelException e) {
            ui.printException(e);
        }
    }

    /**
     * Voert de stappen voor de use case "Update Task" uit.
     */
    public void updateTask() {
        try {
            Task task = ui.selectTaskFromList(this.getAllTasks());
            updateTask(task);
        }
        catch (EmptyListException|CancelException e) {
            ui.printException(e);
        }
    }

    private void updateTask(Task task) throws CancelException{
        try {
            LocalDateTime startTime = ui.requestDatum("Starttijd (of laat leeg om starttijd niet te wijzigen):");
            LocalDateTime endTime = ui.requestDatum("Eindtijd (of laat leeg om eindtijd niet te wijzigen):");
            String status = ui.requestString("Status: FAILED of FINISHED (of laat leeg om status niet te wijzigen):");

            if(startTime != null){
                task.setStartTime(startTime);
            }
            if(task.hasStartTime() && endTime != null){
                task.setEndTime(endTime);
            }
           if(!status.isEmpty()){
               task.setNewStatus(TaskStatus.valueOf(status));
           }
            if (startTime == null && endTime == null && status.isEmpty())
                ui.printMessage("Geen updates gedaan");
            else
                ui.printMessage("Taak geupdated");
        }
        catch (IllegalArgumentException e) {
            ui.printException(e);
            updateTask(task);
        }
    }

    private ImmutableList<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<Task>();
        ImmutableList<Project> projects = projectRepository.getProjects();
        for (Project project : projects) {
            tasks.addAll(project.getTasks());
        }
        return ImmutableList.copyOf(tasks);
    }

}