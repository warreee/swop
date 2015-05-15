package be.swop.groep11.main.core;

import be.swop.groep11.main.util.Observable;

import java.time.LocalDateTime;

/**
 * Houdt een systeemtijd bij.
 */
public class SystemTime extends Observable<SystemTime>{


    /**
     * Initialiseerd deze SystemTime met de huidige tijd.
     */
    public SystemTime() {
        this.currentSystemTime = LocalDateTime.now();
    }



    /**
     * Initialiseerd deze SystemTime met de doorgegeven tijd.
     * @param systemTime De tijd die deze SystemTime moet bevatten.
     */
    public SystemTime(LocalDateTime systemTime){
        this.currentSystemTime = systemTime;
    }

    /**
     * Haalt de tijd op die in deze SystemTime wordt bijgehouden.
     * @return De tijd.
     */
    public LocalDateTime getCurrentSystemTime() {
        return currentSystemTime;
    }

    /**
     * Verzet de SystemTime naar een andere tijd. Dit kan alleen maar naar de toekomst gebeuren.
     * @param currentSystemTime De nieuwe tijd.
     * @throws IllegalArgumentException Wordt gegooid wanneer een onmogelijke nieuwe systeemtijd wordt gezet.
     */
    private void setCurrentSystemTime(LocalDateTime currentSystemTime)throws IllegalArgumentException {
        if (!canHaveAsNewSystemTime(currentSystemTime)) {
            throw new IllegalArgumentException("Ongeldige nieuwe tijd");
        }
        this.currentSystemTime = currentSystemTime;
    }

    /**
     * Controlleerd of dit een geldige nieuw systeemtijd is.
     * @param currentSystemTime De nieuwe systeemtijd.
     * @return True als de doorgegeven systeemtijd een geldige tijd is. Anders False.
     */
    private boolean canHaveAsNewSystemTime(LocalDateTime currentSystemTime) {
        return currentSystemTime != null && currentSystemTime.isAfter(getCurrentSystemTime());
    }

    private LocalDateTime currentSystemTime;

    /**
     * Verzet de SystemTime naar een andere tijd. Dit kan alleen maar naar de toekomst gebeuren.
     * @param newTime De nieuwe tijd.
     * @throws IllegalArgumentException Wordt gegooid wanneer een onmogelijke nieuwe systeemtijd wordt gezet.
     */
    public void updateSystemTime(LocalDateTime newTime)throws IllegalArgumentException {
        setCurrentSystemTime(newTime);
//        updateObservers();
    }

}
