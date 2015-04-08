package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskAvailable extends TaskStatus2 {

    @Override
    protected void execute(Task task) {
        if (checkPlan()){
            TaskStatus2 executing = new TaskExecuting();
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
        TaskStatus2 unavailable = new TaskUnavailable();
        task.setStatus(unavailable);
    }


}
