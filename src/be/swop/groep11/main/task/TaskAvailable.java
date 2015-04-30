package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.resource.IPlan;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskAvailable extends TaskStatus {

    protected TaskAvailable() {
        super(TaskStatusEnum.AVAILABLE);
    }

    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.AVAILABLE;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return new TaskAvailable();
    }

    @Override
    protected void execute(Task task, LocalDateTime startTime) {
        task.setStartTime(startTime);
        TaskStatus executing = new TaskExecuting();
        task.setStatus(executing);
    }

    @Override
    protected void finish(Task task, LocalDateTime endTime) {
        throw new IllegalStateTransitionException("Een taak moet eerst worden uitgevoerd voor hij gefinish wordt");
    }

    @Override
    protected void fail(Task task, LocalDateTime endTime) {
        throw new IllegalStateTransitionException("Een taak kan niet van Available naar Fail gaan");
    }

    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransitionException("De taak was al available");
    }

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
    public void plan(Task task, IPlan plan) {
        task.setPlan(plan);
    }

}
