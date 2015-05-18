package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.util.*;

/**
 * Bevat de stappen om de use cases "Create Task" en "Update Task" uit te voeren.
 */
public class TaskController extends AbstractController {

    private SystemTime systemTime;
    private LogonController logonController;

    /**
     * Constructor om een nieuwe task controller te maken.
     *
     * @param userInterface
     */
    public TaskController(LogonController logonController, SystemTime systemTime, UserInterface userInterface) {

        super(userInterface);
        this.logonController = logonController;
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
            BranchOffice selectedBO = logonController.getBranchOffice();
            ProjectRepository projectRepository = selectedBO.getProjectRepository();
            ResourceRepository resourceRepository = selectedBO.getResourceRepository();

            Project project = getUserInterface().selectProjectFromList(projectRepository.getProjects());
            String description = getUserInterface().requestString("Beschrijving:");
            Double acceptableDeviation = getUserInterface().requestDouble("Aanvaardbare afwijking in procent:") / 100;
            Duration estimatedDuration = Duration.ofMinutes(getUserInterface().requestNumber("Geschatte duur in minuten:"));

            // Lees alle resource types in.
            Map<AResourceType, Integer> selectedTypes = new HashMap<>();
            ImmutableList<AResourceType> resourceTypes = resourceRepository.getPresentResourceTypes();//Veronderstelling zonder developers

            // Laat gebruiker een aantal developers kiezen

            //TODO ui selected number between min = 1 & max = selectedBO.amountOfDevelopers()
            int nbDevelopers = getUserInterface().requestNumber("Hoeveel developers zijn er nodig?");
//            selectedTypes = addToResourceMap(resourceManager.getDeveloperType(), nbDevelopers, selectedTypes); TODO fix

            // Laat gebruiker resource types selecteren.
            //Voor ieder beschikbaar type in de BO, selectNumberBetween ...

            String message = "Voeg resource types toe?";
            while (getUserInterface().requestBoolean(message)) {
                try {
                    AResourceType iResourceType = getUserInterface().selectFromList(resourceTypes, AResourceType::getName);
                    Integer number = getUserInterface().requestNumber("Hoeveel wil je er?");
                    selectedTypes = addToResourceMap(iResourceType, number, selectedTypes);
                    resourceTypes.remove(iResourceType);
                    printResourceMap(selectedTypes);
                    message = "\nWilt u nog resource types toevoegen?";
                } catch (EmptyListException e) {
                    getUserInterface().printMessage("Geen resource types om toe te voegen");
                }
            }

            // Lees de afhankelijkheden in.
            List<Task> tasks = new ArrayList<>(project.getTasks());
            List<Task> selectedTasks = new ArrayList<>();
            while (getUserInterface().requestBoolean("Voeg een afhankelijkheid toe?")) {
                if (tasks.isEmpty()) {
                    getUserInterface().printMessage("Geen taken om toe te voegen...");
                    break;
                } else {
                    getUserInterface().printMessage("Opmerking: de nieuwe taak zal van de hieronder gekozen taak afhangen.");
                    Task task = getUserInterface().selectTaskFromList(ImmutableList.copyOf(tasks));
                    tasks.remove(task);
                    selectedTasks.add(task);
                }
            }

            // Vraag de gebruiker dat dit een alternatieve taak voor iets is als dat kan.
            Task alternativeTaskFor = null;
            if ((!project.getFailedTasks().isEmpty()) && getUserInterface().requestBoolean("Is deze taak een alternatieve taak?")) {
                getUserInterface().printMessage("Deze taak zal een alternatieve taak zijn voor de geselecteerde taak.");
                alternativeTaskFor = getUserInterface().selectTaskFromList(project.getFailedTasks());
            }

            // Bouw de IRequirementList en voeg hem toe aan de taak.
            IRequirementList requirementList = buildIRequirementList(selectedTypes, resourceRepository);

            project.addNewTask(description, acceptableDeviation, estimatedDuration, requirementList);
            Task task = project.getTasks().get(project.getTasks().size() - 1);

            // opm.: het toevoegen van afhankelijke taken kan nog geen fouten veroorzaken,
            // dus het is geen probleem dat de taak al gecreëerd is
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
            BranchOffice selectedBO = logonController.getBranchOffice();
            ProjectRepository projectRepository = selectedBO.getProjectRepository();

            List<Task> tasks = new LinkedList<>(projectRepository.getAllAvailableTasks());
            tasks.addAll(projectRepository.getAllExecutingTasks());
            Task task =  getUserInterface().selectTaskFromList(ImmutableList.copyOf(tasks));
            updateTask(task);
        }
        catch (CancelException|EmptyListException e) {
            getUserInterface().printException(e);
        }
    }

    /**
     * Voegt een AResourceType number keer toe aan de gegeven map.
     * @param iResourceType Het AResourceType dat moet worden toegevoegd.
     * @param number Het aantal keer dat AResourceType moet worden toegevoegd.
     * @param map De map waaraan alles moet warden toegevoegd.
     * @return Een geupdate versie van de doorgegeven map.
     */
    private Map<AResourceType, Integer> addToResourceMap(AResourceType iResourceType, Integer number, Map<AResourceType, Integer> map){
        if(map.containsKey(iResourceType)){
            map.put(iResourceType, map.get(iResourceType) + number);
        } else {
            map.put(iResourceType, number);
        }
        return map;
    }

    /**
     * Laat de huidige resourceMap zien aan de gebruiker via de UserInterface.
     * @param map De map die dient afgedrukt te worden.
     */
    private void printResourceMap(Map<AResourceType, Integer> map){
        getUserInterface().printMessage("De volgende resource zijn al geselecteerd:\n");
        map.forEach((x, y) -> getUserInterface().printMessage("\t" + x.getName() + ": " + y));
    }

    /**
     * Maakt gebruikt van een RequirementListBuilder om een IRequirementList aan te maken van de gegeven map.
     * @param map De map waarvan de IRequirementList moet worden opgebouwd.
     * @return De IRequirementList na het lezen van de gegeven map.
     */
    private IRequirementList buildIRequirementList(Map<AResourceType, Integer> map, ResourceRepository resourceRepository){
        RequirementListBuilder builder = new RequirementListBuilder(resourceRepository);
        map.forEach(builder::addNewRequirement);
        return builder.getRequirements();
    }

    /**
     * Vraagt de gebruiker wat de nieuwe status van een taak moet worden.
     * @param task De taak waarvan de status moet geupdate worden.
     * @throws CancelException Wordt gegooid als de gebruiker wil cancellen.
     */
    private void updateTask(Task task) throws CancelException{
        try {
            ArrayList<String> options = new ArrayList<>(Arrays.asList("FAIL", "FINISH", "EXECUTE"));
            String status = getUserInterface().selectFromList(options, x -> x);
            doTransition(status, task);

        }
        catch (IllegalArgumentException|IllegalStateTransitionException e) {
            getUserInterface().printException(e);
            updateTask(task);
        }
    }

    /**
     * Maakt de call naar de backend om van status te veranderen.
     * @param status De status in String vorm naar waar veranderd moet worden.
     * @param task De Task die de status overgang moet maken.
     * @throws IllegalArgumentException Wordt gegooid indien de status overgang niet mag.
     */
    private void doTransition(String status, Task task) throws IllegalArgumentException {
        status = status.toLowerCase();
        switch (status) {
            case "EXECUTE":
                task.execute(getUserInterface().requestDatum("Starttijd:"));
                // TODO: planned of unplanned execution? (unplanned => extra reservaties maken = mogelijk)
                break;
            case "FAIL":
                task.fail(getUserInterface().requestDatum("Eindtijd:"));
                break;
            case "FINISH":
                task.finish(getUserInterface().requestDatum("Eindtijd:"));
                break;
            default:
                throw new IllegalArgumentException("Een verkeerd status werd meegegeven!");
        }
    }
}