package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskExecuting extends TaskStatus2 {

    @Override
    protected void execute(Task task) {
        throw new IllegalStateTransition("De taak was reeds aan het uitvoeren!");
    }

    @Override
    protected void finish(Task task) {
        if (super.checkPlan()){
            TaskStatus2 finished = new TaskFinished();
            task.setStatus(finished);
        }

    }

}
