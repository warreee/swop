package be.swop.groep11.main.task;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Created by warreee on 4/7/15.
 */
public class TaskExecuting extends TaskStatus2 {

    @Override
    protected void execute(Task task) {
        throw new IllegalStateTransition("De taak was reeds aan het uitvoeren!");
    }

    @Override
    protected void finish(Task task) {
        if (super.checkPlan()){
            TaskStatus2 finished = new TaskFinished();
            task.setStatus(finished);
        }

    }

    /**
     * Geeft de geschatte duur van de taak die aan het uitvoeren is, indien de taak over tijd is
     * dan wordt de duur berekend als het vereschil tussen de starttijd en de huidige systeemtijd.
     * @param task
     * @param currentSystemTime
     * @return de duur van de taak.
     */
    @Override
    public Duration getDuration(Task task, LocalDateTime currentSystemTime) {

        return task.isOverTime() ? Duration.between(task.getStartTime(),currentSystemTime) : task.getEstimatedDuration();
    }

}
