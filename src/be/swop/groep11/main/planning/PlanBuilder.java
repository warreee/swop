package be.swop.groep11.main.planning;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.mockito.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Een PlanBuilder bevat methodes om een Plan te maken voor een taak in een bepaalde branch office.
 */
public class PlanBuilder {

    private BranchOffice branchOffice;
    private Task task;

    private List<ResourceRequirement> resourceRequirements;
    private HashMap<AResourceType, ArrayList<ResourceInstance>> specificInstances;
    private HashMap<AResourceType, ArrayList<ResourceInstance>> proposedInstances;

    private LocalDateTime startTime, endTime;

    /**
     * Maakt een nieuwe PlanBuilder voor een taak aan die werkt met de gegeven BranchOffice.
     * @param branchOffice Het BranchOffice waarmee deze PlanBuilder gaat werken.
     * @param task         De taak waarvoor een plan moet gemaakt worden.
     * @param startTime    De starttijd van het plan.
     * @throws IllegalArgumentException Ongeldige waarde voor branchOffice, task of startTime
     *                      | ! isValidBranchOffice(branchOffice)
     *                      | ! canHaveAsTask(task)
     *                      | ! isValidStartTime(startTime)
     */
    public PlanBuilder(BranchOffice branchOffice, Task task, LocalDateTime startTime) {
        setBranchOffice(branchOffice);
        setTask(task);
        setStartTime(startTime);

        resourceRequirements = new ArrayList<>();
        specificInstances = new HashMap<>();
        proposedInstances = new HashMap<>();

        Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
        while (it.hasNext()) {
            ResourceRequirement resourceRequirement = it.next();
            resourceRequirements.add(resourceRequirement);
            specificInstances.put(resourceRequirement.getType(), new ArrayList<>());
            proposedInstances.put(resourceRequirement.getType(), new ArrayList<>());
        }

        setEndTime();
    }


    /**
     * Stelt resource instanties voor, door de huidige gekozen resource instanties aan te vullen
     * met andere resource instanties zodat aan de requirement lijst van de taak voldaan is.
     */
    public void proposeResources() {
        for (ResourceRequirement requirement : this.resourceRequirements) {
            AResourceType type = requirement.getType();
            int nbRequiredInstances = requirement.getAmount();
            int nbSelectedInstances = getSelectedInstances(requirement.getType()).size();
            for (int i=0; i<nbRequiredInstances-nbSelectedInstances; i++) {
                // welke instances kunnen nog gekozen worden?
                List<ResourceInstance> instancesLeft = branchOffice.getResourcePlanner().getInstances(
                        type, getTimeSpan()).stream().filter(x -> !getSelectedInstances(type).contains(x)).collect(Collectors.toList());
                if (instancesLeft.size() > 0) {
                    addResourceInstance(instancesLeft.get(0));
                }
            }
        }
    }

    /**
     * Zorgt ervoor dat een specifieke ResourceInstance voor het gegenereerde plan gebruikt gaat worden.
     * @param resourceInstance De ResourceInstance die gebruikt moet worden.
     */
    public void addResourceInstance(ResourceInstance resourceInstance) {
        checkCanAddResourceInstance(resourceInstance); // Gooit exceptions indien niet.
        specificInstances.getOrDefault(resourceInstance.getResourceType(), new ArrayList<>()).add(resourceInstance);

    }

    /**
     * Controleert of de gegeven resourceInstance wel aan deze PlanBuilder kan worden toegevoegd.
     */
    private void checkCanAddResourceInstance(ResourceInstance resourceInstance) {
        if (resourceInstance == null) {
            throw new IllegalArgumentException("ResourceInstance mag niet null zijn.");
        }
        int taskTypeAmount = task.getRequirementList().getRequirementFor(resourceInstance.getResourceType()).getAmount();
        if (taskTypeAmount <= 0) {
            throw new IllegalArgumentException("De taak heeft geen requirements voor de gegeven ResourceInstance.");
        }
        int builderTypeAmount = specificInstances.getOrDefault(resourceInstance.getResourceType(), new ArrayList<>()).size();
        if (taskTypeAmount <= builderTypeAmount) {
            throw new IllegalArgumentException("Voor de gegeven ResourceInstance zijn al voldoende ResourceInstances " +
                    "van zijn ResourceType toegevoegd.");
        }
    }

    /**
     * Controleert of er conflicterende reservaties zijn voor het plan.
     * @return
     */
    public boolean hasConflictingReservations() {
        // TODO
        return false;
    }

    /**
     * Controleert of aan alle resource requirements van de taak voldaan zijn.
     * @return
     */
    public boolean isSatisfied() {
        // TODO
        return false;
    }

    /**
     * Geeft het gemaakte plan terug.
     * @return
     * @throws IllegalArgumentException Het plan heeft conflicterende reservaties of voldoet niet aan de requirements.
     *                                  | hasConflictingReservations() || ! isSatisfied()
     */
    public Plan getPlan() {
        // TODO
        return null;
    }

    private void setBranchOffice(BranchOffice branchOffice) {
        if (!isValidBranchOffice(branchOffice)) {
            throw new IllegalArgumentException("Branch Office mag niet null zijn bij het aanmaken van een PlanBuilder.");
        }
        this.branchOffice = branchOffice;
    }

    /**
     * Controleert of een branch office geldig is.
     * @return branchOffice != null
     */
    public static boolean isValidBranchOffice(BranchOffice branchOffice) {
        return branchOffice != null;
    }

    private void setTask(Task task) {
        canHaveAsTask(task);
        this.task = task;
    }

    /**
     * Controleert of een taak geldig is voor deze plan builder.
     * Hiervoor moet eerst de branch office gezet zijn.
     * @return Als taak niet null is en de taak in de lijst van ongeplande taken van de branch office zit.
     */
    public void canHaveAsTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task mag niet null zijn.");
        }
        if (branchOffice == null) {
            throw new IllegalArgumentException("BranchOffice moet voor Task gezet worden.");
        }
        if (!branchOffice.getUnplannedTasks().contains(task)) {
            throw new IllegalArgumentException("Task zit niet in de lijst van ongeplande taken van BranchOffice.");
        }
    }

    private void setStartTime(LocalDateTime startTime) {
        if (! isValidStartTime(startTime))
            throw new IllegalArgumentException("Starttijd mag niet null zijn");
        this.startTime = startTime;
    }

    private void setEndTime() {
        for (ResourceRequirement resourceRequirement : resourceRequirements) {
            AResourceType resourceType = resourceRequirement.getType();
            LocalDateTime resourceTypeEndTime = resourceType.calculateEndTime(startTime, task.getEstimatedDuration());
            if (resourceTypeEndTime.isAfter(endTime)) {
                this.endTime = resourceTypeEndTime;
            }
        }
    }

    /**
     * Controleert of een starttijd geldig is.
     * @return startTime != null & de starttijd valt op een uur (zonder minuten)
     */
    public static boolean isValidStartTime(LocalDateTime startTime) {
        return startTime != null && startTime.getMinute() == 0;
    }

    /**
     * Geeft de geselecteerde resource instanties voor een gegeven resource type.
     * @param resourceType Het resource type
     * @return Alle instanties uit specificInstances en proposedInstances voor het type.
     */
    private List<ResourceInstance> getSelectedInstances(AResourceType resourceType) {
        List<ResourceInstance> selectedInstances = new ArrayList<>();
        selectedInstances.addAll(specificInstances.get(resourceType));
        selectedInstances.addAll(proposedInstances.get(resourceType));
        return selectedInstances;
    }

    private TimeSpan getTimeSpan() {
        return new TimeSpan(startTime, endTime);
    }
}
