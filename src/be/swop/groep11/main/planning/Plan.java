package be.swop.groep11.main.planning;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.ResourceInstance;
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
        if (!task.canHaveAsPlan(this)) {
            throw new IllegalArgumentException("geen geldig plan voor de taak .....");
        }
        setTask(task);
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
        //TODO fix documentatie

        getResourcePlanner().removePlan(this);
        setTask(null);
        this.reservations = new ArrayList<>(); // niet echt nodig
    }

    /**
     * Setter voor Task parameter
     * @param task  De nieuwe taak
     */
    private void setTask(Task task) {
        if (!canHaveAsTask(task)) {
            throw new IllegalArgumentException("Ongeldige taak");
        }
        this.task = task;
    }

    /**
     * @return  True indien gegeven task niet null && huidige task voor dit plan wel null
     *          True indien gegeven task wel null &&  huidige task niet null
     */
    private boolean canHaveAsTask(Task task) {
        //Task is niet null, en plan heeft geen taak.
        //Of task is null & Plan heeft reeds een taak.
        return task != null && getTask() == null || task == null && getTask() != null;
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
    public Task getTask() {
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

    /**
     * Deze methode gaat na of er een equivalent plan bestaat op het gegeven Tijdstip.
     * Waarbij rekening gehouden wordt met reeds specifieke resources.
     * @param localDateTime Het gegeven tijdStip
     * @return Waar indien er een ander plan gemaakt kan worden met de specifieke resources op het gegeven tijdStip
     */
    public boolean hasEquivalentPlan(LocalDateTime localDateTime) {
        return getResourcePlanner().hasEquivalentPlan(this, localDateTime);
    }


    private ResourcePlanner getResourcePlanner() {
        return resourcePlanner;
    }

    public LocalDateTime getPlannedStartTime() {
        return getTimeSpan().getStartTime();
    }

    public LocalDateTime getPlannedEndTime() {
        return getTimeSpan().getEndTime();
    }

    /**
     * Controleer of dit plan start voor een gegeven Plan.
     * @param other
     * @return
     */
    // TODO: wordt niet gebruikt
    public boolean startsBefore(Plan other){
        return other.getPlannedStartTime().isAfter(getPlannedStartTime());
    }

    /**
     * Controleer of dit plan eindig na het gegeven Plan.
     * @param other
     * @return
     */
    // TODO: wordt niet gebruikt
    public boolean endsAfter(Plan other){
        return other.getPlannedEndTime().isBefore(getPlannedEndTime());
    }

    @Override
    public String toString() {
        return "Plan{" +
                "reservations=" + reservations +
                ",\n task=" + task +
                ",\n resourcePlanner=" + resourcePlanner +
                ",\n timeSpan=" + timeSpan +
                '}';
    }

    /**
     * Geeft de lijst van Specifiek gekozen ResourceInstances voor dit plan.
     * @return
     */
    public List<ResourceInstance> getSpecificResources() {
        List<ResourceInstance> instances = new ArrayList<>(reservations.stream().filter(
                reservation -> reservation.isSpecific() && !reservation.isDeveloperReservation()
        ).map(
                resv -> resv.getResourceInstance()).collect(Collectors.toList()
        ));

        return ImmutableList.copyOf(instances);
    }

    /**
     * Geeft de lijst van toegewezen Developers voor dit plan.
     * @return
     */
    public List<ResourceInstance> getAssignedDevelopers(){
        List<ResourceInstance> instances = new ArrayList<>(reservations.stream().filter(
                reservation -> reservation.isDeveloperReservation()
        ).map(
                resv -> resv.getResourceInstance()).collect(Collectors.toList()
        ));

        return ImmutableList.copyOf(instances);
    }
}
