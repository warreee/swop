package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

import java.time.Duration;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskFailed extends TaskStatus2 {

    /**
     * Geeft de tijd terug dat de gefaalde taak heeft geduurd.
     * @param task de taak die gefaald is.
     * @return
     */
    @Override
    public Duration getDuration(Task task) {
        return task.getDuration();
    }
}
