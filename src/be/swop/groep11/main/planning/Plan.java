package be.swop.groep11.main.planning;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stelt een plan voor een taak in een resource planner voor.
 * Een plan heeft een tijdspanne en resource reservaties.
 * Alle reservaties hebben dezelfde tijdspanne als die van het plan.
 */
public class Plan {

    protected Plan(Task task, ResourcePlanner resourcePlanner, List<ResourceReservation> reservations) {
        // TODO
    }

    /**
     * Geeft de tijdspanne van dit plan.
     */
    public TimeSpan getTimeSpan() {
        // TODO
        return null;
    }

    /**
     * Verwijdert dit plan door alle reservaties ervan te verwijderen,
     * dit plan uit de resource planner te halen en dit plan uit de bijhorende taak te halen.
     */
    public void clear() {
        // TODO
    }

    public Task getTask(){
        return null;
    }

    /**
     * Laat de reservaties van dit plan vroeger eindigen dan gepland.
     * Dit wordt gedaan door voor alle reservaties de eindtijd op de gegeven eindtijd te zetten.
     * @param endTime De gegeven eindtijd
     * @throws IllegalArgumentException De gegeven eindtijd ligt buiten de  tijdspanne van dit plan.
     */
    public void clearFutureReservations(LocalDateTime endTime) {
        // TODO
    }

    /**
     * Geeft de lijst van reservaties van dit plan.
     */
    public List<ResourceReservation> getReservations() {
        // TODO
        return null;
    }

    public List<ResourceReservation> getReservations(AResourceType type){
        //TODO
        return null;
    }

    /**
     * Controleert of dit plan voldoende reservaties heeft voor een gegeven resource requirement.
     * @param resourceRequirement De gegeven resource requirement
     * @return True als er in dit plan minstens het aantal resources van de requirement gereserveerd is
     *         voor het resource type van de requirement.
     */
    public boolean hasReservationsFor(ResourceRequirement resourceRequirement) {
        // TODO
        return false;
    }

    /** TODO: implementeren
     * Deze methode gaat na of dat er voor de gegeven systemTime een equivalent plan bestaat voor de taak
     * Deze moet rekening houden met de resourcerepository waarnaar hij gedelegeerd is.
     * @return
     */
    public boolean hasEquivalentPlan() {
        return false;
    }

    /**
     * Geeft aan of de gegeven LocalDateTime in de periode valt voor dit plan.
     * @param localDateTime     De gegeven LocalDateTime
     * @return                  Waar indien de localDateTime in de timespan van dit plan valt, anders niet waar.
     */
    public boolean isWithinPlanTimeSpan(LocalDateTime localDateTime) {
        return getTimeSpan().containsLocalDateTime(localDateTime);
    }

    public boolean isAvailableForUnplannedExecution(){
        //TODO past deze methode hier?
        return false;
    }

}
