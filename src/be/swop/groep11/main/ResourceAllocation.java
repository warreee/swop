package be.swop.groep11.main;

/**
 * Created by robin on 2/04/15.
 */
public class ResourceAllocation {
    public ResourceAllocation(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        // TODO: isValid...(...) en ook controleren of de resource instance wel beschikbaar is voor de timeSpan ( met isAvailable(timeSpan) )
        this.resourceInstance = resourceInstance;
        this.timeSpan = timeSpan;
        resourceInstance.addAllocation(this); // nodig om vanuit de resource instance alle allocaties ervan te kunnen opvragen
    }

    private TimeSpan timeSpan;

    public TimeSpan getTimeSpan() {
        return timeSpan;
    }

    public ResourceInstance getResourceInstance() {
        return resourceInstance;
    }

    private final ResourceInstance resourceInstance;
}
