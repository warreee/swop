package be.swop.groep11.main;

import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.ResourceAllocation;
import be.swop.groep11.main.resource.ResourceInstance;
import be.swop.groep11.main.resource.ResourceType;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
        this.allocations = new ArrayList<>();
    }

    /**
     * Controleert of deze developer beschikbaar is gedurende een gegeven tijdsspanne.
     * @param timeSpan De gegeven tijdsspanne
     * @return True als deze developer beschikbaar is, rekening houdend met dat de developer al "gereserveerd" kan zijn.
     */
    @Override
    public boolean isAvailable(TimeSpan timeSpan) {
        return this.getConflictingAllocations(timeSpan).isEmpty();
    }

    private List<ResourceAllocation> getConflictingAllocations(TimeSpan timeSpan) {
        List<ResourceAllocation> conflictingAllocations = new ArrayList<>();
        ImmutableList<ResourceAllocation> allocations = this.getAllocations();
        for (ResourceAllocation allocation : allocations) {
            if (timeSpan.overlapsWith(allocation.getTimeSpan())) {
                conflictingAllocations.add(allocation);
            }
        }
        return conflictingAllocations;
    }

    /**
     * Geeft de eerst volgende tijdsspanne waarin deze resource voor een gegeven duur beschikbaar is,
     * na een gegeven starttijd. De starttijd van de tijdsspanne is steeds op een uur (zonder minuten).
     * Hierbij wordt rekening gehouden dat een developer van 8u tot 17u werkt en een middagpauze van 1u moet
     * hebben tussen 11u en 14u.
     * en er al allocaties kunnen zijn.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     */
    @Override
    public TimeSpan getNextAvailableTimeSpan(LocalDateTime startTime, Duration duration) {
        // de "echte" starttijd is dan het eerste moment dat binnen de dagelijkse beschikbaarheid ligt
        LocalDateTime realStartTime = this.getDailyAvailability().getNextTime(this.getNextHour(startTime));

        LocalDateTime realEndTime = calculateEndTime(realStartTime, duration);
        TimeSpan timeSpan = new TimeSpan(realStartTime, realEndTime);

        if (!this.isAvailable(timeSpan)) {
            List<ResourceAllocation> conflictingAllocations = this.getConflictingAllocations(timeSpan);
            // bereken hiermee de volgende mogelijke starttijd = de grootste van alle eindtijden van de resources
            LocalDateTime nextStartTime = realStartTime;
            for (ResourceAllocation allocation : conflictingAllocations) {
                if (allocation.getTimeSpan().getEndTime().isAfter(nextStartTime)) {
                    nextStartTime = allocation.getTimeSpan().getEndTime();
                }
            }

            return getNextAvailableTimeSpan(nextStartTime, duration);
        }

        return timeSpan;
    }

    private LocalDateTime getNextHour(LocalDateTime dateTime) {
        if (dateTime.getMinute() == 0)
            return dateTime;
        else
            return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour()+1,0));
    }

    private LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        LocalDateTime currentStartTime = startTime;
        LocalDateTime currentEndTime   = null;
        Duration      currentDuration  = duration;

        while (!currentDuration.isZero()) {
            Duration durationUntilNextEndTime = this.getDailyAvailability().getDurationUntilNextEndTime(currentStartTime);
            if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                currentEndTime = currentStartTime.plus(currentDuration);
                currentDuration = Duration.ZERO;
            }
            else {
                currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                currentDuration = currentDuration.minus(durationUntilNextEndTime);
                currentStartTime = this.getDailyAvailability().getNextStartTime(currentEndTime);
            }

            // rekening houden met de middagpauze: middagpauze opnemen in de duur van de reservatie
            boolean addBreak;
            if (currentStartTime.toLocalTime().equals(startOfBreak.plus(breakDuration))) {
                // als start = 12u ==> +1 uur voor pauze
                addBreak = true;
            }
            else if (! currentStartTime.toLocalTime().isAfter(startOfBreak.plus(breakDuration)) && ! currentEndTime.toLocalTime().isBefore(startOfBreak.plus(breakDuration))) {
                // als start <= 12u & einde >= 12 ==> +1 uur voor pauze
                addBreak = true;
            }
            else {
                // anders niets toevoegen
                addBreak = false;
            }

            if (addBreak) {
                // voeg een middagpauze toe
                currentDuration = currentDuration.plus(breakDuration);
                durationUntilNextEndTime = this.getDailyAvailability().getDurationUntilNextEndTime(currentEndTime);
                if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                    /* voeg een middagpauze toe op dezelfde dag als dat kan*/
                    currentEndTime = currentEndTime.plus(breakDuration);
                    currentDuration = Duration.ZERO;
                }
                else {
                    /* voeg een middagpauze toe, (deels) op een andere dag */
                    currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                    currentDuration = currentDuration.minus(durationUntilNextEndTime);
                    currentStartTime = this.getDailyAvailability().getNextStartTime(currentEndTime);
                }
            }
        }

        return currentEndTime;
    }

    private LocalDateTime calculateEndTimeOld(LocalDateTime startTime, Duration duration) {
        LocalDateTime currentStartTime = startTime;
        LocalDateTime currentEndTime   = null;
        Duration      currentDuration  = duration;

        while (!currentDuration.isZero()) {
            Duration durationUntilNextEndTime = this.getDailyAvailability().getDurationUntilNextEndTime(currentStartTime);
            if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                currentEndTime = currentStartTime.plus(currentDuration);
                currentDuration = Duration.ZERO;
            }
            else {
                currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                currentDuration = currentDuration.minus(durationUntilNextEndTime);
                currentStartTime = this.getDailyAvailability().getNextStartTime(currentEndTime);
            }

            // rekening houden met de middagpauze: middagpauze opnemen in de duur van de reservatie
            // verschillende gevallen: zie uml/Developer.calculateEndTime - verschillende gevallen.jpg
            boolean addBreak;
            if (currentEndTime.toLocalTime().isAfter(endOfBreak.minus(breakDuration)) && currentStartTime.toLocalTime().isBefore(startOfBreak.plus(breakDuration))) {
                /* GEVAL 3 en 4: voeg een middagpauze toe als [currentStartTime] < [endOfBreak]+[breakDuration] & [currentEndTime] > [endOfBreak]-[breakDuration] */
                addBreak = true;
            }
            else if (! currentEndTime.toLocalTime().isAfter(endOfBreak.minus(breakDuration))) {
                /* GEVAL 1 */
                if (! currentEndTime.toLocalTime().isBefore(startOfBreak)) {
                    /* voeg een middagpauze toe op dezelfde dag als [currentEndTime] >= [startOfBreak] */
                    addBreak = true;
                }
                else {
                    addBreak = false;
                }
            }
            else {
                /* GEVAL 2 */
                addBreak = false;
            }

            if (addBreak) {
                // voeg een middagpauze toe
                currentDuration = currentDuration.plus(breakDuration);
                durationUntilNextEndTime = this.getDailyAvailability().getDurationUntilNextEndTime(currentEndTime);
                if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                    /* voeg een middagpauze toe op dezelfde dag als dat kan*/
                    currentEndTime = currentEndTime.plus(breakDuration);
                    currentDuration = Duration.ZERO;
                }
                else {
                    /* voeg een middagpauze toe, (deels) op een andere dag */
                    currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                    currentDuration = currentDuration.minus(durationUntilNextEndTime);
                    currentStartTime = this.getDailyAvailability().getNextStartTime(currentEndTime);
                }
            }
        }

        return currentEndTime;
    }

    private static LocalTime startOfBreak  = LocalTime.of(11,0);
    private static LocalTime endOfBreak    = LocalTime.of(14,0);
    private static Duration  breakDuration = Duration.ofHours(1);

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
