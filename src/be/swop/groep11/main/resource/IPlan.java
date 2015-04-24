package be.swop.groep11.main.resource;

import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Arne De Brabandere_3 on 23/04/2015.
 */
public interface IPlan {
        boolean isValidPlan();

        Task getTask();

        LocalDateTime getStartTime();

        LocalDateTime getEndTime();

        ImmutableList<ResourceReservation> getReservations();

        ImmutableList<ResourceReservation> getReservations(IResourceType resourceType);

        void changeReservations(List<ResourceInstance> resourceInstances);

        void addReservations(List<ResourceInstance> resourceInstances);

        boolean hasReservationFor(ResourceInstance resourceInstance);

        List<Task> getConflictingTasks();
}
