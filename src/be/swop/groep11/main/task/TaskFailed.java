package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskFailed extends TaskStatus2 {

    /**
     * Geeft de tijd terug dat de gefaalde taak heeft geduurd.
     * @param task de taak die gefaald is.
     * @param currentSystemTime
     * @return
     */
    @Override
    public Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return task.getDuration();
    }
}
