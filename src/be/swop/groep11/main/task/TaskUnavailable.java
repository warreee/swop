package be.swop.groep11.main.task;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.OldPlan;

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
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.UNAVAILABLE;
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
     * @param task de taak waarvan de geschatte duur wordt teruggegeven.
     * @param currentSystemTime
     * @return de geschatte duur van de taak.
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {
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

    /**
     * Plant de taak
     * @param task De te plannen taak
     */
    @Override
    public void plan(Task task, OldPlan plan) {
        task.setPlan(plan);
    }

    @Override
    protected void updateStatus(Task task, LocalDateTime systemTime) {

        if (task.isPlanned()){
            Plan plan = task.getPlan2();
            TimeSpan timespan = plan.getTimeSpan();

            if (!timespan.containsLocalDateTime(systemTime)){
                if (plan.hasEquivalentPlan(systemTime)) {
                    makeAvailable(task);
                }
            }
        }
    }

}