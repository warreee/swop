package be.swop.groep11.main.util;

import be.swop.groep11.main.core.SystemTime;

/**
 * Created by Ronald on 15/05/2015.
 */
public interface SystemTimeObserver {
    /**
     * Notify/update deze Observer.
     * @param systemTime
     */
    void update(SystemTime systemTime);}
