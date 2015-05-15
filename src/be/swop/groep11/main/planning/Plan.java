package be.swop.groep11.main.planning;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.ResourceInstance;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.resource.ResourceRequirement;
import be.swop.groep11.main.resource.ResourceReservation;
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

}
