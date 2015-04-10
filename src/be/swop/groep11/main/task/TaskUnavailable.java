package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskUnavailable extends TaskStatus2 {

    /**
     * Geeft als duur, de geschatte duur van de taak terug.
     * @param task de taak waarvan de geschatte duur wordt teruggegeven.
     * @param currentSystemTime
     * @return de geschatte duur van de taak.
     */
    @Override
    public Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return task.getEstimatedDuration();
    }
}
