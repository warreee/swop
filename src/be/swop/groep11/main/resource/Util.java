package be.swop.groep11.main.resource;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by robin on 15/05/15.
 */
public class Util {

    public static LocalDateTime getNextHour(LocalDateTime dateTime){
        if (dateTime.getMinute() == 0)
            return dateTime;
        else
            return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour() + 1, 0));
    }

}
