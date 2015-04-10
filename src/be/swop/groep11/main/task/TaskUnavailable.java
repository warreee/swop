package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

import java.time.Duration;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskUnavailable extends TaskStatus2 {

    /**
     * Geeft als duur, de geschatte duur van de taak terug.
     * @param task de taak waarvan de geschatte duur wordt teruggegeven.
     * @return de geschatte duur van de taak.
     */
    @Override
    protected Duration getDuration(Task task) {
        return task.getEstimatedDuration();
    }
}
