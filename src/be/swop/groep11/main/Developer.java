package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by robin on 2/04/15.
 */
public class Developer extends User implements ResourceInstance {

    /**
     * Constructor om een nieuwe developer aan te maken met een naam en een resource type.
     * @param name         Naam van de developer
     * @param resourceType Resource type van de developer
     * @throws java.lang.IllegalArgumentException Ongeldige resource type
     */
    public Developer(String name, ResourceType resourceType) throws IllegalArgumentException {
        super(name);
        if (resourceType == null)
            throw new IllegalArgumentException("Resource type mag niet null zijn");
        this.resourceType = resourceType;
    }

    @Override
    public boolean isAvailable(TimeSpan timeSpan) {
        return false; // TODO
    }

    @Override
    public TimeSpan getNextAvailableTimeSpan(LocalDateTime startTime, Duration duration) {
        return null; // TODO
    }

    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    private final ResourceType resourceType;

    private List<ResourceAllocation> allocations;

    /**
     * Geeft een lijst van alle resource allocaties voor deze developer.
     */
    @Override
    public ImmutableList<ResourceAllocation> getAllocations() {
        return ImmutableList.copyOf(allocations);
    }

    /**
     * Voegt een resource allocatie toe aan deze developer.
     * @throws java.lang.IllegalArgumentException Ongeldige resource allocatie
     */
    @Override
    public void addAllocation(ResourceAllocation allocation) throws IllegalArgumentException {
        if (! this.canHaveAsAllocation(allocation))
            throw new IllegalArgumentException("Ongeldige resource allocatie");
        allocations.add(allocation);
    }

    /**
     * Controleert of een resource allocatie geldig is voor deze developer.
     * @param allocation
     * @return
     */
    protected boolean canHaveAsAllocation(ResourceAllocation allocation) {
        return true; // TODO
    }

    /**
     * Verwijdert een resource allocatie voor deze resource.
     */
    @Override
    public void removeAllocation(ResourceAllocation allocation) {
        this.allocations.remove(allocation);
    }

    private DailyAvailability getDailyAvailability() {
        return dailyAvailability;
    }

    private static DailyAvailability dailyAvailability = new DailyAvailability(LocalTime.of(8,0), LocalTime.of(17,0));
}
