package be.swop.groep11.main;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by robin on 2/04/15.
 */
public class Developer extends User implements ResourceInstance {
    public Developer(String name) {
        super(name);
    }

    @Override
    public boolean isAvailable(LocalDateTime start, Duration duration) {
        return false;
    }

    @Override
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        //TODO calculate end Time developer
        return null;
    }
}
