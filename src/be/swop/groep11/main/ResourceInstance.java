package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een interface voor die elke instantie van een resource type moet implementeren.
 */
public interface ResourceInstance {

    /**
     * Controleert of de resource instantie beschikbaar is gedurende een gegeven tijdsspanne.
     * @param timeSpan De gegeven tijdsspanne
     * @return True als deze resource instantie beschikbaar is.
     */
    public boolean isAvailable(TimeSpan timeSpan);

    /**
     * Geeft de eerst volgende tijdsspanne waarin de resource instantie voor een gegeven duur beschikbaar is,
     * na een gegeven starttijd.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     */
    public TimeSpan getNextAvailableTimeSpan(LocalDateTime startTime, Duration duration);

    /**
     * Geeft het resource type van de resource instantie.
     */
    public ResourceType getResourceType();

    /**
     * Geeft een lijst van resource allocaties voor de resource instantie.
     */
    public ImmutableList<ResourceAllocation> getAllocations();

    /**
     * Voegt een resource allocatie toe.
     */
    public void addAllocation(ResourceAllocation allocation);

    /**
     * Verwijdert een resource allocatie.
     */
    public void removeAllocation(ResourceAllocation allocation);
}
