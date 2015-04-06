package be.swop.groep11.main;

import java.time.*;

/**
 * Created by arnedebrabandere on 3/04/15.
 */
public class Resource implements ResourceInstance {

    /**
     * Constructor om een nieuwe resource aan te maken met een naam, die 24/7 beschikbaar is.
     * @param name De naam van de resource
     * @throws java.lang.IllegalArgumentException Ongeldige naam voor de resource
     */
    public Resource(String name) throws IllegalArgumentException {
        if (! isValidName(name))
            throw new IllegalArgumentException("Ongeldige naam voor de resource");
        this.name = name;
    }

    /**
     * Constructor om een nieuwe resource aan te maken met een naam en een dagelijkse beschikbaarheid.
     * De starttijd en eindtijd van de dagelijkse beschikbaarheid geven aan wanneer de resource beschikbaar is
     * van maandag tot en met vrijdag. Op zaterdag en zondag zal de resource niet beschikbaar zijn.
     * @param name      De naam van de resource
     * @param startTime De tijd vanaf wanneer de resource van maandag tot en met vrijdag beschikbaar is
     * @param endTime   De tijd tot wanneer de resource van maandag tot en met vrijdag beschikbaar is
     * @throws java.lang.IllegalArgumentException Ongeldige naam of ongeldige start- en/of eindtijd voor de resource
     */
    public Resource(String name, LocalTime startTime, LocalTime endTime) throws IllegalArgumentException {
        this(name);
        this.dailyAvailability = new DailyAvailability(startTime, endTime);
    }

    @Override
    public boolean isAvailable(LocalDateTime startTime, Duration duration) {
        return false;
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

    public String getName() {
        return name;
    }

    public static boolean isValidName(String name) {
        return name != null && ! name.isEmpty();
    }

    private final String name;

    private DailyAvailability getDailyAvailability() {
        return dailyAvailability;
    }

    private DailyAvailability dailyAvailability;

}
