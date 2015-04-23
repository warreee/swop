package be.swop.groep11.main.core;

import java.time.LocalDateTime;

/**
 * Created by warreee on 4/20/15.
 */
public class SystemTime {


    public SystemTime() {
        this.currentSystemTime = LocalDateTime.now();
    }

    public SystemTime(LocalDateTime systemTime){
        this.currentSystemTime = systemTime;
    }

    public LocalDateTime getCurrentSystemTime() {
        return currentSystemTime;
    }

    public void setCurrentSystemTime(LocalDateTime currentSystemTime)throws IllegalArgumentException {
        if (!canHaveAsNewSystemTime(currentSystemTime)) {
            throw new IllegalArgumentException("Ongeldige nieuwe tijd");
        }
        this.currentSystemTime = currentSystemTime;
    }

    private boolean canHaveAsNewSystemTime(LocalDateTime currentSystemTime) {
        return currentSystemTime != null && currentSystemTime.isAfter(getCurrentSystemTime());
    }

    private LocalDateTime currentSystemTime;

    public void updateSystemTime(LocalDateTime newTime)throws IllegalArgumentException {
        setCurrentSystemTime(newTime);
    }

}
