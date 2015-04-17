package be.swop.groep11.main.controllers;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.task.Task;
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
public class TaskController extends AbstractController {

    private ProjectRepository projectRepository;

    /**
     * Constructor om een nieuwe task controller te maken.
     * @param ui Gebruikersinterface
     */
    public TaskController(ProjectRepository projectRepository, UserInterface ui) {
        super(ui);
        this.projectRepository = projectRepository;
    }

    /**
     * Voert de stappen voor de use case "Create Task" uit.
     */
    public void createTask(){
        try {
            Project project =  getUserInterface().selectProjectFromList(projectRepository.getProjects());
            String description =  getUserInterface().requestString("Beschrijving:");
            Double acceptableDeviation =  getUserInterface().requestDouble("Aanvaardbare afwijking in procent:") / 100;
            Duration estimatedDuration = Duration.ofMinutes(Integer.valueOf( getUserInterface().requestNumber("Geschatte duur in minuten:")).longValue());

            List<Task> tasks = new ArrayList<Task>(project.getTasks());
            List<Task> selectedTasks = new ArrayList<Task>();
            while ( getUserInterface().requestString("Voeg een afhankelijkheid toe? (y/N)").equalsIgnoreCase("y")) {
                if (tasks.isEmpty()) {
                    getUserInterface().printMessage("Geen taken om toe te voegen...");
                    break;
                }
                else {
                    getUserInterface().printMessage("Opmerking: de nieuwe taak zal van de hieronder gekozen taak afhangen.");
                    Task task =  getUserInterface().selectTaskFromList(ImmutableList.copyOf(tasks));
                    tasks.remove(task);
                    selectedTasks.add(task);
                }
            }

            Task alternativeTaskFor = null;
            if ( (! project.getFailedTasks().isEmpty()) &&  getUserInterface().requestString("Is deze taak een alternatieve taak? (y/N)").equalsIgnoreCase("y")) {
                getUserInterface().printMessage("Deze taak zal een zal een alternatieve taak zijn voor de geselecteerde taak.");
                alternativeTaskFor =  getUserInterface().selectTaskFromList(project.getFailedTasks());
            }

            project.addNewTask(description, acceptableDeviation, estimatedDuration);
            // opm.: het toevoegen van afhankelijke taken kan nog geen fouten veroorzaken,
            // dus het is geen probleem dat de taak al gecreëerd is
            Task task = project.getTasks().get(project.getTasks().size()-1);
            for (Task dependingOn : selectedTasks) {
                task.addNewDependencyConstraint(dependingOn);
            }
            if (alternativeTaskFor != null) {
                alternativeTaskFor.setAlternativeTask(project.getTasks().get(project.getTasks().size() - 1));
            }
            getUserInterface().printMessage("Taak toegevoegd");
        }
        catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            createTask();
        }
        catch (EmptyListException|CancelException e) {
            getUserInterface().printException(e);
        }
    }

    /**
     * Voert de stappen voor de use case "Update Task" uit.
     */
    public void updateTask() {
        try {
            Task task =  getUserInterface().selectTaskFromList(projectRepository.getAllAvailableTasks());
            updateTask(task);
        }
        catch (EmptyListException|CancelException e) {
            getUserInterface().printException(e);
        }
    }

    private void updateTask(Task task) throws CancelException{
        try {
            LocalDateTime startTime =  getUserInterface().requestDatum("Starttijd (of laat leeg om starttijd niet te wijzigen):");
            LocalDateTime endTime =  getUserInterface().requestDatum("Eindtijd (of laat leeg om eindtijd niet te wijzigen):");
            String status =  getUserInterface().requestString("Status: FAILED of FINISHED (of laat leeg om status niet te wijzigen):");

            if(startTime != null){
                task.setStartTime(startTime);
            }
            if(task.hasStartTime() && endTime != null){
                task.setEndTime(endTime);
            }
           if(!status.isEmpty()){
               doTransition(status, task);
           }
            if (startTime == null && endTime == null && status.isEmpty())
                getUserInterface().printMessage("Geen updates gedaan");
            else
                getUserInterface().printMessage("Taak geupdated");
        }
        catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            updateTask(task);
        }
    }

    private void doTransition(String status, Task task) throws IllegalArgumentException {
        status = status.toLowerCase();
        switch (status) {
            case "execute":
                task.execute();
                break;
            case "fail":
                task.fail();
                break;
            case "finish":
                task.finish();
                break;
            default:
                throw new IllegalArgumentException("Een verkeerd commando werd meegegeven!");
        }

    }





}