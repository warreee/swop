package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskAvailable extends TaskStatus {

    @Override
    protected void execute(Task task) {
        if (checkPlan()){
            TaskStatus executing = new TaskExecuting();
            task.setStatus(executing);
        }
    }

    @Override
    protected void finish(Task task) {
        throw new IllegalStateTransition("Een taak moet eerst worden uitgevoerd voor hij gefinish wordt");
    }

    @Override
    protected void fail(Task task) {
        throw new IllegalStateTransition("Een taak kan niet van Available naar Fail gaan");
    }

    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransition("De taak was al available");
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
    public Duration getDuration(Task task, LocalDateTime currentSystemTime) {
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





}
