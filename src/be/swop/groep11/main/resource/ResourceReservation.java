package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;

/**
 * Stelt de reservatie van een resource instantie voor een taak gedurende een bepaalde tijdsspanne voor.
 */
public class ResourceReservation{

    /**
     * Constructor om een nieuwe reservatie van een resource instantie voor een taak
     * gedurende een bepaalde tijdsspanne te maken.
     * @param task             De taak
     * @param resourceInstance De resource instantie
     * @param timeSpan         De tijdsspanne
     * @param isSpecific       True als het om een reservatie van een specifiek resource instantie gaat
     * @throws java.lang.IllegalArgumentException De taak, resource instantie of tijdsspanne is null
     */
    public ResourceReservation(Task task, ResourceInstance resourceInstance, TimeSpan timeSpan, boolean isSpecific) throws IllegalArgumentException {
        if (task == null)
            throw new IllegalArgumentException("Taak mag niet null zijn");
        if (resourceInstance == null)
            throw new IllegalArgumentException("Resource instantie mag niet null zijn");
        if (timeSpan == null)
            throw new IllegalArgumentException("Tijdsspanne mag niet null zijn");
        this.task = task;
        this.resourceInstance = resourceInstance;
        this.TimeSpan = timeSpan;
        this.isSpecific = isSpecific;
    }

    private final Task task;

    /**
     * Geeft de taak van deze resource reservatie.
     */
    public Task getTask() {
        return task;
    }

    private final TimeSpan TimeSpan;

    /**
     * Geeft de tijdsspanne van deze resource allocatie.
     */
    public TimeSpan getTimeSpan() {
        return TimeSpan;
    }

    private final ResourceInstance resourceInstance;

    /**
     * Geeft de resource instantie van deze resource allocatie.
     */
    public ResourceInstance getResourceInstance() {
        return resourceInstance;
    }

    private final boolean isSpecific;

    /**
     * Geeft true als deze reservatie een reservatie van een specifiek resource instantie is.
     */
    public boolean isSpecific() {
        return isSpecific;
    }

    @Override
    public String toString() {
        return getTimeSpan().getStartTime().toString() + "  -  " + getTimeSpan().getEndTime().toString() + " - " + getResourceInstance();
    }

    /**
     * Controleer of deze ResourceReservation een reservatie is voor een Developer.
     */
    public boolean isDeveloperReservation() {
        return resourceInstance instanceof Developer;
    }
}
