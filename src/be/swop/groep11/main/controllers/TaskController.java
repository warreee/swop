package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.RequirementListBuilder;
import be.swop.groep11.main.resource.ResourceRepository;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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
            ImmutableList<AResourceType> resourceTypes = resourceRepository.getPresentResourceTypes();//Veronderstelling zonder developers
            //Start RequirementListBuilder
            RequirementListBuilder requirementListBuilder = new RequirementListBuilder(resourceRepository);

            // Laat gebruiker een aantal developers kiezen
            int min = 1;
            int max = selectedBO.amountOfDevelopers();
            int nbDevelopers = getUserInterface().requestNumberBetween("Hoeveel developers zijn er nodig?", min, max);
            requirementListBuilder.addNewRequirement(resourceRepository.getDeveloperType(),nbDevelopers);

            // Laat gebruiker resource types selecteren.
            //Voor ieder beschikbaar type in de BO, selectNumberBetween ...
            if (getUserInterface().requestBoolean("Voeg resource types toe?")) {
                Function<AResourceType,String> typePrinter = type -> type.getTypeName() + " " + type.getDailyAvailability().toString();
                List<AResourceType> selectionTypes = getUserInterface().selectMultipleFromList("selecteer types", resourceTypes, new ArrayList<>(), typePrinter);

                selectionTypes.forEach(type -> {
                    int amount = getUserInterface().requestNumberBetween("Hoeveel instanties van " + type.getTypeName() + " nodig?", 1, resourceRepository.amountOfResourceInstances(type));
                    requirementListBuilder.addNewRequirement(type, amount);
                });
            }

            // Lees de afhankelijkheden in.
            List<Task> dependencyTasks = new ArrayList<>();
            if (!project.getTasks().isEmpty() && getUserInterface().requestBoolean("Voeg een afhankelijkheid toe?")) {
                List<Task> dependencies = getUserInterface().selectMultipleFromList("selecteer afhankelijkheden",project.getTasks(), new ArrayList<>(),getUserInterface().getTaskPrinter());
                dependencyTasks.addAll(dependencies);
            }

            // Vraag de gebruiker dat dit een alternatieve taak voor iets is als dat kan.
            Task alternativeTaskFor = null;
            if ((!project.getFailedTasks().isEmpty()) && getUserInterface().requestBoolean("Is deze taak een alternatieve taak?")) {
                getUserInterface().printMessage("Deze taak zal een alternatieve taak zijn voor de geselecteerde taak.");
                alternativeTaskFor = getUserInterface().selectTaskFromList(project.getFailedTasks());

            }

            // Bouw de IRequirementList en voeg hem toe aan de taak.
            project.addNewTask(description, acceptableDeviation, estimatedDuration, requirementListBuilder.getRequirements());
            Task task = project.getLastAddedTask();

            // opm.: het toevoegen van afhankelijke taken kan nog geen fouten veroorzaken,
            // dus het is geen probleem dat de taak al gecreëerd is
            dependencyTasks.forEach(task::addNewDependencyConstraint);
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
            Function<Task,String> taskPrinter = tsk -> tsk.getDescription() + " " + tsk.getStatusString() ;
            Task task = getUserInterface().selectFromList(projectRepository.getAllAvailableOrExecutingTasks(), taskPrinter);

//            Task task =  getUserInterface().selectTaskFromList();
            updateTask(task);

        }
        catch (CancelException|EmptyListException e) {
            getUserInterface().printException(e);
        }
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
        status = status.toUpperCase();
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