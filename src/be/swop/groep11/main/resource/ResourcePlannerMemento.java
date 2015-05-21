package be.swop.groep11.main.resource;

import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.util.Observable;
import com.rits.cloning.Cloner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Houdt een moment in de tijd bij van ResourcePlanner.
 */
public class ResourcePlannerMemento implements IResourcePlannerMemento {

    //Sleutel = StartTijd van Plan, waarde is lijst van plannen met zelfde StartTijd
    private TreeMap<LocalDateTime, ArrayList<Plan>> planMap;
//    private List<Plan> plans;

    /**
     * Haal alle plannen die in deze memento zitten op.
     * @return Een lijst met plannen.
     */
    public TreeMap<LocalDateTime, ArrayList<Plan>> getPlans() {
        return this.planMap;
    }

    /**
     * Zet alle plannen die in deze memento moeten zitten.
     * @param planMap De plannen die er in zouden moeten zitten.
     */
    public void setPlans(TreeMap<LocalDateTime, ArrayList<Plan>> planMap) {
        Cloner cloner = new Cloner();
        cloner.dontCloneInstanceOf(Task.class);
        cloner.dontCloneInstanceOf(ResourceRepository.class);
        cloner.dontCloneInstanceOf(ResourceTypeRepository.class);
        cloner.dontCloneInstanceOf(AResourceType.class);
        cloner.dontCloneInstanceOf(ResourceInstance.class);
        cloner.dontCloneInstanceOf(Observable.class);
        TreeMap<LocalDateTime, ArrayList<Plan>> plansClone = cloner.deepClone(planMap);
//        TreeMap<LocalDateTime, ArrayList<Plan>> plansClone = new TreeMap<>(planMap);
        this.planMap = plansClone;
    }

}