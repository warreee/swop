package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Ronald on 22/04/2015.
 */
public class PlanningController extends AbstractController {

    private ProjectRepository projectRepository;
    private ResourceManager resourceManager;
    private SystemTime systemTime;

    public PlanningController(ProjectRepository projectRepository, ResourceManager resourceManager, SystemTime systemTime, UserInterface userInterface) {
        super(userInterface);
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
        this.systemTime = systemTime;
    }

    //TODO implement resolve & plan

    public void planTask(){

        try {

            UserInterface ui = getUserInterface();

        /* 2. The system shows a list of all currently unplanned tasks and the project
                they belong to
           3. The user selects the task he wants to plan. */

            Task task = ui.selectTaskFromList(this.requestUnplannedTasks());

        /* 4. The system shows the first three possible starting times (only consid-
                ering exact hours, e.g. 09:00, and counting from the current system
                time) that a task can be planned (i.e. enough resource instances and
                developers are available) */

            Map<LocalDateTime, IPlan> plansMap = requestNextStartTimes(3, task);

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
                while (startTime == null || startTime.getMinute() == 0 || startTime.isBefore(systemTime.getCurrentSystemTime())) {
                    startTime = ui.requestDatum("Kies een starttijd (moet op een uur vallen en mag niet voor de huidige systeemtijd (" + systemTime.getCurrentSystemTime().format(formatter) + ") vallen:");
                }
            }

        /* 6. The system confirms the selected planned timespan and shows the re-
                quired resource types and their necessary quantity as assigned by the
                project manager when creating the task. For each required resource
                type instance to perform the task, the system proposes a resource instance
                to make a reservation for. */

            IPlan plan = resourceManager.getNextPlans(1, task, startTime).get(0);

            ui.printMessage("Gekozen starttijd: " + plan.getStartTime().format(formatter));

            // toon de voorgestelde reservaties
            String msgDefaultResourceInstances = "Voorgestelde reservaties:";
            Iterator<ResourceRequirement> it1 = task.getRequirementList().iterator();
            while (it1.hasNext()) {
                ResourceRequirement requirement = it1.next();
                IResourceType type = requirement.getType();
                msgDefaultResourceInstances += " - Voor type " + type.getName() + ": (" + requirement.getAmount() + " instanties nodig)\n";

                for (ResourceInstance resourceInstance : type.getResourceInstances()) {
                    if (plan.hasReservationFor(resourceInstance)) {
                        msgDefaultResourceInstances += "    - "+resourceInstance.getName() + "\n";
                    }
                }
            }
            ui.printMessage(msgDefaultResourceInstances);

        /* 7. The user allows the system to select the required resources. */

            if (ui.requestBoolean("Deze resource instanties reserveren?")) {
                // doe niets en behoud de reservaties van het plan
            } else {

                // kies resource instanties
                List<ResourceInstance> selectedInstances = new ArrayList<>();
                Iterator<ResourceRequirement> it2 = task.getRequirementList().iterator();
                while (it2.hasNext()) {
                    ResourceRequirement requirement = it2.next();
                    IResourceType type = requirement.getType();
                    String msgSelectResourceInstances = "Kies instanties voor type " + type.getName() + " (" + requirement.getAmount() + " instanties nodig)";

                    List<ResourceInstance> allInstances = type.getResourceInstances();
                    List<ResourceInstance> defaultSelectedInstances = new ArrayList<>();
                    int maxInstances = requirement.getAmount();

                    for (ResourceInstance resourceInstance : type.getResourceInstances()) {
                        if (plan.hasReservationFor(resourceInstance)) {
                            defaultSelectedInstances.add(resourceInstance);
                        }
                    }

                    // TODO: selectedInstances.addAll(ui.selectMultipleInstancesFromList(msgSelectResourceInstances, allInstances, defaultSelectedInstances, maxInstances))
                }

                // verander de reservaties van het plan:
                plan.changeReservations(selectedInstances);

            }

            // zijn de reservaties geldig?
            if (! plan.isValidPlan()) {
                resolveConflict(task, plan);
            }

        /* 8. The system shows a list of developers */

        /* 9. The user selects the developers to perform the task. */

            IResourceType developerType = resourceManager.getDeveloperType();
            ImmutableList<ResourceInstance> availableDevelopers = resourceManager.getAvailableInstances(developerType, new TimeSpan(plan.getStartTime(), plan.getEndTime()));
            // TODO: select multiple developers from list

        /* 10. The system makes the required reservations and assigns the selected
                developers. */

            // TODO: hiervoor moet nog een methode in resource manager komen (om alle reservaties van een plan aan de resource manager toe te voegen)

        }

        catch (EmptyListException|CancelException e) {
            getUserInterface().printException(e);
        }
    }

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

    private Map<LocalDateTime,IPlan> requestNextStartTimes(int n, Task task) {
        Map<LocalDateTime,IPlan> map = new LinkedHashMap<>();
        List<IPlan> plans = resourceManager.getNextPlans(3, task, systemTime.getCurrentSystemTime());
        for (IPlan plan : plans) {
            map.put(plan.getStartTime(), plan);
        }
        return map;
    }

    private void resolveConflict(Task task, IPlan plan) {
        // TODO
    }

}
