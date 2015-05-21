package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.ConflictException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Bevat de stappen om de usecase planTask uit te voeren.
 */
public class PlanningController extends AbstractController {

    private SystemTime systemTime;

    private LogonController logonController;

    /**
     * Constructor om een nieuwe PlanningController aan te maken.
     * @param systemTime De SystemTime.
     * @param userInterface De concrete implementatie van de UserInterface.
     */
    public PlanningController(LogonController logonController, SystemTime systemTime, UserInterface userInterface) {
        super(userInterface);
        this.logonController = logonController;
        this.systemTime = systemTime;
    }

    /**
     * Start met het plannen van een taak. Alle benodigde info wordt aan de gebruiker gevraagd via de UserInterface.
     */
    public void planTask(){

        BranchOffice branchOffice = logonController.getBranchOffice();
        try {

            /* The system shows a list of all currently unplanned tasks and the project
                they belong to. The user selects the task he wants to plan. */
            Task task = getUserInterface().selectTaskFromList(ImmutableList.copyOf(branchOffice.getUnplannedTasks()));

           planTask(task,branchOffice);
        } catch (EmptyListException|CancelException|IllegalStateException e) {
            getUserInterface().printException(e);
            getUserInterface().showHelp();
        }
    }

    /**
     * Plant een taak.
     * @param task De taak die gepland moet worden.
     * @return Een lijst met plannen.
     */
    private void planTask(Task task,BranchOffice branchOffice) {
         /* The system confirms the selected planned timespan and shows the re-
                quired resource types and their necessary quantity as assigned by the
                project manager when creating the task. For each required resource
                type instance to perform the task, the system proposes a resource instance
                to make a reservation for. */


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        /* The user selects a start time */
        LocalDateTime startTime = selectStartTime(task, branchOffice.getResourcePlanner());
        System.out.println(startTime);
        PlanBuilder planBuilder = new PlanBuilder(branchOffice, task, startTime);

        getUserInterface().printMessage("Een plan maken voor de taak: " + task.getDescription());
        getUserInterface().printMessage("Gekozen starttijd: " + startTime.format(formatter));

        planBuilder.proposeResources(); //Stelt geen developers voor.
        showProposedInstances(task, planBuilder);


        try {
            selectResources(task, planBuilder, branchOffice.getResourceRepository()); //checks & can throw ConflictException

         /* The system shows a list of developers. The user selects the developers to perform the task. */
            selectDevelopers(task, branchOffice.getResourcePlanner(), planBuilder, branchOffice);  //checks & can throw ConflictException

            Plan plan = planBuilder.getPlan();
            /* The system makes the required reservations and assigns the selected
                developers. */
            branchOffice.getResourcePlanner().addPlan(plan);

            getUserInterface().printMessage("Taak gepland (" + task.getDescription() + ")");
        } catch (ConflictException conflictException) {
            resolveConflict(task, conflictException.getConflictingTasks(),branchOffice);
            planTask(task,branchOffice);
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
        }
    }

    /**
     * Voer de stappen uit voor de resolveConflict uitbreiding.
     * @param task De taak waarvoor het conflict moet worden opgelost.
     * @param conflictingTasks De conflicterende taken
     * @return Een lijst met plannen.
     */
    private void resolveConflict(Task task, List<Task> conflictingTasks,BranchOffice branchOffice) {
        IResourcePlannerMemento memento = branchOffice.getResourcePlanner().createMemento();
        getUserInterface().printMessage("De volgende taken vormen een conflict met de in te plannen taak: " + task.getDescription());
        getUserInterface().showList(conflictingTasks, Task::getDescription);

        try {
            if (getUserInterface().requestBoolean("Wilt u de conflicterende taken verplaatsen?")) {
                conflictingTasks.forEach(taak -> {
                    Plan plan = branchOffice.getPlanForTask(taak);
                    plan.clear();//Taak plan delete!
                    planTask(taak,branchOffice);
                });
            }
        } catch (CancelException e) {
//            System.out.println("restoring memento");
            branchOffice.getResourcePlanner().setMemento(memento);
            throw e;
        }



    }


    private LocalDateTime selectStartTime(Task task, ResourcePlanner resourcePlanner) {
        /* The system shows the first three possible starting times (only consid-
            ering exact hours, e.g. 09:00, and counting from the current system
            time) that a task can be planned (i.e. enough resource instances and
            developers are available) */
        List<LocalDateTime> nextStartTimes = resourcePlanner.getNextPossibleStartTimes(
                task.getRequirementList(),systemTime.getCurrentSystemTime(),task.getEstimatedDuration(),3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime;
        if (getUserInterface().requestBoolean("Wilt u mogelijke startTijden laten generen? (N: indien u zelf een startTijd wilt ingeven)")) {
            /* The user selects a proposed time. */
            Function<LocalDateTime, String> dateTimePrinter = localDateTime -> localDateTime.format(formatter);
            startTime = getUserInterface().selectFromList(nextStartTimes, dateTimePrinter);
        } else {
            /* The user indicates he wants to select another time */
            startTime = getUserInterface().requestDatum("Kies een starttijd (moet op een uur vallen en mag niet voor de huidige systeemtijd (" + systemTime.getCurrentSystemTime().format(formatter) + ") vallen:");
        }
        return startTime;
    }

    private void showProposedInstances(Task task, PlanBuilder planBuilder) {
        getUserInterface().printMessage("Voorgestelde resources:");
        task.getRequirementList().getRequirements().forEach(resourceRequirement -> {
            if (!resourceRequirement.isDeveloperRequirement()) {
                getUserInterface().printMessage(resourceRequirement.toString());//voor
                List<ResourceInstance> instances = planBuilder.getSelectedInstances(resourceRequirement.getType());
                getUserInterface().showList(instances, resourceInstance -> resourceInstance.getName());
            }
        });
    }

    private void selectResources(Task task, PlanBuilder planBuilder,ResourceRepository resourceRepository) throws ConflictException {

        /* The user allows the system to select the required resources. */
        if (getUserInterface().requestBoolean("Wilt u zelf resources selecteren?")) {
            //Clear reeds door het systeem geselecteerde instanties
            planBuilder.clearInstances();
            // laat gebruiker resource instanties selecteren
            task.getRequirementList().getRequirements().stream().filter(req -> !req.isDeveloperRequirement()).forEach(resourceRequirement -> {
                AResourceType type = resourceRequirement.getType();
                List<ResourceInstance> instances = getUserInterface().selectMultipleFromList(
                        "request", resourceRepository.getResources(type),
                        new ArrayList<>(), resourceRequirement.getAmount(),
                        true, ri -> ri.getName()
                );
                instances.forEach(resourceInstance -> planBuilder.addResourceInstance(resourceInstance));
            });
            //Check for conflicting reservations
            planBuilder.checkForConflictingReservations();
        }
    }

    private void selectDevelopers(Task task, ResourcePlanner resourcePlanner, PlanBuilder planBuilder,BranchOffice branchOffice) throws ConflictException{
        String msgSelectDevelopers = "Selecteer developers";
        int nbDevelopers = task.getRequirementList().getRequiredDevelopers();

        Function<ResourceInstance, String> entryPrinter = resourceInstance -> {
            return resourcePlanner.isAvailable(resourceInstance,planBuilder.getTimeSpan())? resourceInstance.getName() + " (beschikbaar)": resourceInstance.getName() + " (niet beschikbaar)";
        };

        List<ResourceInstance> selectedDevelopers = getUserInterface().selectMultipleFromList(msgSelectDevelopers, branchOffice.getDevelopers(), new ArrayList<>(), nbDevelopers, true, entryPrinter);
        selectedDevelopers.forEach(planBuilder::addResourceInstance);
        planBuilder.checkForConflictingReservations();
    }
}