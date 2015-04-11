package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

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


    /** TODO: vragen aan michiel
     * Controleer of de gegeven start tijd geldig is voor deze taak.
     *
     * @param startTime De starttijd om te controleren
     * @return          Waar indien de status van deze taak AVAILABLE is, geen huidige endTime en de gegeven startTime niet null is.
     *                  Waar indien de status van deze taak AVAILABLE is, een huidige endTime heeft en de gegeven startTime voor de endTime valt.
     *
     */
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime){
        if(startTime == null) { // TODO: wanneer zou dit gebeuren?
            return false;
        }
        if(task.hasEndTime() && startTime.isAfter(task.getEndTime())){
            return false;
        }
        Set<Task> tasks = task.getDependingOnTasks();
        for(Task t: tasks){
            if(t.getEndTime() == null){
                continue;
            }
            if(startTime.isBefore(t.getEndTime())){
                return false; // De gegeven starttijd ligt voor een eindtijd van een afhankelijke taak
            }
        }
        return true;
    }
}
