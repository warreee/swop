package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.resource.Plan;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskUnavailable extends TaskStatus {

    protected TaskUnavailable() {
        super(TaskStatusEnum.UNAVAILABLE);
    }

    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransitionException("De taak kan niet naar de status UNAVAILABLE gaan want was dit reeds!");
    }

    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.UNAVAILABLE;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return new TaskUnavailable();
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
    public void plan(Task task, Plan plan) {
        task.setPlan(plan);
    }

    //TODO make available
}