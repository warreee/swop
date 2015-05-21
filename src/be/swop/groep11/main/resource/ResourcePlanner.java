package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.util.Observable;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasse die gebruikt wordt om resources te kunnen plannen. Deze klasse houdt ook een lijst bij van alle plannen die
 * al gemaakt zijn.
 */
public class ResourcePlanner extends Observable<ResourcePlanner>{

    //Sleutel = StartTijd van Plan, waarde is lijst van plannen met zelfde StartTijd
    private TreeMap<LocalDateTime, ArrayList<Plan>> planMap;

    /**
     * Maakt een nieuwe ResourcePlanner object aan. Dit ResourcePlanner object gebruikt de gegeven ResourceRepository om
     * alle resources in te plannen.
     *
     * @param resourceRepository De ResourceRepository waaruit alle info over de resources wordt gehaald.
     * @param systemTime
     */
    public ResourcePlanner(ResourceRepository resourceRepository, SystemTime systemTime) throws IllegalArgumentException {
        setSystemTime(systemTime);
        setResourceRepository(resourceRepository);
        planMap = new TreeMap<>();
    }

    private boolean canHaveAsSystemTime(SystemTime systemTime) {
        return systemTime != null && getSystemTime() == null;
    }

    private SystemTime getSystemTime() {
        return systemTime;
    }

    private void setSystemTime(SystemTime systemTime) throws IllegalArgumentException {
        if (!canHaveAsSystemTime(systemTime)) {
            throw new IllegalArgumentException("Ongeldige systemTime");
        }
        this.systemTime = systemTime;
    }

    private SystemTime systemTime;


    /**
     * Bepaalt of deze ResourcePlanner in staat is om de gegeven taak in de toekomst mogelijks te plannen is.
     *
     * @param task De taak die gepland moet worden.
     * @return true als het mogelijk is, anders false.
     */
    public boolean hasEnoughResourcesToPlan(Task task){
        Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
        while (it.hasNext()){
            ResourceRequirement req = it.next();
            if(req.getAmount() > resourceRepository.amountOfResourceInstances(req.getType())){
                return false;
            }
        }
        // Voor elke requirement van taak zijn er voldoende resources beschikbaar in deze planner.
        return true;
    }

    /**
     * Controleer of er het gegeven aantal ResourceTypes beschikbaar is gedurende de TimeSpan.
     *
     * @param resourceType De ResourceType die beschikbaar moet zijn.
     * @param timeSpan     De TimeSpan wanneer de ResourceType beschikbaar moet zijn.
     * @param amount       De hoeveelheid ResourceTypes die beschikbaar moet zijn.
     * @return true als er voldoende ResourceTypes beschikbaar zijn, anders false.
     */
    public boolean isAvailable(AResourceType resourceType, TimeSpan timeSpan, int amount) {
        int count = 0;
        for (ResourceInstance resourceInstance : getResourceRepository().getResources(resourceType)) {
            if (isAvailable(resourceInstance, timeSpan)) {
                count++;
            }
            if (count == amount) {
                return true;
            }
        }
        return false;
    }

    /**
     * Controlleer of er een ResourceInstance beschikbaar is voor het ResourceType gedurende de gegeven TimeSpan.
     *
     * @param resourceType De ResourceType die beschikbaar moet zijn.
     * @param timeSpan     De TimeSpan wanneer de ResourceType beschikbaar moet zijn.
     * @return true als er 1 beschikbaar, anders false
     */
    public boolean isAvailable(AResourceType resourceType, TimeSpan timeSpan) {
        return isAvailable(resourceType, timeSpan, 1);
    }

    /**
     * Controleer of de gegeven ResourceInstance beschikbaar is gedurende de gegeven TimeSpan.
     *
     * @param resourceInstance De ResourceInstance die beschikbaar moet zijn.
     * @param timeSpan         Wanneer de ResourceInstance beschikbaar moet zijn.
     * @return true als de ResourceInstance beschikbaar is. false indien dit niet zo is.
     */
    public boolean isAvailable(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        // Haal alle plannen op die beginnen voor de eindtijd van de gegeven timeSpan.
        NavigableMap<LocalDateTime, ArrayList<Plan>> map = planMap.headMap(timeSpan.getEndTime(), true);

        for (ArrayList<Plan> planList : map.values()) {
            for (Plan plan : planList) {
                if (checkResourceInstanceOverlapsWithOtherPlan(resourceInstance, timeSpan, plan)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Berekent alle taken die conflicteren met een lijst van ResourceInstances gedurende een TimeSpan.
     * Berekend alle taken die conflicteren met een lijst van ResourceInstances gedurende een TimeSpan.
     *
     * @param resourceInstances De lijst met ResourceInstances waarvan alle conflicterende taken gezocht moet worden.
     * @param timeSpan          Wanneer de ResourceInstances moeten conflicteren om in de lijst terecht te komen.
     * @return Een lijst met alle conflicterende taken.
     */
    public List<Task> getConflictingTasks(List<ResourceInstance> resourceInstances, TimeSpan timeSpan) {
        NavigableMap<LocalDateTime, ArrayList<Plan>> map = planMap.headMap(timeSpan.getEndTime(), true);
        Set<Task> conflictingTasks = new HashSet<>();

        for (ArrayList<Plan> planList : map.values()) {
            for (Plan plan : planList) {
                for (ResourceInstance instance : resourceInstances) {
                    if (checkResourceInstanceOverlapsWithOtherPlan(instance, timeSpan, plan)) {
                        conflictingTasks.add(plan.getTask());
                    }
                }
            }
        }

        return new ArrayList<>(conflictingTasks);
    }


    /**
     * Voegt een nieuw plan toe aan deze ResourcePlanner.
     * @param plan Het plan dat toegevoegd wordt.
     */
    public void addPlan(Plan plan) throws IllegalArgumentException {
        checkPlan(plan);

        if (planMap.containsKey(plan.getPlannedStartTime())) {
            planMap.get(plan.getTimeSpan().getStartTime()).add(plan);
        } else {
            ArrayList<Plan> list = new ArrayList<>();
            list.add(plan);
            planMap.put(plan.getPlannedStartTime(), list);
        }

        //
        addObserver(plan.getTask().getResourcePlannerObserver());
    }

    /**
     * Verwijdert een plan uit deze ResourcePlanner.
     * @param plan Het plan dat verwijderd wordt.
     */
    public void removePlan(Plan plan){
        checkPlan(plan);
        if(planMap.containsKey(plan.getPlannedStartTime())){
            planMap.get(plan.getPlannedStartTime()).remove(plan);
            if (planMap.get(plan.getPlannedStartTime()).isEmpty()) {
                planMap.remove(plan.getPlannedStartTime());
            }
        }

        removeObserver(plan.getTask().getResourcePlannerObserver());
    }

    /**
     * Controleert of een plan niet null is en of dat het geen reservaties bevat voor ResourceInstances die op het
     * Controleert of een plan niet null is en of dat het geen reservaties bevat voor ResourceInstances die op het
     * moment van die reservatie al gereserveerd zijn.
     *
     * @param plan Het plan dat gecontroleert moet worden.
     * @throws IllegalArgumentException Wordt gegooid wanneer er een fout is.
     */
    // TODO: controleert deze alles genoeg?
    private void checkPlan(Plan plan) throws IllegalArgumentException {
        if (plan == null) {
            throw new IllegalArgumentException("Plan mag niet 'null' zijn.");
        }
        for (ResourceReservation reservation : plan.getReservations()) {
            if (!isAvailable(reservation.getResourceInstance(), reservation.getTimeSpan())) {
                throw new IllegalArgumentException("Plan bevat een reservatie voor een ResourceInstance die al " +
                        "gereserveerd is gedurende de tijdsduur van die reservatie.");
            }
        }
    }

    /**
     * Bepaald of voor een gegeven IRequirementList en TimeSpan alles beschikbaar is gedurende die TimeSpan.
     *
     * @param timeSpan        De TimeSpan wanneer alles beschikbaar moet zijn.
     * @param requirementList De IRequirementList die beschikbaar moet zijn.
     * @return true als alles beschikbaar is, anders false.
     */
    private boolean hasAvailableRequiredResources(TimeSpan timeSpan, IRequirementList requirementList) {
        Iterator<ResourceRequirement> it = requirementList.iterator();
        while (it.hasNext()) {
            ResourceRequirement req = it.next();
            if (!isAvailable(req.getType(), timeSpan, req.getAmount())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Bepaald de n volgende mogelijke TimeSpans (die starten op volledige uren vb 9:00 en 10:00) voor een gegeven
     * requirementList.
     *
     * @param requirementList        De IRequirementList die voldaan moet zijn.
     * @param firstPossibleStartTime De eerste mogelijke starttijd vanaf wanneer de TimeSpans kunnen beginnen.
     * @param duration               Hoelang alle elementen in de IRequirementList beschikbaar moeten zijn.
     * @param amount                 Hoeveel mogelijke TimeSpans er moeten berekend worden.
     * @return Een lijst met de gevraagde hoeveelheid mogelijke TimeSpans.
     */
    public List<TimeSpan> getNextPossibleTimeSpans(IRequirementList requirementList, LocalDateTime firstPossibleStartTime, Duration duration, int amount) {
        LocalDateTime fullHour = getNextHour(firstPossibleStartTime);

        ArrayList<TimeSpan> possibleTimeSpans = new ArrayList<>();
        int listSize = possibleTimeSpans.size();
        while ( listSize < amount) {

            TimeSpan timeSpan = requirementList.calculateReservationTimeSpan(fullHour, duration);
            if (hasAvailableRequiredResources(timeSpan, requirementList)) {
                possibleTimeSpans.add(timeSpan);
                listSize = possibleTimeSpans.size();
            }
            fullHour = fullHour.plusHours(1);

        }
        return possibleTimeSpans;
    }


    /**
     * Bepaald de n volgende mogelijke starttijden (die starten op volledige uren vb 9:00 en 10:00) voor een gegeven
     * requirementList.
     *
     * @param requirementList        De IRequirementList die voldaan moet zijn.
     * @param firstPossibleStartTime De eerste mogelijke starttijd vanaf wanneer de starttijden kunnen beginnen.
     * @param duration               Hoelang alle elementen in de IRequirementList beschikbaar moeten zijn.
     * @param amount                 Hoeveel mogelijke starttijden er moeten berekend worden.
     * @return Een lijst met de gevraagde hoeveelheid mogelijke starttijden.
     */
    public List<LocalDateTime> getNextPossibleStartTimes(IRequirementList requirementList, LocalDateTime firstPossibleStartTime, Duration duration, int amount) {
        return getNextPossibleTimeSpans(requirementList, firstPossibleStartTime, duration, amount).stream().map(TimeSpan::getStartTime).collect(Collectors.toList());
    }

    /**
     * Bepaald de n volgende mogelijke starttijden (die starten op volledige uren vb 9:00 en 10:00) voor een gegeven
     * requirementList.
     *
     * @param requirementList        De IRequirementList die voldaan moet zijn.
     * @param duration               Hoelang alle elementen in de IRequirementList beschikbaar moeten zijn.
     * @param amount                 Hoeveel mogelijke starttijden er moeten berekend worden.
     * @return Een lijst met de gevraagde hoeveelheid mogelijke starttijden.
     */
    public List<LocalDateTime> getNextPossibleStartTimes(IRequirementList requirementList, Duration duration, int amount) {
        return getNextPossibleStartTimes(requirementList, getSystemTime().getCurrentSystemTime(), duration, amount);
    }

    /**
     * Geeft alle ResourceInstances die beschikbaar zijn van een bepaald type gedurende een TimeSpan.
     *
     * @param type     Het AResourceType dat beschikbaar moet zijn.
     * @param timeSpan Wanneer het AResourceType beschikbaar moet zijn.
     * @return Een lijst met alle ResourceInstances die beschikbaar zijn.
     */
    public List<ResourceInstance> getAvailableInstances(AResourceType type, TimeSpan timeSpan) {
        return resourceRepository.getResources(type).stream().filter(x -> isAvailable(x, timeSpan)).collect(Collectors.toList());
    }

    /**
     * Geeft alle ResourceInstances van een bepaald type.
     * In deze lijst staan alle ResourceInstances die beschikbaar zijn gedurende een TimeSpan vooraan.
     *
     * @param type     Het AResourceType.
     * @param timeSpan Wanneer het AResourceType beschikbaar moet zijn.
     * @return Een lijst met alle ResourceInstances van het type.
     */
    public List<ResourceInstance> getInstances(AResourceType type, TimeSpan timeSpan) {
        List<ResourceInstance> instances = resourceRepository.getResources(type);

        // sorteer de instances volgens beschikbaarheid (eerst beschikbaar, dan niet-beschikbaar)
        class ResourceInstanceComparator implements Comparator<ResourceInstance> {
            @Override
            public int compare(ResourceInstance instance1, ResourceInstance instance2) {
                if (isAvailable(instance1, timeSpan) == isAvailable(instance2, timeSpan)) {
                    return 0;
                } else if (isAvailable(instance1, timeSpan)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        Collections.sort(instances, new ResourceInstanceComparator());
        return instances;
    }

    /**
     * Controleer of een ResourceInstance in een TimeSpan voorkomt in een gegeven Plan. Dit gebeurt door alle reservaties
     * van een plan op te halen wanneer de TimeSpan van het plan overlapt met de gegeven TimeSpan.
     *
     * @param resourceInstance De ResourceInstance die nog niet in het plan mag zitten.
     * @param timeSpan         Wanneer de ResourceInstance niet in het plan mag zitten.
     * @param plan             Het plan waar de ResourceInstance niet in mag zitten.
     * @return true als de ResourceInstance er in voorkomt, anders false.
     */
    private boolean checkResourceInstanceOverlapsWithOtherPlan(ResourceInstance resourceInstance, TimeSpan timeSpan, Plan plan) {
        // Controleer eerst of het plan wel overlapt met de timeSpan. Alleen dan zijn verdere berekeningen nuttig.
        if (plan.getTimeSpan().overlapsWith(timeSpan)) {
            for (ResourceReservation reservation : plan.getReservations(resourceInstance.getResourceType())) {
                if (reservation.getResourceInstance().equals(resourceInstance)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setResourceRepository(ResourceRepository resourceRepository) {
        checkResourceRepository(resourceRepository);
        this.resourceRepository = resourceRepository;
    }

    private void checkResourceRepository(ResourceRepository resourceRepository) {
        if (resourceRepository == null) {
            throw new IllegalArgumentException("ResourceRepository mag niet 'null' zijn.");
        }
    }

    public ResourceRepository getResourceRepository() {
        //TODO TEMP? getter, nodig in branch office atm. zoek betere oplossing
        return resourceRepository;
    }

    /**
     * Deze methode gaat na of er voor het gegeven plan een equivalent plan bestaat startend op het gegeven Tijdstip.
     * Waarbij rekening gehouden wordt met reeds specifieke resources.
     * @param localDateTime Het gegeven tijdStip
     * @param plan          Het gegeven plan
     * @return  Waar indien er een ander plan gemaakt kan worden met de specifieke resources op het gegeven tijdStip
     *          Waar indien localDateTime in de periode van het gegeven plan valt.
     */
    public boolean hasEquivalentPlan(Plan plan,LocalDateTime localDateTime) {
        boolean result;
        if (!plan.isWithinPlanTimeSpan(localDateTime)) {
            ImmutableList<ResourceReservation> specificReservations = plan.getSpecificReservations();

            PlanBuilder planBuilder = new PlanBuilder(plan.getTask().getDelegatedTo(), plan.getTask(), localDateTime);
            //add specific instances
            specificReservations.forEach(reservation -> planBuilder.addResourceInstance(reservation.getResourceInstance()));
            //propose rest
            planBuilder.proposeResources();
            result = planBuilder.isSatisfied() && (!planBuilder.hasConflictingReservations());
        }else{
            result = true;
        }
        return result;
    }

    public boolean hasEquivalentPlan(Plan plan) {
        return hasEquivalentPlan(plan, getSystemTime().getCurrentSystemTime());
    }

    private ResourceRepository resourceRepository;

    /**
     * Geeft een ResourcePlannerMemento object die de status van deze project repository bevat.
     */
    public IResourcePlannerMemento createMemento() {
        ResourcePlannerMemento memento = new ResourcePlannerMemento();
        memento.setPlans(this.getPlans());
        return memento;
    }

    /**
     * Wijzigt de status van deze resource planner naar de status van een gegeven ResourcePlannerMemento object.
     * @param memento Het ResourcePlannerMemento object met de status
     */
    public void setMemento(IResourcePlannerMemento memento) {
        this.planMap = new TreeMap<>();
        List<Plan> plans = memento.getPlans();
        for (Plan plan : plans) {
            this.addPlan(plan);
        }
    }

    private List<Plan> getPlans() {
        List<Plan> plans = new ArrayList<>();
        for (LocalDateTime startTime : this.planMap.keySet()) {
            if (planMap.get(startTime) != null) {
                plans.addAll(planMap.get(startTime));
            }
        }
        return plans;
    }

    private LocalDateTime getNextHour(LocalDateTime dateTime){
        if (dateTime.getMinute() == 0)
            return dateTime.truncatedTo(ChronoUnit.SECONDS);
        else{
            return dateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        }
    }

}