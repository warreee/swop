package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.resource.Plan;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Abstracte basisklasse voor alle TaskStatussen.
 */
public abstract class TaskStatus implements Cloneable {


    /**
     * Maakt een nieuw TaskStatus object aan.
     * @param statusEnum De statusEnum van deze status.
     */
    protected TaskStatus(TaskStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }
    private final TaskStatusEnum statusEnum;


    /**
     * Haalt de status op van deze TaskStatus.
     * @return
     */
    protected abstract TaskStatusEnum getStatus();

    /**
     * Haalt het status object op vand eze TaskStatus.
     * @return
     */
    protected abstract TaskStatus getTaskStatus();

    /**
     * Gaat na of de taak Available is
     * @param task
     * @return true als de taak available is
     */
    protected boolean isAvailable(Task task){
        return false;
    }
    /**
     * Gaat na of de taak unAvailable is
     * @param task
     * @return true als de taak unavailable is
     */
    protected boolean isUnavailable(Task task){
        return false;
    }
    /**
     * Gaat na of de taak executing is
     * @param task
     * @return true als de taak executing is
     */
    protected boolean isExecuting(Task task){
        return false;
    }
    /**
     * Gaat na of de taak failed is
     * @param task
     * @return true als de taak failed is
     */
    protected boolean isFailed(Task task){
        return false;
    }
    /**
     * Gaat na of de taak finished is
     * @param task
     * @return true als de taak finished is
     */
    protected boolean isFinished(Task task){
        return false;
    }

    /**
     * Cloned deze task Status.
     * @return De Clone.
     * @throws CloneNotSupportedException
     */
    @Override
    protected TaskStatus clone() throws CloneNotSupportedException {
        return (TaskStatus) super.clone();
    }

    /**
     * Voert een gegeven taak uit op de gegeven tijd.
     */
    protected void execute(Task task, LocalDateTime startTime) {
        throw new IllegalStateTransitionException("De taak kan niet naar de status EXECUTING gaan vanuit de huidige status");
    }

    /**
     * finished een taak op de gegeven tijd.
     */
    protected void finish(Task task, LocalDateTime endTime){
        throw new IllegalStateTransitionException("De taak kan niet naar de status FINISHED gaan vanuit de huidige status");
    }

    /**
     * Faalt een taak op de gegeven tijd.
     */
    protected void fail(Task task, LocalDateTime endTime){
        throw new IllegalStateTransitionException("De taak kan niet naar de status FAIL gaan vanuit de huidige status");
    }

    /**
     * Maakt een gegeven taak available.
     */
    protected void makeAvailable(Task task){
        throw new IllegalStateTransitionException("De taak kan niet naar de status AVAILABLE gaan vanuit de huidige status");
    }

    /**
     * Maakt een gegeven taak Unavailable.
     */
    protected void makeUnavailable(Task task){
        throw new IllegalStateTransitionException("De taak kan niet naar de status UNAVAILABLE gaan vanuit de huidige status");
    }

    /**
     * Geeft de duratie van de gegeven taak terug adhv de systeemtijd.
     */
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

        if (task.getStartTime().isAfter(endTime)){
            return false;
        }

        return true;
    }

    /**
     * Controleer of voor een gegeven taak een gegeven alternatieve taak gezet kan worden.
     * @param task
     * @param alternative
     * @return
     */
    protected boolean canSetAlternativeTask(Task task,Task alternative){
        return false;
    }

    /**
     * Plant de taak
     * @param task De te plannen taak
     * @throws IllegalStateException De taak kan niet gepland worden.
     */
    protected void plan(Task task, Plan plan) throws IllegalArgumentException {
        throw new IllegalStateException("De taak kan niet gepland worden.");
    }
}
