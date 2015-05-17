package be.swop.groep11.main.util;

import be.swop.groep11.main.resource.ResourcePlanner;

/**
 * Created by Ronald on 15/05/2015.
 */
public interface ResourcePlannerObserver {

    /**
     * Notify/update deze Observer.
     * @param resourcePlanner
     */
    void update(ResourcePlanner resourcePlanner);
}
