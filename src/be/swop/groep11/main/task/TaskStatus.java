package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by warreee on 4/7/15.
 */
public abstract class TaskStatus implements Cloneable {



    protected TaskStatus(TaskStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }
    private final TaskStatusEnum statusEnum;


    protected abstract TaskStatusEnum getStatus();
    protected abstract TaskStatus getTaskStatus();

    @Override
    protected TaskStatus clone() throws CloneNotSupportedException {
        return (TaskStatus) super.clone();
    }

    protected void execute(Task task, LocalDateTime startTime) {
        throw new IllegalStateTransition("De taak kan niet naar de status EXECUTING gaan vanuit de huidige status");
    }

    protected void finish(Task task, LocalDateTime endTime){
        throw new IllegalStateTransition("De taak kan niet naar de status FINISHED gaan vanuit de huidige status");
    }

    protected void fail(Task task, LocalDateTime endTime){
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

    protected abstract Duration getDuration(Task task, LocalDateTime currentSystemTime);


    /**
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

    /**
     * Controleer of de gegeven eindtijd een geldig tijdstip is voor deze taak..
     *
     * @param endTime   De eindtijd om te controleren
     * @return          Waar indien de status van deze taak AVAILABLE is, een huidige starttijd heeft,
     *                  en de gegeven endTime na de start tijd van deze taak valt.
     *                  Waar indien de status van deze taak AVAILABLE is en een huidige starttijd heeft,
     *                  en de gegeven endTime niet null is en de huidige endTime
     */
    protected boolean canHaveAsEndTime(Task task, LocalDateTime endTime) {

        if (!task.hasStartTime()) {
            return false;
        }
        if(endTime == null){
            return false;
        }

        if (task.getStartTime().isBefore(endTime)){
            return true;
        }

        return true;
    }

    protected boolean isValidDependingOn(Task task, Task dependingOn){
        if (task == dependingOn) // TODO: werkt dit wel?
            return false;

        Set<Task> dependingOnTasks = dependingOn.getDependingOnTasks();
        if (dependingOnTasks.contains(task))
            // dan hangt dependingOn af van task,
            // dus nu zeggen dat task afhankelijk is van dependingOn zou een lus veroorzaken
            return false;
        /*if(! task.getProject().equals(dependingOn.getProject())){
            // De 2 taken moeten aan hetzelfde project toebehoren.
            return false;
        }*/
        return true;
    }
}
