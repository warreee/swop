package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.util.Util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Klasse die gebruikt wordt om resources te kunnen plannen. Deze klasse houdt ook een lijst bij van alle plannen die
 * al gemaakt zijn.
 */
public class ResourcePlanner {

    private TreeMap<LocalDateTime, ArrayList<OldPlan>> planMap;

    /**
     * Maakt een nieuwe ResourcePlanner object aan. Dit ResourcePlanner object gebruikt de gegeven ResourceRepository om
     * alle resources in te plannen.
     *
     * @param resourceRepository De ResourceRepository waaruit alle info over de resources wordt gehaald.
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
        NavigableMap<LocalDateTime, ArrayList<OldPlan>> map = planMap.headMap(timeSpan.getEndTime(), true);

        for(ArrayList<OldPlan> planList: map.values()){
            for(OldPlan plan: planList){
                if (checkResourceInstanceOverlapsWithOtherPlan(resourceInstance, timeSpan, plan)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Bepaald de n volgende mogelijke starttijden (alleen volledige uren vb 9:00 en 10:00) voor een gegeven
     * requirementList.
     * @param requirementList De IRequirementList die voldaan moet zijn.
     * @param firstPossibleStartTime De eerste mogelijke starttijd vanaf wanneer de starttijden kunnen beginnen.
     * @param duration Hoelang alle elementen in de IRequirementList beschikbaar moeten zijn.
     * @param amount Hoeveel mogelijke starttijden er moeten berekend worden.
     * @return Een lijst met de gevraagde hoeveelheid mogelijke starttijden.
     */
    public List<LocalDateTime> getNextStartTimes(IRequirementList requirementList, LocalDateTime firstPossibleStartTime, Duration duration, int amount){
        LocalDateTime fullHour = Util.getNextHour(firstPossibleStartTime);
        LocalDateTime furthest;
        ArrayList<LocalDateTime> possibleStartTimes = new ArrayList<>();

        while(possibleStartTimes.size() < amount) {
            furthest = getFurthestTime(duration, fullHour, requirementList);
            if(resourceRequirementsSatisfiable(new TimeSpan(fullHour, furthest), requirementList)){
                possibleStartTimes.add(fullHour);
            }
            fullHour = fullHour.plusHours(1);
        }
        return possibleStartTimes;
    }

    /**
     * Bepaald of voor een gegeven IRequirementList en TimeSpan alles beschikbaar is gedurende die TimeSpan.
     * @param timeSpan De TimeSpan wanneer alles beschikbaar moet zijn.
     * @param requirementList De IRequirementList die beschikbaar moet zijn.
     * @return true als alles beschikbaar is, anders false.
     */
    private boolean resourceRequirementsSatisfiable(TimeSpan timeSpan, IRequirementList requirementList) {
        Iterator<ResourceRequirement> it = requirementList.iterator();
        while (it.hasNext()){
            ResourceRequirement req = it.next();
            if(!isAvailable(req.getType(), timeSpan, req.getAmount())){
                return false;
            }
        }
        return true;
    }

    /**
     * Bepaald voor een IRequirementList de verste eindtijd vanaf een starttijd wanneer de IRequirementList een Duration
     * beschikbaar moet zijn.
     * @param duration Hoelang alle dingen in IRequirementList beschikbaar moeten zijn.
     * @param startTime De starttijd vanaf wanneer de dingen in IRequirementList beschikbaar moeten zijn.
     * @param requirementList De IRequirementList waarvan de verste eindtijd berekend moet worden.
     * @return De verste eindtijd.
     */
    // TODO: information expert, hoort dit thuis in requirements list of waar?
    private LocalDateTime getFurthestTime(Duration duration, LocalDateTime startTime, IRequirementList requirementList) {
        LocalDateTime furthest = LocalDateTime.MIN;
        Iterator<ResourceRequirement> it = requirementList.iterator();
        while (it.hasNext()) {
            LocalDateTime end = it.next().getType().calculateEndTime(startTime, duration);
            if (end.isAfter(furthest)) {
                furthest = end;
            }
        }
        return furthest;
    }

    /**
     * Controlleer of een ResourceInstance in een TimeSpan voorkomt in een gegeven OldPlan. Dit gebeurt door alle reservaties
     * van een plan op te halen wanneer de TimeSpan van het plan overlapt met de gegeven TimeSpan.
     *
     * @param resourceInstance De ResourceInstance die nog niet in het plan mag zitten.
     * @param timeSpan Wanneer de ResourceInstance niet in het plan mag zitten.
     * @param plan Het plan waar de ResourceInstance niet in mag zitten.
     * @return true als de ResourceInstance er in voorkomt, anders false.
     */
    private boolean checkResourceInstanceOverlapsWithOtherPlan(ResourceInstance resourceInstance, TimeSpan timeSpan, OldPlan plan) {
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