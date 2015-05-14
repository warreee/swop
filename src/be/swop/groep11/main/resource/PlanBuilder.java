package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by robin on 14/05/15.
 */
public class PlanBuilder {

    private BranchOffice branchOffice;
    private Task task;
    private HashMap<AResourceType, ArrayList<ResourceInstance>> specificInstances;
    private LocalDateTime startTime;

    /**
     * Maakt een nieuwe PlanBuilder aan die werkt met het gegeven BranchOffice.
     *
     * @param branchOffice Het BranchOffice waarmee deze PlanBuilder gaat werken.
     */
    public PlanBuilder(BranchOffice branchOffice, Task task, LocalDateTime startTime) {
        setBranchOffice(branchOffice);
        setTask(task);
        setStartTime(startTime);
        specificInstances = new HashMap<>();
    }

    /**
     * Zorgt ervoor dat een specifieke ResourceInstance voor het gegenereerde plan gebruikt gaat worden.
     *
     * @param resourceInstance De ResourceInstance die gebruikt moet worden.
     */
    public void addResourceInstance(ResourceInstance resourceInstance) {
        checkCanAddResourceInstance(resourceInstance); // Gooit exceptions indien niet.
        specificInstances.getOrDefault(resourceInstance.getResourceType(), new ArrayList<>()).add(resourceInstance);
    }

    /**
     * Controlleert of de gegeven resourceInstance wel aan deze PlanBuilder kan worden toegevoegd.
     *
     * @param resourceInstance
     */
    private void checkCanAddResourceInstance(ResourceInstance resourceInstance) {
        if (resourceInstance == null) {
            throw new IllegalArgumentException("ResourceInstance mag niet 'null' zijn.");
        }
        int taskTypeAmount = task.getRequirementList().getRequirementFor(resourceInstance.getResourceType()).getAmount();
        if (taskTypeAmount <= 0) {
            throw new IllegalArgumentException("De taak heeft geen requirements voor de gegeven ResourceInstance.");
        }
        int builderTypeAmount = specificInstances.getOrDefault(resourceInstance.getResourceType(), new ArrayList<>()).size();
        if (taskTypeAmount <= builderTypeAmount) {
            throw new IllegalArgumentException("Voor de gegeven ResourceInstance zijn al teveel ResourceInstances " +
                    "van zijn ResourceType toegevoegd.");
        }
    }

    public boolean hasConflictingReservations() {
        return false;
    }

    public boolean isSatisfied() {
        return false;
    }

    public Plan getPlan() {
        return null;
    }

    private void setBranchOffice(BranchOffice branchOffice) {
        if (!isValidBranchOffice(branchOffice)) {
            throw new IllegalArgumentException("Branch Office mag niet null zijn bij het aanmaken van een PlanBuilder.");
        }
        this.branchOffice = branchOffice;
    }

    private boolean isValidBranchOffice(BranchOffice branchOffice) {
        return branchOffice != null;
    }

    private void setTask(Task task) {
        checkTask(task);
        this.task = task;
    }

    private void checkTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task mag niet 'null' zijn.");
        }
        if (branchOffice == null) {
            throw new IllegalArgumentException("BranchOffice moet voor Task gezet worden.");
        }
        if (!branchOffice.getUnplannedTasks().contains(task)) {
            throw new IllegalArgumentException("Task zit niet in de lijst van ongeplande taken van BranchOffice.");
        }
    }

    private void setStartTime(LocalDateTime startTime) {
        checkStartTime(startTime);
        this.startTime = startTime;
    }

    private void checkStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Starttijd mag niet 'null' zijn.");
        }
    }
}
