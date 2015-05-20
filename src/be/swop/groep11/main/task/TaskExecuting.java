package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Stelt een taak status voor. Deze status is Executing.
 */
public class TaskExecuting extends TaskStatus {
    /**
     * Maakt een nieuw TaskExecuting object aan.
     */
    protected TaskExecuting() {
        super(TaskStatusEnum.EXECUTING);
    }

    /**
     * Haalt de status op.
     */
    @Override
    public String getStatusString() {
        return TaskStatusEnum.EXECUTING.toString();
    }

    /**
     * Haalt een nieuwe TaskExecutingStatus op.
     */
    @Override
    public TaskStatus getTaskStatus() {
        return new TaskExecuting();
    }


    @Override
    protected boolean isExecuting(Task task) {
        return true;
    }

    /**
     * Start met de uitvoer van de gegeven taak op de gegeven moment. Dit mag niet dus gooit een exception.
     * @param task De taak die moet uitgevoerd worden.
     * @param startTime De starttijd van de uitvoer.
     */
    @Override
    protected void execute(Task task, LocalDateTime startTime) {
        throw new IllegalStateTransitionException("De taak was reeds aan het uitvoeren!");
    }

    /**
     * Beeindigd de uitvoer van de gegeven taak op het gegeven moment.
     * @param task De taak die moet beeindigd worden.
     * @param endTime De eindtijd van de uitvoer.
     */
    @Override
    protected void finish(Task task, LocalDateTime endTime) {
        task.setEndTime(endTime);
        TaskStatus finished = new TaskFinished();
        task.setStatus(finished);
        if(!task.getPlan().getTimeSpan().isBefore(endTime)) {
            task.getPlan().clearFutureReservations(endTime);
        }
        task.makeDependentTasksAvailable();
    }

    /**
     * Geeft een aparte exceptie indien er van EXECUTING naar AVAILABLE probeert gegaan te worden
     * @param task de taak die aan het uitvoeren is
     */
    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransitionException("De taak is aan het uitvoeren, en kan dus niet naar AVAILABLE gaan!");
    }
    /**
     * Geeft een aparte exceptie indien er van EXECUTING naar UNAVAILABLE probeert gegaan te worden
     * @param task de taak die aan het uitvoeren is
     */
    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransitionException("De taak is aan het uitvoeren, en kan dus niet naar UNAVAILABLE gaan!");
    }

    /**
     * Faalt de gegeven taak op het gegeven moment.
     * @param task De taak die moet falen.
     * @param endTime De tijd van het falen.
     */
    @Override
    protected void fail(Task task, LocalDateTime endTime) {
        task.setEndTime(endTime);
        TaskStatus failed = new TaskFailed();
        task.setStatus(failed);
        task.getPlan().clearFutureReservations(endTime);
    }

    /**
     * Geeft de geschatte duur van de taak die aan het uitvoeren is, indien de taak over tijd is
     * dan wordt de duur berekend als het verschil tussen de starttijd en de huidige systeemtijd.
     * @param currentSystemTime
     * @param task
     * @param localDateTime
     * @return de duur van de taak.
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime localDateTime) {
        return task.isOverTime() ? Duration.between(task.getStartTime(),localDateTime) : task.getEstimatedDuration();
    }

    /**
     * Een uitvoerende taak kan geen nieuwe starttijd hebben. Deze is al gezet bij de overgang van available naar executing.
     * @return false
     */
    @Override
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime) {
        return false;
    }


}
