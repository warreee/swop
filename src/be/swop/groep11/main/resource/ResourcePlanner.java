package be.swop.groep11.main.resource;

import be.swop.groep11.main.task.Task;

import java.util.Iterator;

/**
 * Created by robin on 14/05/15.
 */
public class ResourcePlanner {

    private ResourceRepository resourceRepository;

    /**
     * Maakt een nieuwe ResourcePlanner object aan. Dit ResourcePlanner object gebruikt de gegeven ResourceRepository om
     * alle resources in te plannen.
     * @param resourceRepository
     */
    public ResourcePlanner(ResourceRepository resourceRepository){
        setResourceRepository(resourceRepository);
    }

    /**
     * Bepaalt of deze ResourcePlanner in staat is om de gegeven taak te plannen.
     * @param task De taak die gepland moet worden.
     * @return true als het mogelijk is, anders false.
     */
    public boolean canPlan(Task task){
        Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
        while (it.hasNext()){
            ResourceRequirement req = it.next();
            if(req.getAmount() > resourceRepository.getResources(req.getType()).size()){
                return false;
            }
        }
        // Voor elke requirement van taak zijn er voldoende resources beschikbaar in deze planner.
        return true;
    }

    private void setResourceRepository(ResourceRepository resourceRepository){
        checkResourceRepository(resourceRepository);
        this.resourceRepository = resourceRepository;
    }

    private void checkResourceRepository(ResourceRepository resourceRepository){
        if(resourceRepository == null){
            throw new IllegalArgumentException("ResourceRepository mag niet 'null' zijn.");
        }
    }
}
