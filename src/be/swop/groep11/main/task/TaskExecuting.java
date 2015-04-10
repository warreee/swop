package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;


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
    // TODO: hoe berekenen als het nog bezig is?
    @Override
    public Duration getDuration(Task task, LocalDateTime currentSystemTime) {

        return null;
    }

}
