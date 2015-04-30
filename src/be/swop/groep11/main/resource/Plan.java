package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Stelt een plan voor om reservaties te maken voor een taak.
 */
public class Plan {

    private ResourceManager resourceManager;

    /**
     * Constructor om een nieuw plan aan te maken met default reservaties voor de resource requirements van de gegeven taak.
     * Hierbij worden ook de default reservaties voor het plan toegevoegd.
     *
     * @param task      De gegeven taak
     * @param startTime De starttijd van het plan: moet op een uur vallen (zonder minuten)
     */
    public Plan(Task task, LocalDateTime startTime,ResourceManager resourceManager) {
        if (resourceManager == null) {
            throw new IllegalArgumentException("resourceManager mag niet null zijn");
        }
        if (task == null)
            throw new IllegalArgumentException("Taak mag niet null zijn");
        if (startTime == null)
            throw new IllegalArgumentException("Starttijd mag niet null zijn");
        if (startTime.getMinute() != 0)
            throw new IllegalArgumentException("Ongeldige starttijd: moet op een uur vallen (zonder minuten)");
        this.task = task;
        this.startTime = startTime;
        this.resourceManager = resourceManager;
        this.makeDefaultReservations();
    }


    /**
     * Controleert of het plan geldig is.
     *
     * @return True als de reservaties niet conflicteren met andere reservaties
     * en alle nodige reservaties gemaakt zijn.
     */
    public boolean isValid() {

        // geen conflicten?
        for (ResourceReservation reservation : this.getReservations()) {
            if (!resourceManager.isAvailable(reservation.getResourceInstance(), reservation.getTimeSpan())) {
                return false;
            }
        }

        // nodige reservaties gemaakt?
        Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
        while (it.hasNext()) {
            ResourceRequirement requirement = it.next();

            List<ResourceReservation> reservations = this.getReservations(requirement.getType());

            int nbRequiredInstances = requirement.getAmount();
            int nbReservations = reservations.size();

            if (nbRequiredInstances != nbReservations) {
                return false;
            }
        }

        return true;
    }

    /**
     * Controleert of het plan geldig is, zonder developers.
     *
     * @return True als de reservaties niet conflicteren met andere reservaties
     * en alle nodige reservaties gemaakt zijn. Hierbij wordt
     * geen rekening gehouden met reservaties voor developers.
     */
    public boolean isValidWithoutDevelopers() {
        // geen conflicten?
        for (ResourceReservation reservation : this.getReservations()) {
            if (!resourceManager.isAvailable(reservation.getResourceInstance(), reservation.getTimeSpan())) {
                return false;
            }
        }

        // nodige reservaties gemaakt?
        Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
        while (it.hasNext()) {
            ResourceRequirement requirement = it.next();

            if (requirement.getType() != resourceManager.getDeveloperType()) {

                ImmutableList<ResourceReservation> reservations = this.getReservations(requirement.getType());

                int nbRequiredInstances = requirement.getAmount();
                int nbReservations = reservations.size();

                if (nbRequiredInstances != nbReservations) {
                    return false;
                }

            }
        }

        return true;
    }

    public void makeDefaultReservations() {
        this.reservations = calculateDefaultReservations(this.getTask(), this.getStartTime());
    }

    /**
     * Geeft de taak van het plan.

     */
    public Task getTask() {
        return task;
    }
    private final Task task;

    /**
     * Geeft de starttijd van het plan.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }
    private final LocalDateTime startTime;
    /**
     * Geeft de eindtijd van het plan.
     */
    public LocalDateTime getEndTime() {
        if (this.getReservations().isEmpty()) {
            return this.getStartTime().plus(this.getTask().getEstimatedDuration());
        } else {
            LocalDateTime endTime = this.getStartTime().plus(this.getTask().getEstimatedDuration());
            for (ResourceReservation reservation : this.getReservations()) {
                if (reservation.getTimeSpan().getEndTime().isAfter(endTime)) {
                    endTime = reservation.getTimeSpan().getEndTime();
                }
            }
            return endTime;
        }
    }
    private List<ResourceReservation> reservations = new LinkedList<>();

    /**
     * Geeft de reservaties van dit plan.
     *
     * @return De reservaties van dit plan en die eindigen allemaal op hetzelfde moment.
     */
    public ImmutableList<ResourceReservation> getReservations() {
        List<ResourceReservation> reservations = new LinkedList<>();
        // zorg wel dat alle reservaties op het zelfde moment eindigen!
        for (ResourceReservation reservation : this.reservations) {
            if (reservation.getTimeSpan().getEndTime().isBefore(this.maxEndTime())) {
                reservations.add(new ResourceReservation(reservation.getTask(),
                        reservation.getResourceInstance(),
                        new TimeSpan(reservation.getTimeSpan().getStartTime(), this.maxEndTime()),
                        reservation.isSpecific()));
            }
            else {
                reservations.add(new ResourceReservation(reservation.getTask(),
                        reservation.getResourceInstance(),
                        reservation.getTimeSpan(),
                        reservation.isSpecific()));
            }
        }
        return ImmutableList.copyOf(reservations);
    }

    /**
     * Geeft de reservaties van dit plan voor een bepaalde resource type.
     *
     * @param resourceType Het resource type
     * @return De reservaties van dit plan en die eindigen allemaal op hetzelfde moment.
     */
    public ImmutableList<ResourceReservation> getReservations(AResourceType resourceType) {
        List<ResourceReservation> reservations = new LinkedList<>();
        ImmutableList<ResourceReservation> reservationlist = this.getReservations();
        for (ResourceReservation reservation : reservationlist) {
            if (reservation.getResourceInstance().getResourceType() == resourceType) {
                reservations.add(reservation);
            }
        }
        return ImmutableList.copyOf(reservations);
    }

    private LocalDateTime maxEndTime() {
        LocalDateTime maxEndTime = this.getStartTime();
        for (ResourceReservation reservation : this.reservations) {
            if (reservation.getTimeSpan().getEndTime().isAfter(maxEndTime)) {
                maxEndTime = reservation.getTimeSpan().getEndTime();
            }
        }
        return maxEndTime;
    }

    /**
     * Gooit de huidige reservaties weg en voegt reservaties voor de gegeven resource instanties toe.
     * @param resourceInstances De gegeven resource instanties
     */
    public void changeReservations(List<ResourceInstance> resourceInstances) {
        this.reservations = new LinkedList<>();
        for (ResourceInstance resourceInstance : resourceInstances) {
            this.addReservation(resourceInstance);
        }
    }

    /**
     * Voegt reservaties voor de gegeven resource instanties toe.
     * @param resourceInstances De gegeven resource instanties
     */
    public void addReservations(List<ResourceInstance> resourceInstances) {
        for (ResourceInstance resourceInstance : resourceInstances) {
            this.addReservation(resourceInstance);
        }
    }

    /**
     * Voegt een reservatie voor een resource instantie toe aan dit plan.
     * De toegevoegde reservatie zal een specifieke reservatie zijn.
     * @param resourceInstance De te reserveren resource instantie
     * @throws IllegalArgumentException Er is in dit plan al een reservatie voor de gegeven resource instantie gemaakt.
     */
    public void addReservation(ResourceInstance resourceInstance) {
        if (this.hasReservationFor(resourceInstance)) {
            throw new IllegalArgumentException("Er is in dit plan al een reservatie voor de gegeven resource instantie gemaakt.");
        }

        this.reservations.add(new ResourceReservation(this.getTask(),
                resourceInstance,
                new TimeSpan(this.getStartTime(), resourceManager.getNextAvailableTimeSpan(resourceInstance,this.getStartTime(),this.getTask().getEstimatedDuration()).getEndTime()),
                true));
    }


    /**
     * Controleert of dit plan een reservatie voor een resource instantie bevat.
     */
    public boolean hasReservationFor(ResourceInstance resourceInstance) {
        for (ResourceReservation reservation : this.reservations) {
            if (reservation.getResourceInstance() == resourceInstance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Geeft de gereserveerde resources van de taak weer vrij.
     *
     * @param endTime De tijd waarop de reservaties van de taak moeten eindigen.
     */
    public void releaseResources(LocalDateTime endTime) {
        resourceManager.endReservationsFromTask(this.getTask(), endTime);
    }


    /**
     * Geeft de taken die reservaties hebben die conflicteren met de reservaties van dit plan.
     */
    public List<Task> getConflictingTasks() {
        Set<Task> conflictingTasks = new HashSet<>();
        for (ResourceReservation reservation : this.getReservations()) {
            List<ResourceReservation> conflictingReservations = resourceManager.getConflictingReservations(reservation.getResourceInstance(), reservation.getTimeSpan());
            if (! conflictingReservations.isEmpty()) {
                conflictingTasks.add(conflictingReservations.get(0).getTask());
            }
        }
        return new LinkedList<>(conflictingTasks);
    }

    /**
     * Past dit plan toe door huidige de reservaties van de taak te verwijderen
     * en de reservaties van het plan toe te voegen.
     * @throws IllegalStateException De reservaties voor dit plan kunnen niet gemaakt worden.
     */
    public void apply() {
        try {
            resourceManager.endReservationsFromTask(this.getTask(), this.getStartTime());
            resourceManager.makeReservationsForPlan(this);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalStateException("De reservaties voor dit plan kunnen niet gemaakt worden.");
        }
    }

    private List<ResourceReservation> calculateDefaultReservations(Task task, LocalDateTime startTime) throws IllegalArgumentException {
        List<ResourceReservation> defaultReservations = new LinkedList<>();

        Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
        while (it.hasNext()) {
            ResourceRequirement requirement = it.next();

            if (requirement.getType() != resourceManager.getDeveloperType()) {
                List<ResourceInstance> availableInstances = resourceManager.getAvailableInstances(requirement.getType(), startTime, task.getEstimatedDuration());

                int nbRequiredInstances = requirement.getAmount();
                int nbAvailableInstances = availableInstances.size();
                if (nbAvailableInstances < nbRequiredInstances) {
                    // maak geen reservaties
                } else {
                    // voeg de nodige reservaties toe
                    for (int i = 0; i < nbRequiredInstances; i++) {
                        defaultReservations.add(new ResourceReservation(task, availableInstances.get(i),
                                resourceManager.getNextAvailableTimeSpan(availableInstances.get(i), startTime, task.getEstimatedDuration()), false));
                    }
                }
            }
        }
        return defaultReservations;
    }
}
