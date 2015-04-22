package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;

/**
 * Stelt de allocatie van een resource instantie gedurende een bepaalde tijdsspanne voor.
 */
public abstract class ResourceReservation {

    /**
     * Constructor om een resource reservatie aan te maken met een resource instantie en een tijdsspanne
     * @param resourceInstance De resource instantie
     * @param timeSpan         De tijdsspanne
     * @throws java.lang.IllegalArgumentException De resource instantie of tijdsspanne is null
     */
    public ResourceReservation(ResourceInstance resourceInstance, TimeSpan timeSpan) throws IllegalArgumentException {
        if (resourceInstance == null)
            throw new IllegalArgumentException("Resource instantie mag niet null zijn");
        if (timeSpan == null)
            throw new IllegalArgumentException("Tijdsspanne mag niet null zijn");
        this.resourceInstance = resourceInstance;
        this.timeSpan = timeSpan;
    }

    private TimeSpan timeSpan;

    /**
     * Geeft de tijdsspanne van deze resource allocatie.
     */
    public TimeSpan getTimeSpan() {
        return timeSpan;
    }

    private final ResourceInstance resourceInstance;

    /**
     * Geeft de resource instantie van deze resource allocatie.
     */
    public ResourceInstance getResourceInstance() {
        return resourceInstance;
    }
}
