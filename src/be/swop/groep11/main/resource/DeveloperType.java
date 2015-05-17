package be.swop.groep11.main.resource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Stelt het DeveloperType voor.
 */
public class DeveloperType extends AResourceType{
    /**
     * Maakt een DeveloperType aan
     **/
    protected DeveloperType() throws IllegalArgumentException {
        super("Developer");
        setDailyAvailability(new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(17, 0)));
    }

    /**
     * Voegt een nieuwe DeveloperInstance toe.
     * @param name De naam van de ResourceInstance die moet worden toegevoegd.
     * @throws IllegalArgumentException Wordt gegooid wanneer de naam foute waarden bevat.
     */
    @Override
    protected void addResourceInstance(String name) throws IllegalArgumentException {
        Developer dev = new Developer(name);
        addResourceInstance(dev);
    }

    /**
     * Implementeert calculateEndTime in de interface ResourceInstance.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return De tijd wanneer deze developer terug vrij is.
     */
    @Override
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        LocalDateTime currentStartTime = this.getDailyAvailability().getNextTime(startTime);;
        LocalDateTime currentEndTime   = null;
        Duration      currentDuration  = duration;

        while (!currentDuration.isZero()) {
            LocalDateTime newStartTime;
            Duration newDuration;

            Duration durationUntilNextEndTime = this.getDailyAvailability().getDurationUntilNextEndTime(currentStartTime);
            if (currentDuration.compareTo(durationUntilNextEndTime) <= 0) {
                currentEndTime = currentStartTime.plus(currentDuration);
                newDuration = Duration.ZERO;
                newStartTime = currentStartTime;
            }
            else {
                currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                newDuration = currentDuration.minus(durationUntilNextEndTime);
                newStartTime = this.getDailyAvailability().getNextStartTime(currentEndTime);
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
                newDuration = newDuration.plus(breakDuration);
                durationUntilNextEndTime = this.getDailyAvailability().getDurationUntilNextEndTime(currentEndTime);
                if (newDuration.compareTo(durationUntilNextEndTime) <= 0) {
                    /* voeg een middagpauze toe op dezelfde dag als dat kan*/
                    currentEndTime = currentEndTime.plus(breakDuration);
                    newDuration = Duration.ZERO;
                }
                else {
                    /* voeg een middagpauze toe, (deels) op een andere dag */
                    currentEndTime = currentStartTime.plus(durationUntilNextEndTime);
                    newDuration = newDuration.minus(durationUntilNextEndTime);
                    newStartTime = this.getDailyAvailability().getNextStartTime(currentEndTime);
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
}
