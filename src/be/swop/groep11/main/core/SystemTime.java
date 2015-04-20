package be.swop.groep11.main.core;

import java.time.LocalDateTime;

/**
 * Created by warreee on 4/20/15.
 */
public class SystemTime {


    public SystemTime() {
        setCurrentSystemTime(LocalDateTime.now());
    }

    public LocalDateTime getCurrentSystemTime() {
        return currentSystemTime;
    }

    private void setCurrentSystemTime(LocalDateTime currentSystemTime) {
        this.currentSystemTime = currentSystemTime;
    }

    private LocalDateTime currentSystemTime;
}
