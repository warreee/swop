package be.swop.groep11.main.planning;

import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.resource.ResourceRequirement;
import be.swop.groep11.main.resource.ResourceReservation;
import be.swop.groep11.main.task.Task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Arne De Brabandere_3 on 15/05/2015.
 */
public class Plan {

    protected Plan(Task task, ResourcePlanner resourcePlanner) {
        // TODO
    }

    public LocalDateTime getStartTime() {
        // TODO
        return null;
    }

    public LocalDateTime getEndTime() {
        // TODO
        return null;
    }

    public void clear() {
        // TODO
    }

    public void clearFutureReservations() {
        // TODO
    }

    public List<ResourceReservation> getReservations() {
        // TODO
        return null;
    }

    public boolean hasReservationFor(ResourceRequirement resourceRequirement) {
        // TODO
        return false;
    }

}
