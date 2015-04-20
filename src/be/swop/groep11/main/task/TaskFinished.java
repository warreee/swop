package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskFinished extends TaskStatus {


    @Override
    public TaskStatusString getStatusString() {
        return TaskStatusString.FINISHED;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return new TaskFinished();
    }

    @Override
    protected void execute(Task task,LocalDateTime time) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet opnieuw worden uitgevoerd");
    }

    @Override
    protected void finish(Task task,LocalDateTime time) {
        throw new IllegalStateTransition("De taak was reeds gefinishd");
    }

    @Override
    protected void fail(Task task,LocalDateTime time) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet gefaild worden!");
    }

    @Override
    protected void makeAvailable(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet available worden!");
    }

    @Override
    protected void makeUnavailable(Task task) {
        throw new IllegalStateTransition("Een gefinishste taak kan niet unavailable worden!");
    }

    /**
     * Geeft de duur van een gefinishte taak terug.
     * @param task de taak waarvan de duur wordt opgevraagd.
     * @param currentSystemTime
     * @return de duur van de taak
     */
    @Override
    protected Duration getDuration(Task task, LocalDateTime currentSystemTime) {
        return task.getDuration();
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

    /**
     * Aan een taak die al gefinisht is kan niet ineens dependency worden toegevoegd.
     * @param task
     * @param dependingOn
     * @return
     */
    @Override
    protected boolean isValidDependingOn(Task task, Task dependingOn) {
        return false;
    }
}
