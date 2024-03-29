package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.planning.Plan;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een taak status voor. Deze status is Unvailable
 */
public class TaskUnavailable extends TaskStatus {

    /**
     * Maakt een nieuw TaskUnavailable object aan.
     */
    protected TaskUnavailable() {
        super(TaskStatusEnum.UNAVAILABLE);
    }

    /**
     * Maakt de gegeven taak unavailable. Dit mag niet dus gooit een exception.
     * @param task De taak die unavailable moet gemaakt wotden.
     */
    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransitionException("De taak kan niet naar de status UNAVAILABLE gaan want was dit reeds!");
    }

    /**
     * Maakt de gegeven taak available.
     * @param task De taak die available moet gemaakt worden.
     */
    @Override
    protected void makeAvailable(Task task) {
        // Indien de dependingOn taken van deze taak niet finished zijn, zijn ze nog niet gereed en mag deze taak
        // niet worden op available worden gezet.
        // We moeten hier niet checken op failed aangezien depencygraph garandeert dat er indien er een gefailde taak is
        // er direct een alternatieve taak wordt gezet in dependingOnTasks
        if (task.getDependingOnTasks().stream().filter((entry) -> (!entry.isFinished())).count() == 0) {
            TaskStatus newStatus = new TaskAvailable();
            task.setStatus(newStatus);
        }
    }

    /**
     */
    @Override
    public String getStatusString() {
        return TaskStatusEnum.UNAVAILABLE.toString();
    }

    /**
     * Haalt het Statusobject op van deze status.
     */
    @Override
    public TaskStatus getTaskStatus() {
        return new TaskUnavailable();
    }

    @Override
    protected boolean isUnavailable(Task task) {
        return true;
    }

    /**
     * Geeft als duur, de geschatte duur van de taak terug.
     * @param localDateTime
     * @param task de taak waarvan de geschatte duur wordt teruggegeven.
     * @param localDateTime
     * @return de geschatte duur van de taak.
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime localDateTime) {
        return task.getEstimatedDuration();
    }

    /**
     * Een taak die nog Unavailable is, kan nog geen starttijd krijgen!
     * @param task
     * @param startTime De starttijd om te controleren
     * @return false
     */
    @Override
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime) {
        return false;
    }

    /**
     * Een Unavailable task kan nog geen EndTime krijgen.
     * @param task
     * @param endTime   De eindtijd om te controleren
     * @return
     */
    @Override
    protected boolean canHaveAsEndTime(Task task, LocalDateTime endTime) {
        return false;
    }

    @Override
    protected boolean canHaveAsPlan(Task task, Plan plan) {
        return plan != null && task.getPlan() == null;
    }

    @Override
    protected void updateStatus(Task task) {
        if (task.isPlanned()){
            Plan plan = task.getPlan();

            //Check if unplanned execution is possible
            if (plan.hasEquivalentPlan()) {
                makeAvailable(task);
            }
        }
    }

}