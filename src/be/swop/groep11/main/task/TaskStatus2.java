package be.swop.groep11.main.task;

/**
 * Created by warreee on 4/7/15.
 */
public abstract class TaskStatus2 {

    protected TaskStatus2() {

    }

    public void execute(Task task) {
        throw new IllegalStateTransition("De taak kan niet naar de status EXECUTING gaan vanuit de huidige status");
    }

    public void finish(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status FINISHED gaan vanuit de huidige status");
    }

    public void fail(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status FAIL gaan vanuit de huidige status");
    }

    public void makeAvailable(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status AVAILABLE gaan vanuit de huidige status");
    }

    public void makeUnavailable(Task task){
        throw new IllegalStateTransition("De taak kan niet naar de status UNAVAILABLE gaan vanuit de huidige status");
    }

    protected boolean checkPlan () {
        return true;
    }
}
