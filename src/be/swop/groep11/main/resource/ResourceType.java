package be.swop.groep11.main.resource;

import java.time.Duration;
import java.time.LocalDateTime;

public class ResourceType extends AResourceType {

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven parameters.
     *
     * @param typeName De naam van deze ResourceType
     */
    public ResourceType(String typeName) throws IllegalArgumentException {
        super(typeName);
    }

    /**
     * Voegt een nieuwe ResourceInstance toe aan dit ResourceType.
     * @param name De naam van de ResourceInstance die moet worden toegevoegd.
     * @throws IllegalArgumentException Wordt gegooid indien er een fout zit in de naam.
     */
    @Override
    protected void addResourceInstance(String name) throws IllegalArgumentException {
        Resource resource = new Resource(name,this);
        addResourceInstance(resource);
    }

    /**
     * Implementeert calculateEndTime in de interface ResourceInstance.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return TODO: uitleggen hoe dit voor Resource gedaan wordt
     */
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {

        if (this.getDailyAvailability() == null) {
            return startTime.plus(duration);
        }
        else {
            LocalDateTime currentStartTime = this.getDailyAvailability().getNextTime(startTime);
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

}
