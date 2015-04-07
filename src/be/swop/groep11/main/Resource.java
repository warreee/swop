package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.*;
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
     *
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
     * Implementeert de methode isAvailable in de interface ResourceInstance.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return          True als deze resource vanaf de starttijd voor de gegeven duur kan gerserveerd worden,
     *                  rekening houdend met andere reservaties voor deze resource.
     */
    @Override
    public boolean isAvailable(LocalDateTime startTime, Duration duration) {
        ImmutableList<ResourceAllocation> allocations = this.getAllocations();
        TimeSpan timeSpan = new TimeSpan(startTime, this.calculateEndTime(startTime, duration));
        for (ResourceAllocation allocation : allocations) {
            if (timeSpan.overlapsWith(allocation.getTimeSpan())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implementeert de methode calculateEndTime in de interface ResourceInstance.
     * @return De eindtijd van deze resource, rekening houdend met het feit dat deze resource mogelijks niet 24/7
     *         beschikbaar is. (Er wordt hierbij GEEN rekening gehouden met de huidige reservaties van deze resource!)
     */
    @Override
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {

        if (this.getDailyAvailability() == null) {
            return startTime.plus(duration);
        }

        else {
            // de "echte" starttijd is het eerste moment dat binnen de dagelijkse beschikbaarheid ligt
            LocalDateTime realStartTime = this.getDailyAvailability().getNextTime(startTime);

            LocalDateTime currentStartTime = realStartTime;
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
