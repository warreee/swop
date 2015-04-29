package be.swop.groep11.main.resource;

import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stelt een plan voor om reservaties te maken voor een taak.
 */
public interface IPlan {

        /**
         * Controleert of het plan geldig is.
         *
         * @return True als de reservaties niet conflicteren met andere reservaties
         *         en alle nodige reservaties gemaakt zijn.
         */
        boolean isValid();

        /**
         * Controleert of het plan geldig is, zonder developers.
         *
         * @return True als de reservaties niet conflicteren met andere reservaties
         *         en alle nodige reservaties gemaakt zijn. Hierbij wordt
         *         geen rekening gehouden met reservaties voor developers.
         */
        boolean isValidWithoutDevelopers();

        /**
         * Geeft de taak van het plan.
         * @return
         */
        Task getTask();

        /**
         * Geeft de starttijd van het plan.
         */
        LocalDateTime getStartTime();

        /**
         * Geeft de eindtijd van het plan.
         */
        LocalDateTime getEndTime();

        /**
         * Geeft de reservaties van het plan.
         */
        ImmutableList<ResourceReservation> getReservations();

        /**
         * Geeft de reservaties van een bepaald resource type van het plan.
         */
        ImmutableList<ResourceReservation> getReservations(AResourceType resourceType);

        /**
         * Gooit de huidige reservaties van het plan weg en voegt reservaties voor de gegeven resource instanties toe.
         * @param resourceInstances De gegeven resource instanties
         */
        void changeReservations(List<ResourceInstance> resourceInstances);
        // TODO: mutators weg!
        /**
         * Voegt reservaties voor de gegeven resource instanties toe aan het plan.
         * @param resourceInstances De gegeven resource instanties
         */
        void addReservations(List<ResourceInstance> resourceInstances);

        /**
         * Controleert of het plan een reservatie voor een resource instantie bevat.
         */
        boolean hasReservationFor(ResourceInstance resourceInstance);

        /**
         * Geeft de gereserveerde resources van de taak weer vrij.
         * @param endTime De tijd waarop de reservaties van de taak moeten eindigen.
         */
        void releaseResources(LocalDateTime endTime);

        List<Task> getConflictingTasks();
}
