package be.swop.groep11.main.resource;

import be.swop.groep11.main.planning.Plan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Een interface voor een IResourcePlannerMemento.
 */
public interface IResourcePlannerMemento {
    //Voor afscherming memento
    TreeMap<LocalDateTime, ArrayList<Plan>> getPlans();
}
