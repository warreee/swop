package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import org.mockito.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Een resource manager houdt alle resource types in het systeem bij en de reservaties voor de resource instanties
 * van de resource types. Een resource manager kan resource instanties reserveren en vrijgeven.
 */
public class ResourceManager {

    /**
     * Constructor om een nieuwe resource manager aan te maken.
     */
    public ResourceManager() {
        //TODO garantie developers als resourceType niet in de constructor van ResourceManager?
        //Zeker zijn dat developers beschikbaar zijn als type
        addDeveloperType();
    }

    /**
     * Voegt een "Developer" type toe aan deze repository. Doet niets als het "Developer" type al bestaat of er een
     * probleem is met de naam.
     */
    private void addDeveloperType(){
        if(!containsType("Developer")) {
            addNewResourceType("Developer", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(17, 0)));
        }
    }

    //TODO documentatie
    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
     *
     */
    public void addNewResourceType(String name, DailyAvailability availability, List<IResourceType> requireTypes, List<IResourceType> conflictingTypes) {
        if(containsType(name)){
            throw new IllegalArgumentException("Er bestaat reeds een ResourceType met de naam " +name);
        }
        ResourceTypeBuilder newTypeBuilder = new ResourceTypeBuilder(name);
        typeBuilders.put(newTypeBuilder.getResourceType(),newTypeBuilder);
        newTypeBuilder.withDailyAvailability(availability);
        //Add require constraints
        // TODO: vraag Robin: Waarom deze check juist?
        if(!requireTypes.isEmpty()){
            for (IResourceType reqType : requireTypes) {
                newTypeBuilder.withRequirementConstraint(reqType);
            }
        }
        //Add conflicting constraints
        if(!conflictingTypes.isEmpty()){
            for (IResourceType conflictType : conflictingTypes) {
                newTypeBuilder.withConflictConstraint(conflictType);
            }
        }
        resourceTypes.add(newTypeBuilder.getResourceType());
    }

    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
     * @param availability
     */
    public void addNewResourceType(String name, DailyAvailability availability) {
        addNewResourceType(name, availability, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Voegt een nieuwe ResourceType met een DailyAvailability voor een ganse dag.
     * @param name De naam van de toe te voegen ResourceType
     */
    public void addNewResourceType(String name) {
        addNewResourceType(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Voegt een nieuwe ResourceType met requiredTypes en conflictingTypes toe. De DailyAvailability is voor een ganse
     * dag.
     * @param name De naam van de toe te voegen ResourceType
     * @param requireTypes De ResourceTypes(lijst) waarvan het toe te voegen ResourceType afhangt.
     * @param conflictingTypes De ResourceTypes(lijst) waarmee het toe te voegen ResourceType conflicteerd.
     */
    public void addNewResourceType(String name,  List<IResourceType> requireTypes, List<IResourceType> conflictingTypes){
        addNewResourceType(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX), requireTypes, conflictingTypes);
    }

    /**
     *
     * @param name De naam van het op te vragen ResourceType
     * @throws NoSuchElementException   wordt gegooid als de naam niet in deze repository zit.
     * @return
     */
    public IResourceType getResourceTypeByName(String name)throws NoSuchElementException{
        for(IResourceType type : typeBuilders.keySet()){
            if(type.getName().equals(name)){
                return type;
            }
        }
        throw new NoSuchElementException("Resource type met de gegeven naam kon niet gevonden worden.");
    }

    public boolean containsType(String typeName){
        //TODO betere manier zoeken voor containsType(String typeName)?
        try {
            getResourceTypeByName(typeName);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Een lijst die alle bekende resourceType van deze repository bevat.
     */
    private ArrayList<IResourceType> resourceTypes = new ArrayList<>();

    public ImmutableList<IResourceType> getResourceTypes() {
        return ImmutableList.copyOf(resourceTypes);
    }

    //TODO eventueel naar map<String,Map<IResourceType,ResourceTypeBuilder>>
    private HashMap<IResourceType, ResourceTypeBuilder> typeBuilders = new HashMap<>();


    /**
     * Zet voor het gegeven IResourceType de DailyAvailability op de gegeven DailyAvailability.
     * @param ownerType     Het IResourceType waarvan de DailyAvailability zal veranderen.
     * @param availability  De nieuwe DailyAvailability voor ownerType.
     * @throws IllegalArgumentException
     *                      Gooi indien availability null is
     */
    public void withDailyAvailability(IResourceType ownerType, DailyAvailability availability) throws IllegalArgumentException{
        typeBuilders.get(ownerType).withDailyAvailability(availability);
    }

    /**
     *
     * @param ownerType
     * @param reqType
     */
    public void withRequirementConstraint(IResourceType ownerType, IResourceType reqType) {
        typeBuilders.get(ownerType).withRequirementConstraint(reqType);
    }

    /**
     *
     * @param ownerType
     * @param conflictType
     */
    public void withConflictConstraint(IResourceType ownerType, IResourceType conflictType) {
        typeBuilders.get(ownerType).withConflictConstraint(conflictType);
    }

    /**
     *
     * @param ownerType
     * @param inst
     */
    public void addResourceInstance(IResourceType ownerType, String inst) {
        typeBuilders.get(ownerType).addResourceInstance(inst);

    }

    //////////////////////////////////////// RESOURCE RESERVATIONS /////////////////////////////////////////////////////

    /**
     * Maakt een reservatie voor een resource instantie gedurende een bepaalde tijdsspanne.
     * @param task             De taak waarvoor de reservatie moet gemaakt worden.
     * @param resourceInstance De te reserveren resource instantie
     * @param timeSpan         De gegeven tijdsspanne
     * @param isSpecific       True als de resource instantie specifiek gekozen is
     * @throws IllegalArgumentException De resource instantie is niet beschikbaar in de gegeven tijdsspanne,
     *                                  of de gegeven taak is null
     */
    public void makeReservation(Task task, ResourceInstance resourceInstance, TimeSpan timeSpan, boolean isSpecific) {
        if (task == null)
            throw new IllegalArgumentException("Taak mag niet null zijn");
        if (! isAvailable(resourceInstance, timeSpan))
            throw new IllegalArgumentException("De resource instantie is niet beschikbaar in de gegeven tijdsspanne");

        this.addReservation(task, new ResourceReservation(task, resourceInstance, timeSpan, isSpecific));
    }

    /**
     * Controleert of een resource instantie beschikbaar is gedurende een gegeven tijdsspanne.
     * @param resourceInstance De te controleren resource instantie
     * @param timeSpan         De gegeven tijdsspanne
     * @return True als de resource instantie beschikbaar is.
     */
    public boolean isAvailable(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        return this.getConflictingReservations(resourceInstance, timeSpan).isEmpty();
    }

    /**
     * Geeft de eerst volgende tijdsspanne waarin een resource instantie voor een gegeven duur beschikbaar is,
     * na een gegeven starttijd. De starttijd van de tijdsspanne is steeds op een uur (zonder minuten).
     * Hierbij wordt rekening gehouden dat de resource instantie niet noodzakelijk 24/7 beschikbaar is.
     * en er al reservaties kunnen zijn.
     * @param resourceInstance De resource instantie
     * @param startTime        De gegeven starttijd
     * @param duration         De gegeven duur
     */
    public TimeSpan getNextAvailableTimeSpan(ResourceInstance resourceInstance, LocalDateTime startTime, Duration duration) {
        IResourceType resourceType = resourceInstance.getResourceType();

        // bereken de "echte starttijd": hangt af van de dagelijkse beschikbaarheid en moet op een uur (zonder minuten) vallen
        LocalDateTime realStartTime;
        if (resourceType.getDailyAvailability() == null) {
            realStartTime = getNextHour(startTime);
        }
        else {
            // de "echte" starttijd is dan het eerste moment dat binnen de dagelijkse beschikbaarheid ligt
            realStartTime = resourceType.getDailyAvailability().getNextTime(getNextHour(startTime));
        }

        // bereken de eindtijd van een reservatie vanaf realStartTime voor duration: vraag dit aan de resourceInstance!
        LocalDateTime realEndTime = resourceInstance.calculateEndTime(realStartTime, duration);

        // maak van de start- en eindtijd een tijdsspanne
        TimeSpan timeSpan = new TimeSpan(realStartTime, realEndTime);

        // is de resource wel beschikbaar in die tijdsspanne?
        if (! isAvailable(resourceInstance, timeSpan)) {
            List<ResourceReservation> conflictingAllocations = getConflictingReservations(resourceInstance, timeSpan);
            // bereken hiermee de volgende mogelijke starttijd = de grootste van alle eindtijden van de resources
            LocalDateTime nextStartTime = realStartTime;
            for (ResourceReservation allocation : conflictingAllocations) {
                if (allocation.getTimeSpan().getEndTime().isAfter(nextStartTime)) {
                    nextStartTime = allocation.getTimeSpan().getEndTime();
                }
            }

            return getNextAvailableTimeSpan(resourceInstance, nextStartTime, duration);
        }

        return timeSpan;
    }

    /**
     * Geeft een lijst van alle resource instanties van een resource type, die beschikbaar zijn vanaf een gegeven starttijd
     * voor een gegeven duur. De lijst is gesorteerd volgens toenemende eindtijd van de eerstvolgende beschikbare tijdsspanne
     * van elke resource instantie.
     * @param resourceType Het gegeven resource type
     * @param startTime    De gegeven starttijd
     * @param duration     De gegeven duur
     */
    public List<ResourceInstance> getAvailableInstances(IResourceType resourceType, LocalDateTime startTime, Duration duration) {
        List<ResourceInstance> availableInstances = new ArrayList<>();

        // voeg alle resource instances toe die beschikbaar zijn vanaf startTime
        for (ResourceInstance instance : resourceType.getResourceInstances()) {
            if (this.getNextAvailableTimeSpan(instance, startTime, duration).getStartTime().equals(startTime)) {
                availableInstances.add(instance);
            }
        }

        // sorteer de instances volgens toenemende eindtijd van de eerstvolgende beschikbare tijdsspanne
        class ResourceInstanceComparator implements Comparator<ResourceInstance> {
            @Override
            public int compare(ResourceInstance instance1, ResourceInstance instance2) {
                return getNextAvailableTimeSpan(instance1, startTime, duration).getEndTime()
                        .compareTo(getNextAvailableTimeSpan(instance2, startTime, duration).getEndTime());
            }
        }
        Collections.sort(availableInstances, new ResourceInstanceComparator());

        return availableInstances;
    }

    /**
     * Geeft een lijst van conflicterende reservaties voor een resource instantie in een bepaalde tijdsspanne.
     * @param resourceInstance De gegeven resource instantie
     * @param timeSpan         De gegeven tijdsspanne
     * @return Lijst van reservaties voor de resource instantie waarvan de tijdsspanne overlapt met de gegeven tijdsspanne
     */
    private List<ResourceReservation> getConflictingReservations(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        List<ResourceReservation> conflictingReservations = new ArrayList<>();
        ImmutableList<ResourceReservation> reservations = this.getReservations(resourceInstance);
        for (ResourceReservation reservation : reservations) {
            if (timeSpan.overlapsWith(reservation.getTimeSpan())) {
                conflictingReservations.add(reservation);
            }
        }
        return conflictingReservations;
    }

    private LocalDateTime getNextHour(LocalDateTime dateTime) {
        if (dateTime.getMinute() == 0)
            return dateTime;
        else
            return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour()+1,0));
    }

    // TODO: reservaties verwijderen

    // TODO: reservaties vroeger laten eindigien

    /**
     * Geeft een immutable list van alle reservaties.
     */
    public ImmutableList<ResourceReservation> getReservations() {
        List<ResourceReservation> allReservations = new LinkedList<>();
        for (List<ResourceReservation> taskReservations : this.reservations.values()) {
            allReservations.addAll(taskReservations);
        }
        return ImmutableList.copyOf(allReservations);
    }

    /**
     * Geeft een immutable list van alle reservaties van een taak.
     * @param task De gegeven taak
     */
    public ImmutableList<ResourceReservation> getReservations(Task task) {
        if (reservations.containsKey(task)) {
            return ImmutableList.copyOf(new LinkedList<ResourceReservation>());
        }
        else {
            return ImmutableList.copyOf(reservations.get(task));
        }
    }

    /**
     * Geeft een immutable list van alle reservaties van een resource instantie.
     * @param resourceInstance De resource instantie
     */
    public ImmutableList<ResourceReservation> getReservations(ResourceInstance resourceInstance) {
        ImmutableList<ResourceReservation> allReservations = this.getReservations();
        List<ResourceReservation> resourceReservations = new LinkedList<>();
        for (ResourceReservation reservation : allReservations) {
            if (reservation.getResourceInstance() == resourceInstance) {
                resourceReservations.add(reservation);
            }
        }
        return ImmutableList.copyOf(resourceReservations);
    }

    /**
     * Hashmap die voor elke taak een lijst reservaties bijhoudt
     */
    private Map<Task,List<ResourceReservation>> reservations = new HashMap<>();

    private void addReservation(Task task, ResourceReservation reservation) {
        if (reservations.containsKey(task)) {
            List<ResourceReservation> taskReservations = reservations.get(task);
            taskReservations.add(reservation);
        }
        else {
            List<ResourceReservation> taskReservations = new LinkedList<>();
            taskReservations.add(reservation);
            reservations.put(task,taskReservations);
        }
    }

    /**
     * Geeft een immutable list van de resource instanties van een gegeven resource type
     * die beschikbaar zijn in een bepaalde tijdsspanne.
     * @param resourceType Het gegeven resource type
     * @param timeSpan     De gegeven tijdsspanne
     */
    public ImmutableList<ResourceInstance> getAvailableInstances(IResourceType resourceType, TimeSpan timeSpan) {
        ImmutableList<ResourceInstance> instances = resourceType.getResourceInstances();
        List<ResourceInstance> availableInstances = new LinkedList<>();
        for (ResourceInstance resourceInstance : instances) {
            if (this.isAvailable(resourceInstance, timeSpan)) {
                availableInstances.add(resourceInstance);
            }
        }
        return ImmutableList.copyOf(availableInstances);
    }

    /**
     * Geeft een lijst van de eerste n mogelijke plannen voor een taak na een gegeven tijdstip.
     * @param n        Het aantal mogelijke plannen
     * @param task     De taak waarvoor mogelijke plannen moeten gemaakt worden
     * @param dateTime Het gegeven tijdstip waarna de plannen moeten starten
     * @return Een lijst van lengte n van de eerstvolgende mogelijke plannen
     */
    public List<Plan> getNextPlans(int n, Task task, LocalDateTime dateTime) {
        // TODO: dit is nog niet efficiënt genoeg!

        List<Plan> plans = new LinkedList<>();

        LocalDateTime startTime = getNextHour(dateTime);
        while (plans.size() < n) {
            Plan nextPlan = new Plan(task, startTime);
            if (nextPlan.canMakeDefaultReservations()) {
                plans.add(nextPlan);
            }

            startTime = startTime.plusHours(1);
        }

        return plans;
    }

    private class Plan {

        /**
         * Constructor om een nieuw plan aan te maken met default reservaties voor de resource requirements van de gegeven taak.
         *
         * @param task      De gegeven taak
         * @param startTime De starttijd van het plan: moet op een uur vallen (zonder minuten)
         */
        public Plan(Task task, LocalDateTime startTime) {
            if (task == null)
                throw new IllegalArgumentException("Taak mag niet null zijn");
            if (startTime == null)
                throw new IllegalArgumentException("Starttijd mag niet null zijn");
            if (startTime.getMinute() != 0)
                throw new IllegalArgumentException("Ongeldige starttijd: moet op een uur vallen (zonder minuten)");
            this.task = task;
            this.startTime = startTime;
        }

        /**
         * Controleert of dit plan geldig is.
         *
         * @return True als de reservaties niet conflicteren met andere reservaties.
         */
        public boolean isValidPlan() {
            for (ResourceReservation reservation : this.getReservations()) {
                if (!isAvailable(reservation.getResourceInstance(), reservation.getTimeSpan())) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Controleert of op de starttijd voor alle resource requirements de nodige resource instanties kunnen
         * gereserveerd worden voor de taak.
         */
        public boolean canMakeDefaultReservations() {
            try {
                List<ResourceReservation> defaultReservations = this.calculateDefaultReservations(this.getTask(), this.getStartTime());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        /**
         * Maakt voor elk resource type in de requirement list van de taak de nodige reservaties.
         */
        public void makeDefaultReservations() {
            this.reservations = calculateDefaultReservations(this.getTask(), this.getStartTime());
        }

        public Task getTask() {
            return task;
        }

        private final Task task;

        /**
         * Geeft de starttijd van dit plan.
         */
        public LocalDateTime getStartTime() {
            return startTime;
        }

        private final LocalDateTime startTime;

        /**
         * Geeft de eindtijd van dit plan.
         *
         * @return De laatste eindtijd van alle reservaties van het plan,
         * of starttijd + de estimated duration van de taak indien er geen reservaties zijn.
         */
        public LocalDateTime getEndTime() {
            if (this.getReservations().isEmpty()) {
                return this.getStartTime().plus(this.getTask().getEstimatedDuration());
            } else {
                LocalDateTime endTime = this.getStartTime().plus(this.getTask().getEstimatedDuration());
                for (ResourceReservation reservation : this.getReservations()) {
                    if (reservation.getTimeSpan().getEndTime().isAfter(endTime)) {
                        endTime = reservation.getTimeSpan().getEndTime();
                    }
                }
                return endTime;
            }
        }

        private List<ResourceReservation> reservations = new LinkedList<>();

        /**
         * Geeft de reservaties van dit plan.
         *
         * @return De reservaties van dit plan en die eindigen allemaal op hetzelfde moment.
         */
        public List<ResourceReservation> getReservations() {
            List<ResourceReservation> reservations = new LinkedList<>();
            // zorg wel dat alle reservaties op het zelfde moment eindigen!
            for (ResourceReservation reservation : this.reservations) {
                if (reservation.getTimeSpan().getEndTime().isBefore(this.getEndTime())) {
                    reservations.add(new ResourceReservation(reservation.getTask(),
                            reservation.getResourceInstance(),
                            new TimeSpan(reservation.getTimeSpan().getStartTime(), this.getEndTime()),
                            reservation.isSpecific()));
                }
            }
            return reservations;
        }

        /**
         * @throws IllegalArgumentException Er zijn niet genoeg resource instanties beschikbaar voor een plan van task op de gegeven starttijd
         */
        private List<ResourceReservation> calculateDefaultReservations(Task task, LocalDateTime startTime) throws IllegalArgumentException {
            List<ResourceReservation> defaultReservations = new LinkedList<>();

            LocalDateTime endTime = calculateEndTime(task, startTime);
            TimeSpan timeSpanOfPlan = new TimeSpan(startTime, endTime);

            Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
            while (it.hasNext()) {
                ResourceRequirement requirement = it.next();

                List<ResourceInstance> availableInstances = getAvailableInstances(requirement.getType(), timeSpanOfPlan);

                int nbRequiredInstances = requirement.getAmount();
                if (availableInstances.size() < nbRequiredInstances) {
                    throw new IllegalArgumentException("Er zijn niet genoeg resource instanties beschikbaar voor een plan van task op de gegeven starttijd");
                } else {
                    // voeg de nodige reservaties toe
                    for (int i = 0; i < nbRequiredInstances - 1; i++) {
                        defaultReservations.add(new ResourceReservation(task, availableInstances.get(i), timeSpanOfPlan, false));
                    }
                }
            }

            return defaultReservations;
        }

        private LocalDateTime calculateEndTime(Task task, LocalDateTime startTime) {
            LocalDateTime endTime = startTime.plus(task.getEstimatedDuration());
            Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
            while (it.hasNext()) {
                ResourceRequirement requirement = it.next();

                // lijst van beschikbare resources voor het type, gesorteerd volgens toenemend eindtijd van de eerstvolgende mogelijke tijdsspanne voor een reservatie
                List<ResourceInstance> availableInstances = getAvailableInstances(requirement.getType(), startTime, task.getEstimatedDuration());

                int nbRequiredInstances = requirement.getAmount();

                ResourceInstance instanceWithLongestReservation = availableInstances.get(Math.min(nbRequiredInstances, availableInstances.size() - 1));
                LocalDateTime endTimeOfLongestReservation = getNextAvailableTimeSpan(instanceWithLongestReservation, startTime, task.getEstimatedDuration()).getEndTime();

                if (endTimeOfLongestReservation.isAfter(endTime))
                    endTime = endTimeOfLongestReservation;
            }
            return endTime;
        }
    }

}
