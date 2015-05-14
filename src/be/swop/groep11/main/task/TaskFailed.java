package be.swop.groep11.main.task;

import be.swop.groep11.main.exception.IllegalStateTransitionException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een taak status voor. Deze status is Failed.
 */
public class TaskFailed extends TaskStatus {

    /**
     * Maakt een nieuw TaskFailed object aan.
     */
    protected TaskFailed() {
        super(TaskStatusEnum.FAILED);
    }

    /**
     * Haalt de status op.
     */
    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.FAILED;
    }

    /**
     * Haalt een nieuwe TaskFailedStatus op.
     */
    @Override
    public TaskStatus getTaskStatus() {
        return new TaskFailed();
    }

    @Override
    protected boolean isFailed(Task task) {
        return true;
    }

    /**
     * Geeft een aparte exceptie indien er van FAILED naar AVAILABLE probeert gegaan te worden
     * @param task de taak die aan het failed is
     */




    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransitionException("De taak is FAILED, en kan dus niet naar AVAILABLE gaan!");
    }
    /**
     * Geeft een aparte exceptie indien er van FAILED naar UNVAILABLE probeert gegaan te worden
     * @param task de taak die aan het failed is
     */
    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransitionException("De taak is FAILED, en kan dus niet naar UNAVAILABLE gaan!");
    }

    /**
     * Geeft de tijd terug dat de gefaalde taak heeft geduurd.
     * @param task de taak die gefaald is.
     * @param currentSystemTime
     * @return
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return Duration.between(task.getStartTime(), task.getEndTime());
    }

    /**
     * Een gefaalde taak kan geen nieuwe starttijd hebben. Deze is al gezet bij de overgang van available naar executing.
     * @return false
     */
    @Override
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime) {
        return false;
    }

    /**
     * Een gefaalde task kan geen nieuwe EndTime krijgen.
     * @param task
     * @param endTime   De eindtijd om te controleren
     * @return
     */
    @Override
    protected boolean canHaveAsEndTime(Task task, LocalDateTime endTime) {
        return false;
    }

    /**
     * Controleer of voor de gegeven taak een alternatieve taak gezet kan worden.
     * @param task De taak die gefaald is.
     * @param alternative De alternatieve taak.
     * @return True als dit kan anders False.
     */
    @Override
    protected boolean canSetAlternativeTask(Task task,Task alternative) {
        return  task != null &&( alternative == null || (alternative != null && task != alternative && (! alternative.dependsOn(task))));
    }
}
