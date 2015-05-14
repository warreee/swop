package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by robin on 14/05/15.
 */
public class ResourcePlanner {

    private TreeMap<LocalDateTime, ArrayList<Plan>> planMap;

    /**
     * Maakt een nieuwe ResourcePlanner object aan. Dit ResourcePlanner object gebruikt de gegeven ResourceRepository om
     * alle resources in te plannen.
     *
     * @param resourceRepository
     */
    public ResourcePlanner(ResourceRepository resourceRepository){
        setResourceRepository(resourceRepository);
        planMap = new TreeMap<>();
    }

    /**
     * Bepaalt of deze ResourcePlanner in staat is om de gegeven taak in de toekomst mogelijks te plannen is.
     *
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

    /**
     * Controlleer of er het gegeven aantal ResourceTypes beschikbaar is gedurende de TimeSpan.
     *
     * @param resourceType De ResourceType die beschikbaar moet zijn.
     * @param timeSpan De TimeSpan wanneer de ResourceType beschikbaar moet zijn.
     * @param amount De hoeveelheid ResourceTypes die beschikbaar moet zijn.
     * @return true als er voldoende ResourceTypes beschikbaar zijn, anders false.
     */
    public boolean isAvailable(AResourceType resourceType, TimeSpan timeSpan, int amount){
        int count = 0;
        for(ResourceInstance resourceInstance: resourceType.getResourceInstances()){
            if(isAvailable(resourceInstance, timeSpan)){
                count++;
            }
            if(count == amount){
                return true;
            }
        }
        return false;
    }

    /**
     * Controlleer of er een ResourceInstance beschikbaar is voor het ResourceType gedurende de gegeven TimeSpan.
     *
     * @param resourceType De ResourceType die beschikbaar moet zijn.
     * @param timeSpan De TimeSpan wanneer de ResourceType beschikbaar moet zijn.
     * @return true als er 1 beschikbaar, anders false
     */
    public boolean isAvailable(AResourceType resourceType, TimeSpan timeSpan){
        return isAvailable(resourceType, timeSpan, 1);
    }

    /**
     * Controlleer of de gegeven ResourceInstance beschikbaar is gedurende de gegeven TimeSpan.
     *
     * @param resourceInstance De ResourceInstance die beschikbaar moet zijn.
     * @param timeSpan Wanneer de ResourceInstance beschikbaar moet zijn.
     * @return true als de ResourceInstance beschikbaar is. false indien dit niet zo is.
     */
    public boolean isAvailable(ResourceInstance resourceInstance, TimeSpan timeSpan){
        // Haal alle plannen op die beginnen voor de eindtijd van de gegeven timeSpan.
        NavigableMap<LocalDateTime, ArrayList<Plan>> map = planMap.headMap(timeSpan.getEndTime(), true);

        for(ArrayList<Plan> planList: map.values()){
            for(Plan plan: planList){
                if (checkResourceInstanceOverlapsWithOtherPlan(resourceInstance, timeSpan, plan)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Controlleer of een ResourceInstance in een TimeSpan voorkomt in een gegeven Plan. Dit gebeurt door alle reservaties
     * van een plan op te halen wanneer de TimeSpan van het plan overlapt met de gegeven TimeSpan.
     *
     * @param resourceInstance De ResourceInstance die nog niet in het plan mag zitten.
     * @param timeSpan Wanneer de ResourceInstance niet in het plan mag zitten.
     * @param plan Het plan waar de ResourceInstance niet in mag zitten.
     * @return true als de ResourceInstance er in voorkomt, anders false.
     */
    private boolean checkResourceInstanceOverlapsWithOtherPlan(ResourceInstance resourceInstance, TimeSpan timeSpan, Plan plan) {
        // Controleer eerst of het plan wel overlapt met de timeSpan. Alleen dan zijn verdere berekeningen nuttig.
        if(plan.getTimeSpan().overlapsWith(timeSpan)) {
            for (ResourceReservation reservation : plan.getReservations(resourceInstance.getResourceType())) {
                if(reservation.getResourceInstance().equals(resourceInstance)){
                    return true;
                }
            }
        }
        return false;
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

    private ResourceRepository resourceRepository;
}