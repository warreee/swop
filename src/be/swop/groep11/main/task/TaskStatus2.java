package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public abstract class TaskStatus2 implements Cloneable {

    protected TaskStatus2() {

    }

    @Override
    protected TaskStatus2 clone() throws CloneNotSupportedException {
        return (TaskStatus2) super.clone();
    }

    protected void execute(Task task) {
        throw new IllegalStateTransition("De taak kan niet naar de status EXECUTING gaan vanuit de huidige status");
    }

    protected void finish(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status FINISHED gaan vanuit de huidige status");
    }

    protected void fail(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status FAIL gaan vanuit de huidige status");
    }

    protected void makeAvailable(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status AVAILABLE gaan vanuit de huidige status");
    }

    protected void makeUnavailable(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status UNAVAILABLE gaan vanuit de huidige status");
    }

    protected boolean checkPlan () {
        return true;
    }

    public abstract Duration getDuration(Task task, LocalDateTime currentSystemTime);
}
