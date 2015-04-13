package be.swop.groep11.main;

import be.swop.groep11.main.resource.ResourceInstance;

/**
 * Stelt de allocatie van een resource instantie gedurende een bepaalde tijdsspanne voor.
 */
public class ResourceAllocation {

    /**
     * Constructor om een resource allocatie aan te maken met een resource instantie en een tijdsspanne
     * @param resourceInstance De resource instantie
     * @param timeSpan         De tijdsspanne
     * @throws java.lang.IllegalArgumentException De resource instantie of tijdsspanne is null,
     *                                            of de allocatie is niet geldig voor de resource instantie.
     */
    public ResourceAllocation(ResourceInstance resourceInstance, TimeSpan timeSpan) throws IllegalArgumentException {
        if (resourceInstance == null)
            throw new IllegalArgumentException("Resource instantie mag niet null zijn");
        if (timeSpan == null)
            throw new IllegalArgumentException("Tijdsspanne mag niet null zijn");
        this.resourceInstance = resourceInstance;
        this.timeSpan = timeSpan;
        resourceInstance.addAllocation(this); // nodig om vanuit de resource instance alle allocaties ervan te kunnen opvragen
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
