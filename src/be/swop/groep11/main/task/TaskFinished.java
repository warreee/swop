package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

import java.time.Duration;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskFinished extends TaskStatus2 {


    @Override
    protected void execute(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet opnieuw worden uitgevoerd");
    }

    @Override
    protected void finish(Task task) {
        throw new IllegalStateTransition("De taak was reeds gefinishd");
    }

    @Override
    protected void fail(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet gefaild worden!");
    }

    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet available worden!");
    }

    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet unavailable worden!");
    }

    /**
     * Geeft de duur van een gefinishte taak terug.
     * @param task de taak waarvan de duur wordt opgevraagd.
     * @return de duur van de taak
     */
    @Override
    public Duration getDuration(Task task) {
        return task.getDuration();
    }
}
