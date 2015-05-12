package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Een resource manager houdt alle resource types in het systeem bij en de reservaties voor de resource instanties
 * van de resource types. Een resource manager kan resource instanties reserveren en vrijgeven.
 */
public class ResourceManager{

    // TODO: methodes voor resource types hier uit halen + andere klassen aanpassen (controllers en Plan)

    /**
     * Constructor om een nieuwe resource manager aan te maken.
     */
    public ResourceManager() {

    }

    /**
     * Geeft het Type terug dat Developers voorsteld.
     * @return Het DeveloperType.
     */
    public AResourceType getDeveloperType() {
        if (this.developerType == null) {
            developerType =  new DeveloperType();
            resourceTypes.add(developerType);
        }
        return developerType;
    }

    private DeveloperType developerType;

    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
     * @param availability De DailyAvailability van het toe te voegen ResourceType.
     * @param requireTypes De ResourceTypes(lijst) waarvan het toe te voegen ResourceType afhangt.
     * @param conflictingTypes De ResourceTypes(lijst) waarmee het toe te voegen ResourceType conflicteerd.
     *
     */
    public void addNewResourceType(String name, DailyAvailability availability, List<AResourceType> requireTypes, List<AResourceType> conflictingTypes) {
        if(containsType(name)){
            throw new IllegalArgumentException("Er bestaat reeds een ResourceType met de naam " +name);
        }
        ResourceType type = new ResourceType(name);
        type.setDailyAvailability(availability);

        //Add require constraints
        for (AResourceType reqType : requireTypes) {
            type.addRequirementConstraint(reqType);
        }

        //Add conflicting constraints
        for (AResourceType conflictType : conflictingTypes) {
            type.addConflictConstraint(conflictType);
        }

        resourceTypes.add(type);
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
    public void addNewResourceType(String name,  List<AResourceType> requireTypes, List<AResourceType> conflictingTypes){
        addNewResourceType(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX), requireTypes, conflictingTypes);
    }

    /**
     *
     * @param name De naam van het op te vragen ResourceType
     * @throws NoSuchElementException   wordt gegooid als de naam niet in deze repository zit.
     * @return Het gevonden AResourceType.
     */
    public AResourceType getResourceTypeByName(String name)throws NoSuchElementException{
        for(AResourceType type : resourceTypes){
            if(type.getName().equals(name)){
                return type;
            }
        }
        throw new NoSuchElementException("Resource type met de gegeven naam kon niet gevonden worden.");
    }

    /**
     * Controleert of de gegeven typeName bekend is bij deze ResourceManager.
     * @param typeName De naam van het type.
     * @return True indien dit zo is, anders False.
     */
    public boolean containsType(String typeName){
        for(AResourceType type : resourceTypes){
            if(type.getName().equals(typeName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Een lijst die alle bekende resourceType van deze repository bevat.
     */
    private ArrayList<AResourceType> resourceTypes = new ArrayList<>();

    public ImmutableList<AResourceType> getResourceTypes() {
        return ImmutableList.copyOf(resourceTypes);
    }

    /**
     * Zet voor het ownerType een RequirementsConstraint met het reqType.
     * @param ownerType Het superType dat niet kan bestaan zonder het kindtype.
     * @param reqType Het kindtype.
     */
    public void withRequirementConstraint(AResourceType ownerType, AResourceType reqType) {
        ownerType.addRequirementConstraint(reqType);
    }

    /**
     * zet voor het ownerType een ConflictsConstraint met het conflictType.
     * @param ownerType Het superType dat conflicteerd met het kindType.
     * @param conflictType Het kindType.
     */
    public void withConflictConstraint(AResourceType ownerType, AResourceType conflictType) {
        ownerType.addConflictConstraint(conflictType);
    }

    /**
     * Voegt een ResourceInstance toe voor het gegeven AResourceType met de gegeven naam.
     * @param ownerType Het type waarvoor we een instantie toevoegen.
     * @param inst De naam van de toe te voegen instance.
     */
    public void addResourceInstance(AResourceType ownerType, String inst) {
        ownerType.addResourceInstance(inst);
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
        AResourceType resourceType = resourceInstance.getResourceType();

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
        LocalDateTime realEndTime = resourceType.calculateEndTime(realStartTime, duration);

        // maak van de start- en eindtijd een tijdsspanne
        TimeSpan timeSpan = new TimeSpan(startTime, realEndTime);

        // is de resource wel beschikbaar in die tijdsspanne?
        if (! isAvailable(resourceInstance, timeSpan)) {
            List<ResourceReservation> conflictingAllocations = getConflictingReservations(resourceInstance, timeSpan);
            // bereken hiermee de volgende mogelijke starttijd = de grootste van alle eindtijden van de resources
            LocalDateTime nextStartTime = startTime;
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
     * Maakt de reservaties van een plan.
     * @param plan Het gegeven plan
     * @throws IllegalArgumentException Het plan is niet geldig.
     */
    protected void makeReservationsForPlan(Plan plan) throws IllegalArgumentException {
        if (! plan.isValid()) {
            throw new IllegalArgumentException("Ongeldig plan");
        }

        removeReservationsFromTask(plan.getTask());
        for (ResourceReservation reservation : plan.getReservations()) {
            this.addReservation(plan.getTask(), reservation);
        }
    }

    /**
     * Geeft een lijst van alle resource instanties van een resource type, die beschikbaar zijn vanaf een gegeven starttijd
     * voor een gegeven duur. De lijst is gesorteerd volgens toenemende eindtijd van de eerstvolgende beschikbare tijdsspanne
     * van elke resource instantie.
     * @param resourceType Het gegeven resource type
     * @param startTime    De gegeven starttijd
     * @param duration     De gegeven duur
     */
    public List<ResourceInstance> getAvailableInstances(AResourceType resourceType, LocalDateTime startTime, Duration duration) {
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
    protected List<ResourceReservation> getConflictingReservations(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        List<ResourceReservation> conflictingReservations = new ArrayList<>();
        ImmutableList<ResourceReservation> reservations = this.getReservations(resourceInstance);
        for (ResourceReservation reservation : reservations) {
            if (timeSpan.overlapsWith(reservation.getTimeSpan())) {
                conflictingReservations.add(reservation);
            }
        }
        return conflictingReservations;
    }

    /**
     * Geeft het volgende volledig uur terug als dit al geen volledig uur is. Anders wordt gewoon het gegeven uur teruggegeven.
     * @param dateTime De LocalDateTime die verder moet gezet worden.
     * @return Een volledig uur.
     */
    private LocalDateTime getNextHour(LocalDateTime dateTime) {
        if (dateTime.getMinute() == 0)
            return dateTime;
        else
            return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour()+1,0));
    }

   /**
     * Verwijderd alle reservaties van een taak.
     * @param task De Task waarvan alle reservaties moeten verwijderd worden.
     */
    protected void removeReservationsFromTask(Task task){
        this.reservations.put(task, new ArrayList<>());
    }

    /**
     * Verwijderd een specifieke reservatie van een taak.
     * @param task De Task waarvan de reservatie moet verwijderd worden.
     * @param reservation De te verwijderen reservatie.
     */
    private void removeSpecificReservationFromTask(Task task, ResourceReservation reservation){
        this.reservations.get(task).remove(reservation);
    }

    /**
     * Beeindigt alle reservaties van de gegeven taak op de gegeven eindtijd.
     * @param task De Task waarvan we de Reservaties willen beeindigen.
     * @param endTime De tijd waarop de reservaties moeten eindigen.
     * @throws IllegalArgumentException Wanneer de eindtijd voor een starttijd van een reservatie ligt.
     */
    protected void endReservationsFromTask(Task task, LocalDateTime endTime){
        List<ResourceReservation> reservations = new ArrayList<>(this.getReservations(task));
        for(ResourceReservation reservation: reservations){
            TimeSpan newTimeSpan = new TimeSpan(reservation.getTimeSpan().getStartTime(), endTime);
            ResourceReservation resourceReservation = new ResourceReservation(task, reservation.getResourceInstance(), newTimeSpan, reservation.isSpecific());
            this.reservations.get(task).remove(reservation);
            this.reservations.get(task).add(resourceReservation);
        }
    }

    /**
     * Geeft een immutable list van alle reservaties.
     */
    public ImmutableList<ResourceReservation> getReservations() {
        List<ResourceReservation> allReservations = new LinkedList<>();
        this.reservations.values().forEach(allReservations::addAll);
        return ImmutableList.copyOf(allReservations);
    }

    /**
     * Geeft een immutable list van alle reservaties van een taak.
     * @param task De gegeven taak
     */
    public ImmutableList<ResourceReservation> getReservations(Task task) {
        if (! reservations.containsKey(task)) {
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
    public ImmutableList<ResourceInstance> getAvailableInstances(AResourceType resourceType, TimeSpan timeSpan) {
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
        // TODO: dit is misschien nog niet effici�nt genoeg?

        List<Plan> plans = new LinkedList<>();

        LocalDateTime startTime = getNextHour(dateTime);
        while (plans.size() < n) {
            Plan nextPlan = new Plan(task, startTime,this);
            if (nextPlan.isValidWithoutDevelopers()) {
                plans.add(nextPlan);
            }

            startTime = startTime.plusHours(1);
        }

        return plans;
    }

    /**
     * Geeft een plan met de gegeven starttijd, ook al is de starttijd niet geldig.
     * @param task     De te plannen taak
     * @param dateTime De starttijd
     */
    public Plan getCustomPlan(Task task, LocalDateTime dateTime) {
        return new Plan(task, dateTime,this);
    }
}
