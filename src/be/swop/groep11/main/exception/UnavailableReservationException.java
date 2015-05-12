package be.swop.groep11.main.exception;

import be.swop.groep11.main.resource.ResourceReservation;

import java.util.List;

/**
 * Created by Ronald on 12/05/2015.
 */
public class UnavailableReservationException extends RuntimeException {
    private List<ResourceReservation> overlappingReservations;
    //TODO possible immutable?
    public UnavailableReservationException(List<ResourceReservation> overlappingReservations) {
        this.overlappingReservations = overlappingReservations;
    }

    public List<ResourceReservation> getOverlappingReservations() {
        return overlappingReservations;
    }

    @Override
    public String toString() {
        return "UnavailableReservationException{" +
                "overlappingReservations=" + overlappingReservations +
                '}';
    }
}
