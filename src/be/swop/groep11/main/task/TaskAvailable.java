package be.swop.groep11.main.task;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.OldPlan;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een taak status voor. Deze status is Available.
 */
public class TaskAvailable extends TaskStatus {

    /**
     * Maakt een nieuw TaskAvailable object aan.
     */
    protected TaskAvailable() {
        super(TaskStatusEnum.AVAILABLE);
    }

    /**
     * Haalt de status op.
     */
    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.AVAILABLE;
    }

    /**
     * Haalt een nieuwe TaskAvailableStatus op.
     */
    @Override
    public TaskStatus getTaskStatus() {
        return new TaskAvailable();
    }

    @Override
    protected boolean isAvailable(Task task) {
        return true;
    }

    /**
     * Start met de uitvoer van de gegeven taak op de gegeven moment.
     * @param task De taak die moet uitgevoerd worden.
     * @param startTime De starttijd van de uitvoer.
     */
    @Override
    protected void execute(Task task, LocalDateTime startTime) {
        task.setStartTime(startTime);
        TaskStatus executing = new TaskExecuting();
        task.setStatus(executing);
    }

    /**
     * Probeert de gegeven taak te beeindigen. Dit mag niet een gooit een IllegalStateTransitionException
     * @param task De taak die moet uitgevoerd worden.
     * @param endTime De eidtijd van de uitvoer.
     */
    @Override
    protected void finish(Task task, LocalDateTime endTime) {
        throw new IllegalStateTransitionException("Een taak moet eerst worden uitgevoerd voor hij gefinish wordt");
    }

    /**
     * Probeert de gegeven taak te failen. Dit mag niet een gooit een IllegalStateTransitionException
     * @param task De taak die moet falen worden.
     * @param endTime De eidtijd van het falen.
     */
    @Override
    protected void fail(Task task, LocalDateTime endTime) {
        throw new IllegalStateTransitionException("Een taak kan niet van Available naar Fail gaan");
    }

    /**
     * Probeert de gegeven taak available te maken. Dit mag niet een gooit een IllegalStateTransitionException
     * @param task De taak die available moet worden.
     */
    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransitionException("De taak was al available");
    }

    /**
     * Maakt de gegeven taak terug unavailable.
     * @param task De taak die terug unavailable moet worden.
     */
    @Override
    protected void makeUnavailable(Task task) {
        TaskStatus unavailable = new TaskUnavailable();
        task.setStatus(unavailable);
    }

    /**
     * Geeft de geschatte duur als duur van de taak terug.
     * @param task de taak waarvan de geschatte duur wordt opgevraagd.
     * @param currentSystemTime
     * @return de geschatte duur van de taak.
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return task.getEstimatedDuration();
    }


    /**
     * Een available task kan nog geen EndTime krijgen.
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
    public void updateStatus(Task task, LocalDateTime systemTime) {
        Plan plan = task.getPlan2();
        TimeSpan timespan = plan.getTimeSpan();

        if (!timespan.containsLocalDateTime(systemTime)){
            if (!plan.hasEquivalentPlan(systemTime)){
                makeUnavailable(task);
            }
        }
    }
}
