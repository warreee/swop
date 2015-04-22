package be.swop.groep11.main.core;

import java.time.LocalDateTime;

/**
 * Created by warreee on 4/20/15.
 */
public class SystemTime {


    public SystemTime() {
        setCurrentSystemTime(LocalDateTime.now());
    }

    public SystemTime(LocalDateTime systemTime){
        this.currentSystemTime = systemTime;
    }

    public LocalDateTime getCurrentSystemTime() {
        return currentSystemTime;
    }

    public void setCurrentSystemTime(LocalDateTime currentSystemTime) {
        this.currentSystemTime = currentSystemTime;
    }

    private LocalDateTime currentSystemTime;

    public void updateSystemTime(LocalDateTime newTime)throws IllegalArgumentException{
        //TODO implement, check if valid new time, niet in het verleden, IllegalArgumentException indien geen geldige nieuwe tijd
    }

}
