package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
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
import java.util.*;
import java.util.function.Function;

/**
 * Bevat de stappen om de usecase planTask uit te voeren.
 */
public class PlanningController extends AbstractController {

    private SystemTime systemTime;

    private UserInterface ui;
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
        this.ui = getUserInterface();
    }

    /**
     * Start met het plannen van een taak. Alle benodigde info wordt aan de gebruiker gevraagd via de UserInterface.
     */
    public void planTask(){

        BranchOffice branchOffice = logonController.getBranchOffice();


        try {

            /* The system shows a list of all currently unplanned tasks and the project
                they belong to. The user selects the task he wants to plan. */
            Task task = ui.selectTaskFromList(ImmutableList.copyOf(branchOffice.getUnplannedTasks()));

            List<Plan> plans = planTask(task,branchOffice);

            /* The system makes the required reservations and assigns the selected
                developers. */
            for (Plan plan : plans) {
                branchOffice.getResourcePlanner().addPlan(plan);
                task.setPlan(plan);
                ui.printMessage("Taak gepland ("+task.getDescription()+")");
            }

        } catch (EmptyListException|CancelException|IllegalStateException e) {
            getUserInterface().printException(e);
        }
    }

    /**
     * Plant een taak.
     * @param task De taak die gepland moet worden.
     * @return Een lijst met plannen.
     */
    private List<Plan> planTask(Task task,BranchOffice branchOffice) {
        ui.printMessage("Een plan maken voor de taak: " + task.getDescription());
        List<Plan> plans = new ArrayList<>();

        /* The user selects a start time */
        LocalDateTime startTime = selectStartTime(task, branchOffice.getResourcePlanner());
        PlanBuilder planBuilder = new PlanBuilder(branchOffice, task, startTime);
        try {

            /* The system confirms the selected planned timespan and shows the re-
                quired resource types and their necessary quantity as assigned by the
                project manager when creating the task. For each required resource
                type instance to perform the task, the system proposes a resource instance
                to make a reservation for. */
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            ui.printMessage("Gekozen starttijd: " + planBuilder.getTimeSpan().getStartTime().format(formatter));
            planBuilder.proposeResources(); //Stelt geen developers voor.
            this.showProposedInstances(task, planBuilder,branchOffice.getResourceRepository());
            this.selectResources(task, planBuilder,branchOffice.getResourceRepository());

            // zijn er nu al conflicten?
            if (planBuilder.hasConflictingReservations()) {
                throw new ConflictException("Conflict!");
            }

            /* The system shows a list of developers. The user selects the developers to perform the task. */
            this.selectDevelopers(task, branchOffice.getResourcePlanner(), planBuilder,branchOffice);

            // zijn de resources en developers geldig?
            if (planBuilder.hasConflictingReservations() || ! planBuilder.isSatisfied()) {
                throw new ConflictException("Conflict!");
            }
        }

        catch (ConflictException e) {
            List<ResourceInstance> instances = planBuilder.getSelectedInstances();
            TimeSpan timeSpan = planBuilder.getTimeSpan();
            List<Task> conflictingtasks = branchOffice.getResourcePlanner().getConflictingTasks(instances, timeSpan);
            return resolveConflict(task, conflictingtasks,branchOffice);
        }

        plans.add(planBuilder.getPlan());
        return plans;
    }

    private LocalDateTime selectStartTime(Task task, ResourcePlanner resourcePlanner) {

        /* The system shows the first three possible starting times (only consid-
            ering exact hours, e.g. 09:00, and counting from the current system
            time) that a task can be planned (i.e. enough resource instances and
            developers are available) */

        List<LocalDateTime> nextStartTimes = resourcePlanner.getNextPossibleStartTimes(
                task.getRequirementList(),
                systemTime.getCurrentSystemTime(),
                task.getEstimatedDuration(),
                3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = null;
        if (ui.requestBoolean("Wilt u mogelijke startTijden laten generen? (N: indien u zelf een startTijd wilt ingeven)")) {
            /* The user selects a proposed time. */
            Function<LocalDateTime, String> dateTimePrinter = localDateTime -> localDateTime.format(formatter);
            startTime = getUserInterface().selectFromList(nextStartTimes, dateTimePrinter);
        } else {
            /* The user indicates he wants to select another time */
            startTime = ui.requestDatum("Kies een starttijd (moet op een uur vallen en mag niet voor de huidige systeemtijd (" + systemTime.getCurrentSystemTime().format(formatter) + ") vallen:");
        }
        
        
        
//        String msgTimes = "De eerste 3 mogelijke starttijden zijn:\n";
//        for (LocalDateTime startTime : nextStartTimes) {
//            msgTimes += " " + startTime.format(formatter) + "\n";
//        }
//        ui.printMessage(msgTimes);

//        LocalDateTime startTime = null;
//        if (ui.requestBoolean("Een starttijd hieruit selecteren?")) {
//
//            startTime = ui.selectLocalDateTimeFromList(new LinkedList<>(nextStartTimes));
//        } else {
//        /* The user indicates he wants to select another time */
//            while (startTime == null || startTime.getMinute() != 0 || startTime.isBefore(systemTime.getCurrentSystemTime())) {
//                startTime = ui.requestDatum("Kies een starttijd (moet op een uur vallen en mag niet voor de huidige systeemtijd (" + systemTime.getCurrentSystemTime().format(formatter) + ") vallen:");
//            }
//        }

        return startTime;
    }

    private void showProposedInstances(Task task, PlanBuilder planBuilder,ResourceRepository resourceRepository) {
        String msgDefaultResourceInstances = "Voorgestelde reservaties:";
        Iterator<ResourceRequirement> it1 = task.getRequirementList().iterator();
        while (it1.hasNext()) {
            ResourceRequirement requirement = it1.next();
            AResourceType type = requirement.getType();
            if (! (type instanceof DeveloperType)) {
                msgDefaultResourceInstances += "\n - Voor type " + type.getTypeName() + ": (" + requirement.getAmount() + " instanties nodig)";

                for (ResourceInstance resourceInstance : resourceRepository.getResources(type)) {
                    if (planBuilder.getSelectedInstances(type).contains(resourceInstance)) {
                        msgDefaultResourceInstances += "\n    - " + resourceInstance.getName();
                    }
                }
            }
        }
        ui.printMessage(msgDefaultResourceInstances);
    }

    private void selectResources(Task task, PlanBuilder planBuilder,ResourceRepository resourceRepository) {

        /* The user allows the system to select the required resources. */

        if (ui.requestBoolean("Deze resource instanties reserveren?")) {
            // doe niets en behoud de resource instanties van het plan
        } else {

            // laat gebruiker resource instanties selecteren
            List<ResourceInstance> selectedInstances = new ArrayList<>();
            Iterator<ResourceRequirement> it2 = task.getRequirementList().iterator();
            while (it2.hasNext()) {
                ResourceRequirement requirement = it2.next();
                AResourceType type = requirement.getType();
                if (! (type instanceof DeveloperType)) {
                    String msgSelectResourceInstances = "Selecteer instanties voor type " + type.getTypeName() + " (" + requirement.getAmount() + " instanties nodig)";

                    List<ResourceInstance> allInstances = resourceRepository.getResources(type);
                    List<ResourceInstance> defaultSelectedInstances = new ArrayList<>();
                    int nbInstances = requirement.getAmount();

                    for (ResourceInstance resourceInstance : resourceRepository.getResources(type)) {
                        if (planBuilder.getSelectedInstances(resourceInstance.getResourceType()).contains(resourceInstance)) {
                            defaultSelectedInstances.add(resourceInstance);
                        }
                    }

                    Function<ResourceInstance, String> entryPrinter = s -> s.getName();
                    List<ResourceInstance> instances = ui.selectMultipleFromList(msgSelectResourceInstances, allInstances, defaultSelectedInstances, nbInstances, true, entryPrinter);
                    selectedInstances.addAll(instances);
                }
            }

            // verander de resource instanties:
            planBuilder.clearInstances();
            for (ResourceInstance instance : selectedInstances) {
                planBuilder.addResourceInstance(instance);
            }
        }
    }

    private void selectDevelopers(Task task, ResourcePlanner resourcePlanner, PlanBuilder planBuilder,BranchOffice branchOffice) {
        String msgSelectDevelopers = "Selecteer developers";
        AResourceType developerType =  resourcePlanner.getResourceRepository().getResourceTypeRepository().getDeveloperType();
        int nbDevelopers = getNbRequiredDevelopers(task.getRequirementList());
        ImmutableList<ResourceInstance> allDevelopers = branchOffice.getDevelopers();
        ImmutableList<ResourceInstance> availableDevelopers = ImmutableList.copyOf(resourcePlanner.getAvailableInstances(developerType, planBuilder.getTimeSpan()));
        Function<ResourceInstance, String> entryPrinter = s -> {
            if (availableDevelopers.contains(s)) {
                return s.getName() + " (beschikbaar)";
            } else {
                return s.getName() + " (niet beschikbaar)";
            }
        };
        List<ResourceInstance> selectedDevelopers = ui.selectMultipleFromList(msgSelectDevelopers, allDevelopers, new ArrayList<>(), nbDevelopers, true, entryPrinter);
        for (ResourceInstance developer : selectedDevelopers) {
            planBuilder.addResourceInstance(developer);
        }
    }

    /**
     * Bepaald uit de gegeven IRequirementList hoeveel developers er nodig zijn.
     * @param requirementList De te controlleren IRequirementList.
     * @return Het aantal benodigde developers.
     */
    private int getNbRequiredDevelopers(IRequirementList requirementList) {
        Iterator<ResourceRequirement> it = requirementList.iterator();
        while (it.hasNext()) {
            ResourceRequirement requirement = it.next();
            AResourceType resourceType = requirement.getType();
            if (resourceType instanceof DeveloperType) {
                return requirement.getAmount();
            }
        }
        return 0;
    }

    /**
     * Voer de stappen uit voor de resolveConflict uitbreiding.
     * @param task De taak waarvoor het conflict moet worden opgelost.
     * @param conflictingtasks De conflicterende taken
     * @return Een lijst met plannen.
     */
    private List<Plan> resolveConflict(Task task, List<Task> conflictingtasks,BranchOffice branchOffice) {
        List<Plan> plans = new ArrayList<>();

        String msgConflictingTasks = "De planning voor de taak "+task.getDescription()+" conflicteert met de volgende taken:";
        for (Task conflictingTask : conflictingtasks) {
            msgConflictingTasks += "\n - "+conflictingTask.getDescription();
        }
        ui.printMessage(msgConflictingTasks);

        if (ui.requestBoolean("Verplaats de conflicterende taken?")) {
            int i=1;
            for (Task conflictingTask : conflictingtasks) {
                try {
                    ui.printMessage("Verplaats conflicterende taak " + i);
                    plans.addAll(planTask(conflictingTask,branchOffice));
                }
                catch(CancelException e) {
                    getUserInterface().printException(e);
                }
                i++;
            }
        }

        List<Plan> newPlansForTask = planTask(task,branchOffice);
        plans.addAll(newPlansForTask);

        return plans;
    }
}