package be.swop.groep11.main.planning;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.resource.ResourceReservation;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stelt een plan voor een taak in een resource planner voor.
 * Een plan heeft een tijdspanne en resource reservaties.
 * Alle reservaties hebben dezelfde tijdspanne als die van het plan.
 */
public class Plan {

    private Task task;
    private ResourcePlanner resourcePlanner;
    private TimeSpan timeSpan;
    private List<ResourceReservation> reservations;

    /**
     * Constructor om een niew plan aan te maken.
     * @param task            De taak waarvoor het plan gemaakt is
     * @param resourcePlanner Resource planner waarin het plan zit
     * @param timeSpan        De tijdspanne van het plan
     * @param reservations    De lijst van reservaties van het plan
     */
    protected Plan(Task task, ResourcePlanner resourcePlanner, TimeSpan timeSpan, List<ResourceReservation> reservations) {
        this.task = task;
        this.timeSpan = timeSpan;
        this.resourcePlanner = resourcePlanner;
        this.reservations = reservations;
    }

    /**
     * Geeft de tijdspanne van dit plan.
     */
    public TimeSpan getTimeSpan() {
        return this.timeSpan;
    }

    /**
     * Verwijdert dit plan door alle reservaties ervan te verwijderen,
     * dit plan uit de resource planner te halen en dit plan uit de bijhorende taak te halen.
     */
    public void clear() {
        this.reservations = new ArrayList<>(); // niet echt nodig
        this.resourcePlanner.removePlan(this);
        this.task.setPlan(null);
    }

    /**
     * Laat de reservaties van dit plan vroeger eindigen dan gepland.
     * Dit wordt gedaan door voor alle reservaties de eindtijd op de gegeven eindtijd te zetten.
     * @param endTime De gegeven eindtijd
     * @throws IllegalArgumentException De gegeven eindtijd ligt buiten de  tijdspanne van dit plan.
     */
    public void clearFutureReservations(LocalDateTime endTime) {
        if (! this.timeSpan.containsLocalDateTime(endTime)) {
            throw new IllegalArgumentException("De gegeven eindtijd ligt buiten de  tijdspanne van dit plan");
        }

        this.timeSpan = new TimeSpan(this.timeSpan.getStartTime(), endTime);

        List<ResourceReservation> newReservations = new ArrayList<>();
        for (ResourceReservation reservation : this.reservations) {
            newReservations.add(new ResourceReservation(
                    reservation.getTask(),
                    reservation.getResourceInstance(),
                    this.timeSpan,
                    reservation.isSpecific()));
        }
        this.reservations = newReservations;
    }

    /**
     * Geeft de immutable lijst van de reservaties van dit plan.
     */
    public ImmutableList<ResourceReservation> getReservations() {
        return ImmutableList.copyOf(this.reservations);
    }

    public ImmutableList<ResourceReservation> getSpecificReservations() {
        return ImmutableList.copyOf(
                reservations.stream().filter(reservation -> reservation.isSpecific()).collect(Collectors.toList())
        );
    }

    /**
     * Geeft een immutable lijst van de reservaties van dit plan voor een bepaald resource type.
     * @param type Het resource type
     */
    public ImmutableList<ResourceReservation> getReservations(AResourceType type){
        return ImmutableList.copyOf(reservations.stream()
                .filter(x -> x.getResourceInstance().getResourceType() == type)
                .collect(Collectors.toList()));
    }

    /**
     * Geeft de taak die bij dit plan hoort.
     */
    public Task getTask(){
        return this.task;
    }


    /**
     * Geeft aan of de gegeven LocalDateTime in de periode valt voor dit plan.
     * @param localDateTime     De gegeven LocalDateTime
     * @return                  Waar indien de localDateTime in de timespan van dit plan valt, anders niet waar.
     */
    public boolean isWithinPlanTimeSpan(LocalDateTime localDateTime) {
        return getTimeSpan().containsLocalDateTime(localDateTime);
    }

    /**
     * Deze methode gaat na of er een equivalent plan bestaat op de huidige systeemTijd.
     * Waarbij rekening gehouden wordt met reeds specifieke reservaties.
     *
     * @return Waar indien er een ander plan gemaakt kan worden met de specifieke resources op de huidige systeemTijd
     */
    public boolean hasEquivalentPlan() {
        return getResourcePlanner().hasEquivalentPlan(this);
    }

    private ResourcePlanner getResourcePlanner() {
        return resourcePlanner;
    }
}
