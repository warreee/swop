package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.core.User;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stelt een developer voor met een naam en resource type.
 */
public class Developer extends User implements ResourceInstance {

    /**
     * Constructor om een nieuwe developer aan te maken met een naam en een resource type.
     * @param name         Naam van de developer
     * @param resourceType Resource type van de developer
     * @throws java.lang.IllegalArgumentException Ongeldige resource type
     */
    public Developer(String name, IResourceType resourceType) throws IllegalArgumentException {
        super(name);
        if (resourceType == null)
            throw new IllegalArgumentException("Resource type mag niet null zijn");
        this.resourceType = resourceType;
    }

    /**
     * Implementeert calculateEndTime in de interface ResourceInstance.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return TODO: uitleggen hoe dit voor Developer gedaan wordt
     */
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        LocalDateTime currentStartTime = this.getResourceType().getDailyAvailability().getNextTime(startTime);;
        LocalDateTime currentEndTime   = null;
        Duration      currentDuration  = duration;

        while (!currentDuration.isZero()) {
            LocalDateTime newStartTime;
            Duration newDuration;

            Duration durationUntilNextEndTime = this.getResourceType().getDailyAvailability().getDurationUntilNextEndTime(currentStartTime);
            if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                currentEndTime = currentStartTime.plus(currentDuration);
                newDuration = Duration.ZERO;
                newStartTime = currentStartTime;
            }
            else {
                currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                newDuration = currentDuration.minus(durationUntilNextEndTime);
                newStartTime = this.getResourceType().getDailyAvailability().getNextStartTime(currentEndTime);
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
                durationUntilNextEndTime = this.getResourceType().getDailyAvailability().getDurationUntilNextEndTime(currentEndTime);
                if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                    /* voeg een middagpauze toe op dezelfde dag als dat kan*/
                    currentEndTime = currentEndTime.plus(breakDuration);
                    currentDuration = Duration.ZERO;
                }
                else {
                    /* voeg een middagpauze toe, (deels) op een andere dag */
                    currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                    currentDuration = currentDuration.minus(durationUntilNextEndTime);
                    currentStartTime = this.getResourceType().getDailyAvailability().getNextStartTime(currentEndTime);
                }
            }

            currentStartTime = newStartTime;
            currentDuration  = newDuration;
        }

        return currentEndTime;
    }

    private static LocalTime startOfBreak  = LocalTime.of(11,0);
    private static LocalTime endOfBreak    = LocalTime.of(14,0);
    private static Duration  breakDuration = Duration.ofHours(1);

    @Override
    public IResourceType getResourceType() {
        return resourceType;
    }

    private final IResourceType resourceType;

}
