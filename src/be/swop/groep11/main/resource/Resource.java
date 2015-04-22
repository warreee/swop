package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import com.google.common.collect.ImmutableList;

import java.time.*;
import java.util.List;

/**
 * Stelt een resource voor met een naam en resource type.
 */
public class Resource implements ResourceInstance {

    /**
     * Constructor om een nieuwe resource aan te maken met een naam en een type, die 24/7 beschikbaar is.
     * @param name         De naam van de resource
     * @param resourceType Het resource type
     * @throws java.lang.IllegalArgumentException Ongeldige naam of type voor de resource
     */
    public Resource(String name, IResourceType resourceType) throws IllegalArgumentException {
        if (! isValidName(name))
            throw new IllegalArgumentException("Ongeldige naam voor de resource");
        if (resourceType == null)
            throw new IllegalArgumentException("Resource type mag niet null zijn");
        this.name = name;
        this.resourceType = resourceType;
    }

    /**
     * Implementeert calculateEndTime in de interface ResourceInstance.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return TODO: uitleggen hoe dit voor Resource gedaan wordt
     */
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {

        if (this.getResourceType().getDailyAvailability() == null) {
            return startTime.plus(duration);
        }

        else {

            LocalDateTime currentStartTime = startTime;
            LocalDateTime currentEndTime   = null;
            Duration      currentDuration  = duration;

            while (!currentDuration.isZero()) {
                Duration durationUntilNextEndTime = this.getResourceType().getDailyAvailability().getDurationUntilNextEndTime(currentStartTime);
                if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                    currentEndTime = currentStartTime.plus(currentDuration);
                    currentDuration = Duration.ZERO;
                }
                else {
                    currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                    currentDuration = currentDuration.minus(durationUntilNextEndTime);
                    currentStartTime = this.getResourceType().getDailyAvailability().getNextStartTime(currentEndTime);
                }
            }

            return currentEndTime;
        }
    }

    private final IResourceType resourceType;

    /**
     * Geeft het resource type van deze resource.
     */
    @Override
    public IResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Geeft de naam van deze resource.
     */
    public String getName() {
        return name;
    }

    /**
     * Controleert of een naam een geldige naam is voor een resource.
     * @param name De te controleren naam
     * @return True als de naam niet null en niet leeg is.
     */
    public static boolean isValidName(String name) {
        return name != null && ! name.isEmpty();
    }

    private final String name;

}