package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.planning.Plan;

import java.util.List;

/**
 * Een interface voor een IResourcePlannerMemento.
 */
public interface IResourcePlannerMemento {
    //Voor afscherming memento
    List<Plan> getPlans();
}
