package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.UnavailableReservationException;
import be.swop.groep11.main.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by Ronald on 12/05/2015.
 */
public class ResourceSchedule {

    private ResourceInstance resource;
    private TreeMap<TimeSpan, Task> timeSpanToTask = new TreeMap<>((o1, o2) -> o1.isBefore(o2) ? -1 : o1.equals(o2) ? 0 : +1);
    private HashMap<Task, ResourceReservation> taskToReservation = new HashMap<>();

    /**
     * Constructor voor een ResourceSchedule, Uniek voor de gegeven Resource
     * @param resource  De Resource Waarvoor een schedule op te bouwen
     */
    public ResourceSchedule(ResourceInstance resource) {
        if (!canHaveAsResourceInstance(resource)) {
            throw new IllegalArgumentException("Invallid resource, needs to be initialized");
        }
        this.resource = resource;
    }

    /**
     * Controleer of de gegeven ResourceInstance een geldige is voor deze ResourceSchedule.
     * @param resource  De te controleren ResourceInstance
     * @return          Waar indien resource niet null is en boven resource reeds geen ResourceSchedule heeft.
     */
    private boolean canHaveAsResourceInstance(ResourceInstance resource) {
        return resource != null && !resource.hasResourceSchedule();

    }

    public ResourceInstance getResource() {
        return resource;
    }

    public void addReservation(ResourceReservation reservation) {
        if (!canHaveAsReservation(reservation)) {
            throw new UnavailableReservationException(getOverlappingReservations(reservation));
        }
        timeSpanToTask.put(reservation.getTimeSpan(), reservation.getTask());
        taskToReservation.put(reservation.getTask(), reservation);
    }

    public boolean canHaveAsReservation(ResourceReservation reservation) {
        //er mag maar ??n reservatie per task zijn
        if (reservation == null) {
            return false;
        }else if (reservation.getResourceInstance().equals(getResource()) && !taskToReservation.containsKey(reservation.getTask())) {
            return this.isFree(reservation.getTimeSpan());
        }else{
            return false;
        }
    }

    private boolean isFree(TimeSpan reservation) {
        TimeSpan next = timeSpanToTask.ceilingKey(reservation); // following first ResourceReservation
        TimeSpan prev = timeSpanToTask.floorKey(reservation); // previous ResourceReservation

        if (next == null && prev == null) {
            return true;
        } else if (next == null) { //prev != null
            return !prev.overlapsWith(reservation);
        } else if (prev == null) { //next != null
            return !next.overlapsWith(reservation);
        } else {
            //next != null && prev != null
            return !prev.overlapsWith(reservation) && !next.overlapsWith(reservation);
        }
    }

    public List<ResourceReservation> getOverlappingReservations(ResourceReservation reservation) {
        if (reservation == null) {
            return new ArrayList<>();
        }
        List<TimeSpan> timeSpans = timeSpanToTask.keySet().stream().filter(ts -> ts.overlapsWith(reservation.getTimeSpan())).collect(Collectors.toList());
        List<ResourceReservation> resourceReservations = new ArrayList<>();
        timeSpans.forEach(tsa -> resourceReservations.add(taskToReservation.get(timeSpanToTask.get(tsa))));
        return resourceReservations;
    }

    public ResourceReservation getReservation(Task task) {
        //check if task is null?
        return taskToReservation.get(task);
    }

    public List<TimeSpan> getAvailableTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("ongeldige dateTime");
        }
        ArrayList<TimeSpan> freeTime = new ArrayList<>();
        if (timeSpanToTask.isEmpty()) {
            freeTime.add(new TimeSpan(dateTime, LocalDateTime.MAX));
            return freeTime;
        }

        //Zoek voor de TimeSpan die de gegeven dateTime omvat; indien er geen is, fake eentje van 1 seconde.
        TimeSpan temp =  searchForTimeSpanContaining(dateTime);

        do {
            TimeSpan resHigher = timeSpanToTask.higherKey(temp);
            TimeSpan free = temp.timeBetween(resHigher);
            if (free != null) {
                freeTime.add(free);
            } else {
                freeTime.add(new TimeSpan(temp.getEndTime(), LocalDateTime.MAX));
            }
            temp = resHigher;
        } while (temp != null);

        return freeTime;
    }

    private TimeSpan searchForTimeSpanContaining(LocalDateTime dateTime) {
        List<TimeSpan> list = timeSpanToTask.keySet().stream().filter(reservation -> reservation.containsLocalDateTime(dateTime)).collect(Collectors.toList());
        return list.isEmpty() ? new TimeSpan(dateTime.minusNanos(1), dateTime) : list.get(0);
    }

}
