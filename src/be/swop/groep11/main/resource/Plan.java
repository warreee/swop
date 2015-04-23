package be.swop.groep11.main.resource;

import be.swop.groep11.main.task.Task;
import org.mockito.cglib.core.Local;

import java.time.LocalDateTime;

/**
 * Created by Arne De Brabandere_3 on 23/04/2015.
 */
public class Plan {

    public Plan(Task task, LocalDateTime startTime) {
        if (task == null)
            throw new IllegalArgumentException("Taak mag niet null zijn");
        if (startTime == null)
            throw new IllegalArgumentException("Starttijd mag niet null zijn");
        this.task = task;
        this.startTime = startTime;
    }

    /**
     * Controleert of een plan voor een taak op een gegeven starttijd geldig is:
     * dit betekent dat op die starttijd voor alle resource requirements
     * @param task
     * @param startTime
     * @return
     */
    public static boolean isValidPlan(Task task, LocalDateTime startTime) {
        // TODO
        return false;
    }

    /**
     * Maakt voor elk resource type in de requirement list van de taak de nodige reservaties.
     * @param resourceManager
     */
    public void makeDefaultReservations(ResourceManager resourceManager) {

    }

    public Task getTask() {
        return task;
    }

    private final Task task;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    private final LocalDateTime startTime;

    public LocalDateTime getEndTime() {
        return null; // TODO
    }

}
