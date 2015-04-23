package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Created by warreee on 4/7/15.
 */
public class TaskExecuting extends TaskStatus {
    protected TaskExecuting() {
        super(TaskStatusEnum.EXECUTING);
    }

    @Override
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.EXECUTING;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return new TaskExecuting();
    }

    @Override
    protected void execute(Task task, LocalDateTime startTime) {
        throw new IllegalStateTransition("De taak was reeds aan het uitvoeren!");
    }

    @Override
    protected void finish(Task task, LocalDateTime endTime) {
        task.setEndTime(endTime);
        TaskStatus finished = new TaskFinished();
        task.setStatus(finished);
        task.releaseResources(); // TODO: implementatie in task doen!
    }

    @Override
    protected void fail(Task task, LocalDateTime endTime) {
        task.setEndTime(endTime);
        TaskStatus failed = new TaskFailed();
        task.setStatus(failed);
        task.releaseResources(); // TODO: implementatie in task doen!
    }

    /**
     * Geeft de geschatte duur van de taak die aan het uitvoeren is, indien de taak over tijd is
     * dan wordt de duur berekend als het vereschil tussen de starttijd en de huidige systeemtijd.
     * @param task
     * @param currentSystemTime
     * @return de duur van de taak.
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {

        return task.isOverTime() ? Duration.between(task.getStartTime(),currentSystemTime) : task.getEstimatedDuration();
    }

    /**
     * Een uitvoerende taak kan geen nieuwe starttijd hebben. Deze is al gezet bij de overgang van available naar executing.
     * @return false
     */
    @Override
    protected boolean canHaveAsStartTime(Task task, LocalDateTime startTime) {
        return false;
    }

    /**
     * Aan een taak die al aan het uitvoeren is kan niet ineens dependency worden toegevoegd.
     * @param task
     * @param dependingOn
     * @return
     */
    @Override
    protected boolean isValidDependingOn(Task task, Task dependingOn) {
        return false;
    }
}
