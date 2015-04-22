package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.actions.ActionMapping;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.actions.CancelException;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Bevat de stappen om de use cases "Create Task" en "Update Task" uit te voeren.
 */
public class TaskController extends AbstractController {

    private ProjectRepository projectRepository;
    private SystemTime systemTime;

    /**
     * Constructor om een nieuwe task controller te maken.
     * @param ui Gebruikersinterface
     */
    public TaskController(ActionMapping actionMapping,ProjectRepository projectRepository,SystemTime systemTime) {
        super(actionMapping);
        this.projectRepository = projectRepository;
        this.systemTime = systemTime;
    }

    private SystemTime getSystemTime() {
        return systemTime;
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

            List<Task> tasks = new ArrayList<>(project.getTasks());
            List<Task> selectedTasks = new ArrayList<>();
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
            //TODO select from list voor ieder project apart
            Task task =  getUserInterface().selectTaskFromList(projectRepository.getAllAvailableTasks());
            updateTask(task);
        }
        catch (CancelException e) {
            getUserInterface().printException(e);
        }
    }

    private void updateTask(Task task) throws CancelException{
        try {
            String status =  getUserInterface().requestString("Status: FAILED of FINISHED (of laat leeg om status niet te wijzigen):");
            doTransition(status, task);
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
                task.execute(getSystemTime().getCurrentSystemTime());
                break;
            case "fail":
                task.fail(getSystemTime().getCurrentSystemTime());
                break;
            case "finish":
                task.finish(getSystemTime().getCurrentSystemTime());
                break;
            default:
                throw new IllegalArgumentException("Een verkeerd status werd meegegeven!");
        }
    }
}