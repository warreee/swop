package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskFinished extends TaskStatus2 {


    @Override
    public void execute(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet opnieuw worden uitgevoerd");
    }

    @Override
    public void finish(Task task) {
        throw new IllegalStateTransition("De taak was reeds gefinishd");
    }

    @Override
    public void fail(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet gefaild worden!");
    }

    @Override
    public void makeAvailable(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet available worden!");
    }

    @Override
    public void makeUnavailable(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet unavailable worden!");
    }
}
