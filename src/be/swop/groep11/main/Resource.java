package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arnedebrabandere on 3/04/15.
 */
public class Resource implements ResourceInstance {

    /**
     * Constructor om een nieuwe resource aan te maken met een naam en een type, die 24/7 beschikbaar is.
     * @param name         De naam van de resource
     * @param resourceType Het resource type
     * @throws java.lang.IllegalArgumentException Ongeldige naam of type voor de resource
     */
    public Resource(String name, ResourceType resourceType) throws IllegalArgumentException {
        if (! isValidName(name))
            throw new IllegalArgumentException("Ongeldige naam voor de resource");
        if (resourceType == null)
            throw new IllegalArgumentException("Resource type mag niet null zijn");
        this.name = name;
        this.resourceType = resourceType;
    }

    /**
     * Constructor om een nieuwe resource aan te maken met een naam, een type en een dagelijkse beschikbaarheid.
     * De starttijd en eindtijd van de dagelijkse beschikbaarheid geven aan wanneer de resource beschikbaar is
     * van maandag tot en met vrijdag. Op zaterdag en zondag zal de resource niet beschikbaar zijn.
     * @param name         De naam van de resource
     * @param resourceType Het resource type
     * @param startTime    De tijd vanaf wanneer de resource van maandag tot en met vrijdag beschikbaar is
     * @param endTime      De tijd tot wanneer de resource van maandag tot en met vrijdag beschikbaar is
     * @throws java.lang.IllegalArgumentException Ongeldige naam, type of start- en/of eindtijd voor de resource
     */
    public Resource(String name, ResourceType resourceType,LocalTime startTime, LocalTime endTime) throws IllegalArgumentException {
        this(name, resourceType);
        this.dailyAvailability = new DailyAvailability(startTime, endTime);
    }

    /**
     * Controleert of deze resource beschikbaar is gedurende een gegeven tijdsspanne.
     * @param timeSpan De gegeven tijdsspanne
     * @return True als deze resoruce beschikbaar is, rekening houdend met dat de resource al gereserveerd kan zijn.
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
     * na een gegeven starttijd.
     * Hierbij wordt rekening gehouden dat deze resource niet noodzakelijk 24/7 beschikbaar is.
     * en er al allocaties kunnen zijn.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     */
    @Override
    public TimeSpan getNextAvailableTimeSpan(LocalDateTime startTime, Duration duration) {
        LocalDateTime realStartTime;
        if (this.getDailyAvailability() == null) {
            realStartTime = startTime;
        }
        else {
            // de "echte" starttijd is dan het eerste moment dat binnen de dagelijkse beschikbaarheid ligt
            realStartTime = this.getDailyAvailability().getNextTime(startTime);
        }

        LocalDateTime realEndTime = calculateEndTime(realStartTime, duration);
        TimeSpan timeSpan = new TimeSpan(realStartTime, realEndTime);

        if (! this.isAvailable(timeSpan)) {
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

    private LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {

        if (this.getDailyAvailability() == null) {
            return startTime.plus(duration);
        }

        else {

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
            }

            return currentEndTime;
        }
    }

    private final ResourceType resourceType;

    /**
     * Geeft het resource type van deze resource.
     */
    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    private List<ResourceAllocation> allocations;

    /**
     * Geeft een lijst van alle resource allocaties voor deze resource.
     */
    @Override
    public ImmutableList<ResourceAllocation> getAllocations() {
        return ImmutableList.copyOf(allocations);
    }

    /**
     * Voegt een resource allocatie toe aan deze resource.
     * @throws java.lang.IllegalArgumentException Ongeldige resource allocatie
     */
    @Override
    public void addAllocation(ResourceAllocation allocation) throws IllegalArgumentException {
        if (! this.canHaveAsAllocation(allocation))
            throw new IllegalArgumentException("Ongeldige resource allocatie");
        allocations.add(allocation);
    }

    /**
     * Controleert of een resource allocatie geldig is voor deze resource.
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

    /**
     * Geeft de naam van deze resource.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * @return
     */
    public static boolean isValidName(String name) {
        return name != null && ! name.isEmpty();
    }

    private final String name;

    private DailyAvailability getDailyAvailability() {
        return dailyAvailability;
    }

    private DailyAvailability dailyAvailability;


}
