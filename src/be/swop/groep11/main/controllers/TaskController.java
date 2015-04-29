package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bevat de stappen om de use cases "Create Task" en "Update Task" uit te voeren.
 */
public class TaskController extends AbstractController {

    private ProjectRepository projectRepository;
    private SystemTime systemTime;
    private  ResourceManager resourceManager;

    /**
     * Constructor om een nieuwe task controller te maken.
     * @param userInterface
     */
    public TaskController( ProjectRepository projectRepository, SystemTime systemTime, UserInterface userInterface, ResourceManager resourceManager) {
        super(userInterface);
        this.projectRepository = projectRepository;
        this.systemTime = systemTime;
        this.resourceManager = resourceManager;
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

            // Lees alle resource types in.
            Map<AResourceType, Integer> selectedTypes = new HashMap<>();
            List<AResourceType> resourceTypes = new ArrayList<>(resourceManager.getResourceTypes());
            resourceTypes.remove(resourceManager.getDeveloperType());

            // Laat gebruiker een aantal developers kiezen
            int nbDevelopers = getUserInterface().requestNumber("Hoeveel developers zijn er nodig?");
            selectedTypes = addToResourceMap(resourceManager.getDeveloperType(), nbDevelopers, selectedTypes);

            // Laat gebruiker resource types selecteren.
            String message = "Voeg resource types toe? (y/N)";
            while (getUserInterface().requestBoolean(message)){
                try {
                    AResourceType iResourceType = getUserInterface().selectFromList(resourceTypes, (x -> x.getName()));
                    Integer number = getUserInterface().requestNumber("Hoeveel wil je er?");
                    selectedTypes = addToResourceMap(iResourceType, number, selectedTypes);
                    resourceTypes.remove(iResourceType);
                    printResourceMap(selectedTypes);
                    message = "\nWilt u nog resource types toevoegen? (y/N)";
                }
                catch (EmptyListException e) {
                    getUserInterface().printMessage("Geen resource types om toe te voegen");
                }
            }

            // Lees de afhankelijkheden in.
            List<Task> tasks = new ArrayList<>(project.getTasks());
            List<Task> selectedTasks = new ArrayList<>();
            while ( getUserInterface().requestString("Voeg een afhankelijkheid toe? (y/N)").trim().equalsIgnoreCase("y")) {
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

            // Vraag de gebruiker dat dit een alternatieve taak voor iets is als dat kan.
            Task alternativeTaskFor = null;
            if ( (! project.getFailedTasks().isEmpty()) &&  getUserInterface().requestString("Is deze taak een alternatieve taak? (y/N)").trim().equalsIgnoreCase("y")) {
                getUserInterface().printMessage("Deze taak zal een zal een alternatieve taak zijn voor de geselecteerde taak.");
                alternativeTaskFor =  getUserInterface().selectTaskFromList(project.getFailedTasks());
            }

            project.addNewTask(description, acceptableDeviation, estimatedDuration);
            // opm.: het toevoegen van afhankelijke taken kan nog geen fouten veroorzaken,
            // dus het is geen probleem dat de taak al gecreëerd is
            Task task = project.getTasks().get(project.getTasks().size()-1);

            // Bouw de IRequirementList en voeg hem toe aan de taak.
            task.setRequirementList(buildIRequirementList(selectedTypes));

            for (Task dependingOn : selectedTasks) {
                task.addNewDependencyConstraint(dependingOn);
            }
            if (alternativeTaskFor != null) {
                alternativeTaskFor.setAlternativeTask(project.getTasks().get(project.getTasks().size() - 1));
            }
            getUserInterface().printMessage("Taak toegevoegd");
        }
        catch (IllegalArgumentException|IllegalRequirementAmountException e) {
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

    private Map<AResourceType, Integer> addToResourceMap(AResourceType iResourceType, Integer number, Map<AResourceType, Integer> map){
        if(map.containsKey(iResourceType)){
            map.put(iResourceType, map.get(iResourceType) + number);
        } else {
            map.put(iResourceType, number);
        }
        return map;
    }

    private void printResourceMap(Map<AResourceType, Integer> map){
        getUserInterface().printMessage("De volgende resource zijn al geselecteerd:\n");
        map.forEach((x, y) -> getUserInterface().printMessage("\t" + x.getName() + ": " + y));
    }

    private IRequirementList buildIRequirementList(Map<AResourceType, Integer> map){
        RequirementListBuilder builder = new RequirementListBuilder();
        map.forEach(builder::addNewRequirement);
        return builder.getRequirements();
    }

    private void updateTask(Task task) throws CancelException{
        try {
            String status =  getUserInterface().requestString("Status: FAILED of FINISHED (of laat leeg om status niet te wijzigen):");
            //TODO implement dit als een selectie uit een lijst. zodat de gebruiker geen verkeerde input kan geven. (ook makkeljker om te testen)
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