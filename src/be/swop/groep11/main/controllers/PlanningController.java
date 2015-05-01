package be.swop.groep11.main.controllers;

import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.exception.EmptyListException;
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

    private ProjectRepository projectRepository;
    private ResourceManager resourceManager;
    private SystemTime systemTime;

    private UserInterface ui;

    /**
     * Constructor om een nieuwe PlanningController aan te maken.
     * @param projectRepository De ProjectRepository waarin gewerkt dient te worden.
     * @param resourceManager De ResourceManager die alle resources bijhoudt waarmee gewerkt dient te worden.
     * @param systemTime De SystemTime.
     * @param userInterface De concrete implementatie van de UserInterface.
     */
    public PlanningController(ProjectRepository projectRepository, ResourceManager resourceManager, SystemTime systemTime, UserInterface userInterface) {
        super(userInterface);
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
        this.systemTime = systemTime;
        this.ui = getUserInterface();
    }

    /**
     * Start met het plannen van een taak. Alle benodigde info wordt aan de gebruiker gevraagd via de UserInterface.
     */
    public void planTask(){

        try {

            /* 2. The system shows a list of all currently unplanned tasks and the project
                they belong to
               3. The user selects the task he wants to plan. */

            Task task = ui.selectTaskFromList(this.requestUnplannedTasks());

            List<Plan> plans = planTask(task);

            /* 10. The system makes the required reservations and assigns the selected
                developers. */

            // op dit moment zouden er geen conflicten in de plannen mogen zitten
            // hopelijk is dit ook zo...

            for (Plan plan : plans) {
                plan.getTask().plan(plan);
                ui.printMessage("Taak gepland ("+task.getDescription()+")");
            }

        }

        catch (EmptyListException|CancelException|IllegalStateException e) {
            getUserInterface().printException(e);
        }
    }

    /**
     * Plant een taak.
     * @param task De taak die gepland moet worden.
     * @return Een lijst met plannen.
     */
    private List<Plan> planTask(Task task) {
        List<Plan> plans = new ArrayList<>();

        ui.printMessage("Maak een plan voor de taak: " + task.getDescription());

    /* 4. The system shows the first three possible starting times (only consid-
            ering exact hours, e.g. 09:00, and counting from the current system
            time) that a task can be planned (i.e. enough resource instances and
            developers are available) */

        Map<LocalDateTime, Plan> plansMap = requestNextStartTimes(3, task);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String msgTimes = "De eerste 3 mogelijke starttijden zijn:\n";
            for (LocalDateTime startTime : plansMap.keySet()) {
                msgTimes += " " + startTime.format(formatter) + "\n";
            }
            ui.printMessage(msgTimes);

            LocalDateTime startTime = null;
            if (ui.requestBoolean("Een starttijd hieruit selecteren?")) {
        /* 5. The user selects a proposed time. */
                startTime = ui.selectLocalDateTimeFromList(new LinkedList<LocalDateTime>(plansMap.keySet()));
            } else {
        /* 5a. The user indicates he wants to select another time */
                while (startTime == null || startTime.getMinute() != 0 || startTime.isBefore(systemTime.getCurrentSystemTime())) {
                    startTime = ui.requestDatum("Kies een starttijd (moet op een uur vallen en mag niet voor de huidige systeemtijd (" + systemTime.getCurrentSystemTime().format(formatter) + ") vallen:");
                }
            }

    /* 6. The system confirms the selected planned timespan and shows the re-
            quired resource types and their necessary quantity as assigned by the
            project manager when creating the task. For each required resource
            type instance to perform the task, the system proposes a resource instance
            to make a reservation for. */

            Plan plan = resourceManager.getCustomPlan(task, startTime);

        try {

            ui.printMessage("Gekozen starttijd: " + plan.getStartTime().format(formatter));

            // toon de voorgestelde reservaties
            String msgDefaultResourceInstances = "Voorgestelde reservaties:";
            Iterator<ResourceRequirement> it1 = task.getRequirementList().iterator();
            while (it1.hasNext()) {
                ResourceRequirement requirement = it1.next();
                AResourceType type = requirement.getType();
                if (type != resourceManager.getDeveloperType()) {
                    msgDefaultResourceInstances += "\n - Voor type " + type.getName() + ": (" + requirement.getAmount() + " instanties nodig)";

                    for (ResourceInstance resourceInstance : type.getResourceInstances()) {
                        if (plan.hasReservationFor(resourceInstance)) {
                            msgDefaultResourceInstances += "\n    - " + resourceInstance.getName();
                        }
                    }
                }
            }
            ui.printMessage(msgDefaultResourceInstances);

    /* 7. The user allows the system to select the required resources. */

            if (ui.requestBoolean("Deze resource instanties reserveren?")) {
                // doe niets en behoud de reservaties van het plan
            } else {

                // laat gebruiker resource instanties selecteren
                List<ResourceInstance> selectedInstances = new ArrayList<>();
                Iterator<ResourceRequirement> it2 = task.getRequirementList().iterator();
                while (it2.hasNext()) {
                    ResourceRequirement requirement = it2.next();
                    AResourceType type = requirement.getType();
                    if (type != resourceManager.getDeveloperType()) {
                        String msgSelectResourceInstances = "Selecteer instanties voor type " + type.getName() + " (" + requirement.getAmount() + " instanties nodig)";

                        List<ResourceInstance> allInstances = type.getResourceInstances();
                        List<ResourceInstance> defaultSelectedInstances = new ArrayList<>();
                        int nbInstances = requirement.getAmount();

                        for (ResourceInstance resourceInstance : type.getResourceInstances()) {
                            if (plan.hasReservationFor(resourceInstance)) {
                                defaultSelectedInstances.add(resourceInstance);
                            }
                        }

                        Function<ResourceInstance, String> entryPrinter = s -> s.getName();
                        List<ResourceInstance> instances = ui.selectMultipleFromList(msgSelectResourceInstances, allInstances, defaultSelectedInstances, nbInstances, true, entryPrinter);
                        selectedInstances.addAll(instances);
                    }
                }

                // verander de reservaties van het plan:
                plan.changeReservations(selectedInstances);

            }

            // zijn de reservaties geldig? (zonder rekening te houden met reservaties voor developers, dat gebeurt pas hieronder)
            if (!plan.isValidWithoutDevelopers()) {
                throw new ConflictException("Conflict!");
            }

    /* 8. The system shows a list of developers
       9. The user selects the developers to perform the task. */

            String msgSelectDevelopers = "Selecteer developers";
            AResourceType developerType = resourceManager.getDeveloperType();
            int nbDevelopers = getNbRequiredDevelopers(task.getRequirementList());
            ImmutableList<ResourceInstance> allDevelopers = developerType.getResourceInstances();
            ImmutableList<ResourceInstance> availableDevelopers = resourceManager.getAvailableInstances(developerType, new TimeSpan(plan.getStartTime(), plan.getEndTime()));
            Function<ResourceInstance, String> entryPrinter = s -> {
                if (availableDevelopers.contains(s)) {
                    return s.getName() + " (beschikbaar)";
                } else {
                    return s.getName() + " (niet beschikbaar)";
                }
            };
            List<ResourceInstance> selectedDevelopers = ui.selectMultipleFromList(msgSelectDevelopers, allDevelopers, new ArrayList<>(), nbDevelopers, true, entryPrinter);
            plan.addReservations(selectedDevelopers);

            // zijn de reservaties geldig?
            if (!plan.isValid()) {
                throw new ConflictException("Conflict!");
            }
        }

        catch (ConflictException e) {
            return resolveConflict(task, plan);
        }

        plans.add(plan);
        return plans;
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
            if (resourceType == resourceManager.getDeveloperType()) {
                return requirement.getAmount();
            }
        }
        return 0;
    }

    /**
     * Haalt alle ongeplande taken op it de projectRepository.
     * @return een ImmutableList met alle ongeplande taken in.
     */
    private ImmutableList<Task> requestUnplannedTasks() {
        List<Task> tasks = new LinkedList<>();
        for (Project project : projectRepository.getProjects()) {
            for (Task task : project.getTasks()) {
                if (! task.isPlanned()) {
                    tasks.add(task);
                }
            }
        }
        return ImmutableList.copyOf(tasks);
    }

    /**
     * Haalt de volgende n mogelijke starttijden op uit de resourceManager.
     * @param n Hoeveel tijden er moeten opgehaald worden.
     * @param task De Task waarvoor er tijden opgehaald moeten worden.
     * @return Een map met de starttijd en het plan.
     */
    private Map<LocalDateTime,Plan> requestNextStartTimes(int n, Task task) {
        Map<LocalDateTime,Plan> map = new LinkedHashMap<>();
        List<Plan> plans = resourceManager.getNextPlans(n, task, systemTime.getCurrentSystemTime());
        for (Plan plan : plans) {
            map.put(plan.getStartTime(), plan);
        }
        return map;
    }

    /**
     * Voer de stappen uit voor de resolveConflict uitbreiding.
     * @param task De taak waarvoor het conflict moet wordne opgelost.
     * @param plan Het conflicterende plan.
     * @return Een lijst met plannen.
     */
    private List<Plan> resolveConflict(Task task, Plan plan) {
        List<Plan> plans = new ArrayList<>();

        String msgConflictingTasks = "De planning voor de taak "+task.getDescription()+" conflicteert met de volgende taken:";
        for (Task conflictingTask : plan.getConflictingTasks()) {
            msgConflictingTasks += "\n - "+conflictingTask.getDescription();
        }
        ui.printMessage(msgConflictingTasks);

        if (ui.requestBoolean("Verplaats de conflicterende taken?")) {
            int i=1;
            for (Task conflictingTask : plan.getConflictingTasks()) {
                try {
                    ui.printMessage("Verplaats conflicterende taak " + i);
                    plans.addAll(planTask(conflictingTask));
                }
                catch(CancelException e) {
                    getUserInterface().printException(e);
                }
                i++;
            }
        }

        List<Plan> newPlansForTask = planTask(task);
        plans.addAll(newPlansForTask);

        return plans;
    }
}