package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een taak status voor. Deze status is Finished.
 */
public class TaskFinished extends TaskStatus {

    /**
     * Maakt een nieuw TaskFinished object aan.
     */
    protected TaskFinished() {
        super(TaskStatusEnum.FINISHED);
    }

    /**
     * Haalt de status op.
     */
    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.FINISHED;
    }

    /**
     * Haalt een nieuwe TaskFinishedStatus op.
     */
    @Override
    public TaskStatus getTaskStatus() {
        return new TaskFinished();
    }

    @Override
    protected boolean isFinished(Task task) {
        return true;
    }

    /**
     * Start met de uitvoer van de gegeven taak op de gegeven moment. Dit mag niet dus gooit een exception.
     * @param task De taak die moet uitgevoerd worden.
     * @param time De starttijd van de uitvoer.
     */




    @Override
    protected void execute(Task task,LocalDateTime time) {
        throw new IllegalStateTransitionException("Een gefinishste taak kan niet opnieuw worden uitgevoerd");
    }

    /**
     * Beeindigd de uitvoer van de gegeven taak op het gegeven moment. Dit mag niet dus gooit een exception.
     * @param task De taak die moet beeindigd worden.
     * @param time De eindtijd van de uitvoer.
     */
    @Override
    protected void finish(Task task,LocalDateTime time) {
        throw new IllegalStateTransitionException("De taak was reeds gefinishd");
    }

    /**
     * Faalt de gegeven taak op het gegeven moment. Dit mag niet dus gooit een exception.
     * @param task De taak die moet falen.
     * @param time De tijd van het falen.
     */
    @Override
    protected void fail(Task task,LocalDateTime time) {
        throw new IllegalStateTransitionException("Een gefinishste taak kan niet gefaild worden!");
    }

    /**
     * Maakt de gegeven taak opnieuw beschikbaar. Dit mag niet dus gooit een exception.
     * @param task De taak die opnieuw beschikbaar moet komen.
     */
    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransitionException("Een gefinishste taak kan niet available worden!");
    }

    /**
     * Maakt de gegeven taak onbeschikbaar. Dit mag niet dus gooit een exception.
     * @param task De taal die onbeschikbaar moet worden.
     */
    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransitionException("Een gefinishste taak kan niet unavailable worden!");
    }

    /**
     * Geeft de duur van een gefinishte taak terug.
     * @param task de taak waarvan de duur wordt opgevraagd.
     * @param currentSystemTime
     * @return de duur van de taak
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return Duration.between(task.getStartTime(), task.getEndTime());
    }

    /**
     * Een gefinishste taak kan geen nieuwe starttijd hebben. Deze is al gezet bij de overgang van available naar executing.
     * @return false
     */
    @Override
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime) {
        return false;
    }

    /**
     * Een gefinishte task kan geen nieuwe EndTime krijgen.
     * @param task
     * @param endTime   De eindtijd om te controleren
     * @return
     */
    @Override
    protected boolean canHaveAsEndTime(Task task, LocalDateTime endTime) {
        return false;
    }

}
