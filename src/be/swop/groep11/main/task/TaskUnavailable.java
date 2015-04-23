package be.swop.groep11.main.task;

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
}