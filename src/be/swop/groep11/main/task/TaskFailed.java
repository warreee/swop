package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskFailed extends TaskStatus {
    protected TaskFailed() {
        super(TaskStatusEnum.FAILED);
    }

    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.FAILED;
    }

    @Override
    public TaskStatus getTaskStatus(LocalDateTime systemTime) {
        return new TaskFailed();
    }

    /**
     * Geeft de tijd terug dat de gefaalde taak heeft geduurd.
     * @param task de taak die gefaald is.
     * @param currentSystemTime
     * @return
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return Duration.between(task.getStartTime(), task.getEndTime());
    }

    /**
     * Een gefaalde taak kan geen nieuwe starttijd hebben. Deze is al gezet bij de overgang van available naar executing.
     * @return false
     */
    @Override
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime) {
        return false;
    }

    /**
     * Een gefaalde task kan geen nieuwe EndTime krijgen.
     * @param task
     * @param endTime   De eindtijd om te controleren
     * @return
     */
    @Override
    protected boolean canHaveAsEndTime(Task task, LocalDateTime endTime) {
        return false;
    }


}
