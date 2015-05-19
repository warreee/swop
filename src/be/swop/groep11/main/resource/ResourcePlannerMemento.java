package be.swop.groep11.main.resource;

import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.task.Task;
import com.rits.cloning.Cloner;

import java.util.List;

/**
 * Houdt een moment in de tijd bij van ResourcePlanner.
 */
public class ResourcePlannerMemento implements IResourcePlannerMemento {

    private List<Plan> plans;

    /**
     * Haal alle plannen die in deze memento zitten op.
     * @return Een lijst met plannen.
     */
    public List<Plan> getPlans() {
        return this.plans;
    }

    /**
     * Zet alle plannen die in deze memento moeten zitten.
     * @param plans De plannen die er in zouden moeten zitten.
     */
    public void setPlans(List<Plan> plans) {
        Cloner cloner = new Cloner();
        cloner.dontCloneInstanceOf(Task.class);
        cloner.dontCloneInstanceOf(ResourceInstance.class);
        List<Plan> plansClone = cloner.deepClone(plans);
        this.plans = plansClone;
    }

}