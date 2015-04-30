package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDate;
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

    }

    public AResourceType getDeveloperType() {
        if (this.developerType == null) {
            developerType =  new DeveloperType();
            resourceTypes.add(developerType);
        }
        return developerType;
    }

    private DeveloperType developerType;

    //TODO documentatie
    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
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
     * @return
     */
    public AResourceType getResourceTypeByName(String name)throws NoSuchElementException{
        for(AResourceType type : resourceTypes){
            if(type.getName().equals(name)){
                return type;
            }
        }
        throw new NoSuchElementException("Resource type met de gegeven naam kon niet gevonden worden.");
    }

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
     * Zet voor het gegeven IResourceType de DailyAvailability op de gegeven DailyAvailability.
     * @param ownerType     Het IResourceType waarvan de DailyAvailability zal veranderen.
     * @param availability  De nieuwe DailyAvailability voor ownerType.
     * @throws IllegalArgumentException
     *                      Gooi indien availability null is
     */
    public void withDailyAvailability(AResourceType ownerType, DailyAvailability availability) throws IllegalArgumentException{
        ownerType.setDailyAvailability(availability);
    }

    /**
     *
     * @param ownerType
     * @param reqType
     */
    public void withRequirementConstraint(AResourceType ownerType, AResourceType reqType) {
        ownerType.addRequirementConstraint(reqType);
    }

    /**
     *
     * @param ownerType
     * @param conflictType
     */
    public void withConflictConstraint(AResourceType ownerType, AResourceType conflictType) {
        ownerType.addConflictConstraint(conflictType);
    }

    /**
     *
     * @param ownerType
     * @param inst
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
        LocalDateTime realEndTime = resourceInstance.calculateEndTime(realStartTime, duration);

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
    private void makeReservationsForPlan(IPlan plan) throws IllegalArgumentException {
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

   /**
     * Verwijderd alle reservaties van een taak.
     * @param task De Task waarvan alle reservaties moeten verwijderd worden.
     */
    private void removeReservationsFromTask(Task task){
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
    private void endReservationsFromTask(Task task, LocalDateTime endTime){
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
    public List<IPlan> getNextPlans(int n, Task task, LocalDateTime dateTime) {
        // TODO: dit is misschien nog niet effici�nt genoeg?

        List<IPlan> plans = new LinkedList<>();

        LocalDateTime startTime = getNextHour(dateTime);
        while (plans.size() < n) {
            Plan nextPlan = new Plan(task, startTime);
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
    public IPlan getCustomPlan(Task task, LocalDateTime dateTime) {
        return new Plan(task, dateTime);
    }

    private class Plan implements IPlan {

        /**
         * Constructor om een nieuw plan aan te maken met default reservaties voor de resource requirements van de gegeven taak.
         * Hierbij worden ook de default reservaties voor het plan toegevoegd.
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
            this.makeDefaultReservations();
        }

        /**
         * Controleert of dit plan geldig is.
         *
         * @return True als de reservaties niet conflicteren met andere reservaties
         *         en alle nodige reservaties gemaakt zijn.
         */
        @Override
        public boolean isValid() {

            // geen conflicten?
            for (ResourceReservation reservation : this.getReservations()) {
                if (!isAvailable(reservation.getResourceInstance(), reservation.getTimeSpan())) {
                    return false;
                }
            }

            // nodige reservaties gemaakt?
            Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
            while (it.hasNext()) {
                ResourceRequirement requirement = it.next();

                List<ResourceReservation> reservations = this.getReservations(requirement.getType());

                int nbRequiredInstances = requirement.getAmount();
                int nbReservations = reservations.size();

                if (nbRequiredInstances != nbReservations) {
                    return false;
                }
            }

            return true;
        }

        /**
         *
         */
        @Override
        public boolean isValidWithoutDevelopers() {
            // geen conflicten?
            for (ResourceReservation reservation : this.getReservations()) {
                if (!isAvailable(reservation.getResourceInstance(), reservation.getTimeSpan())) {
                    return false;
                }
            }

            // nodige reservaties gemaakt?
            Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
            while (it.hasNext()) {
                ResourceRequirement requirement = it.next();

                if (requirement.getType() != getDeveloperType()) {

                    ImmutableList<ResourceReservation> reservations = this.getReservations(requirement.getType());

                    int nbRequiredInstances = requirement.getAmount();
                    int nbReservations = reservations.size();

                    if (nbRequiredInstances != nbReservations) {
                        return false;
                    }

                }
            }

            return true;
        }

        /*
         * Controleert of op de starttijd voor alle resource requirements de nodige resource instanties kunnen
         * gereserveerd worden voor de taak.
         *
        public boolean canMakeDefaultReservations() {
            LocalDateTime endTime = calculateEndTime(task, startTime);
            TimeSpan timeSpanOfPlan = new TimeSpan(startTime, endTime);

            Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
            while (it.hasNext()) {
                ResourceRequirement requirement = it.next();

                if (requirement.getType() != getDeveloperType()) {
                    List<ResourceInstance> availableInstances = getAvailableInstances(requirement.getType(), timeSpanOfPlan);

                    int nbRequiredInstances = requirement.getAmount();
                    if (availableInstances.size() < nbRequiredInstances) {
                        return false;
                    }
                }
            }
            return true;
        } */

        /**
         * Maakt voor elk resource type in de requirement list van de taak de nodige reservaties.
         */
        public void makeDefaultReservations() {
            this.reservations = calculateDefaultReservations(this.getTask(), this.getStartTime());
        }

        /**
         * Geeft de taak van dit plan.
         */
        @Override
        public Task getTask() {
            return task;
        }

        private final Task task;

        /**
         * Geeft de starttijd van dit plan.
         */
        @Override
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
        @Override
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
        @Override
        public ImmutableList<ResourceReservation> getReservations() {
            List<ResourceReservation> reservations = new LinkedList<>();
            // zorg wel dat alle reservaties op het zelfde moment eindigen!
            for (ResourceReservation reservation : this.reservations) {
                if (reservation.getTimeSpan().getEndTime().isBefore(this.maxEndTime())) {
                    reservations.add(new ResourceReservation(reservation.getTask(),
                            reservation.getResourceInstance(),
                            new TimeSpan(reservation.getTimeSpan().getStartTime(), this.maxEndTime()),
                            reservation.isSpecific()));
                }
                else {
                    reservations.add(new ResourceReservation(reservation.getTask(),
                            reservation.getResourceInstance(),
                            reservation.getTimeSpan(),
                            reservation.isSpecific()));
                }
            }
            return ImmutableList.copyOf(reservations);
        }

        /**
         * Geeft de reservaties van dit plan voor een bepaalde resource type.
         *
         * @param resourceType Het resource type
         * @return De reservaties van dit plan en die eindigen allemaal op hetzelfde moment.
         */
        @Override
        public ImmutableList<ResourceReservation> getReservations(AResourceType resourceType) {
            List<ResourceReservation> reservations = new LinkedList<>();
            ImmutableList<ResourceReservation> reservationlist = this.getReservations();
            for (ResourceReservation reservation : reservationlist) {
                if (reservation.getResourceInstance().getResourceType() == resourceType) {
                    reservations.add(reservation);
                }
            }
            return ImmutableList.copyOf(reservations);
        }

        private LocalDateTime maxEndTime() {
            LocalDateTime maxEndTime = this.getStartTime();
            for (ResourceReservation reservation : this.reservations) {
                if (reservation.getTimeSpan().getEndTime().isAfter(maxEndTime)) {
                    maxEndTime = reservation.getTimeSpan().getEndTime();
                }
            }
            return maxEndTime;
        }

        /**
         * Gooit de huidige reservaties weg en voegt reservaties voor de gegeven resource instanties toe.
         * @param resourceInstances De gegeven resource instanties
         */
        @Override
        public void changeReservations(List<ResourceInstance> resourceInstances) {
            this.reservations = new LinkedList<>();
            for (ResourceInstance resourceInstance : resourceInstances) {
                this.addReservation(resourceInstance);
            }
        }

        /**
         * Voegt reservaties voor de gegeven resource instanties toe.
         * @param resourceInstances De gegeven resource instanties
         */
        @Override
        public void addReservations(List<ResourceInstance> resourceInstances) {
            for (ResourceInstance resourceInstance : resourceInstances) {
                this.addReservation(resourceInstance);
            }
        }

        /**
         * Voegt een reservatie voor een resource instantie toe aan dit plan.
         * De toegevoegde reservatie zal een specifieke reservatie zijn.
         * @param resourceInstance De te reserveren resource instantie
         * @throws IllegalArgumentException Er is in dit plan al een reservatie voor de gegeven resource instantie gemaakt.
         */
        public void addReservation(ResourceInstance resourceInstance) {
            if (this.hasReservationFor(resourceInstance)) {
                throw new IllegalArgumentException("Er is in dit plan al een reservatie voor de gegeven resource instantie gemaakt.");
            }

            this.reservations.add(new ResourceReservation(this.getTask(),
                    resourceInstance,
                    new TimeSpan(this.getStartTime(), getNextAvailableTimeSpan(resourceInstance,this.getStartTime(),this.getTask().getEstimatedDuration()).getEndTime()),
                    true));
        }

        /**
         * Controleert of dit plan een reservatie voor een resource instantie bevat.
         */
        @Override
        public boolean hasReservationFor(ResourceInstance resourceInstance) {
            for (ResourceReservation reservation : this.reservations) {
                if (reservation.getResourceInstance() == resourceInstance) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Geeft de taken die reservaties hebben die conflicteren met de reservaties van dit plan.
         */
        @Override
        public List<Task> getConflictingTasks() {
            Set<Task> conflictingTasks = new HashSet<>();
            for (ResourceReservation reservation : this.getReservations()) {
                List<ResourceReservation> conflictingReservations = getConflictingReservations(reservation.getResourceInstance(), reservation.getTimeSpan());
                if (! conflictingReservations.isEmpty()) {
                    conflictingTasks.add(conflictingReservations.get(0).getTask());
                }
            }
            return new LinkedList<>(conflictingTasks);
        }

        /**
         * Past dit plan toe door huidige de reservaties van de taak te verwijderen
         * en de reservaties van het plan toe te voegen.
         * @throws IllegalStateException De reservaties voor dit plan kunnen niet gemaakt worden.
         */
        @Override
        public void apply() {
            try {
                endReservationsFromTask(this.getTask(), this.getStartTime());
                makeReservationsForPlan(this);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException("De reservaties voor dit plan kunnen niet gemaakt worden.");
            }
        }

        private List<ResourceReservation> calculateDefaultReservations(Task task, LocalDateTime startTime) throws IllegalArgumentException {
            List<ResourceReservation> defaultReservations = new LinkedList<>();

            Iterator<ResourceRequirement> it = task.getRequirementList().iterator();
            while (it.hasNext()) {
                ResourceRequirement requirement = it.next();

                if (requirement.getType() != getDeveloperType()) {
                    List<ResourceInstance> availableInstances = getAvailableInstances(requirement.getType(), startTime, task.getEstimatedDuration());

                    int nbRequiredInstances = requirement.getAmount();
                    int nbAvailableInstances = availableInstances.size();
                    if (nbAvailableInstances < nbRequiredInstances) {
                        // maak geen reservaties
                    } else {
                        // voeg de nodige reservaties toe
                        for (int i = 0; i < nbRequiredInstances; i++) {
                            defaultReservations.add(new ResourceReservation(task, availableInstances.get(i),
                                    getNextAvailableTimeSpan(availableInstances.get(i), startTime, task.getEstimatedDuration()), false));
                        }
                    }
                }
            }
            return defaultReservations;
        }

        /**
         * Geeft de gereserveerde resources van de taak weer vrij.
         * @param endTime De tijd waarop de reservaties van de taak moeten eindigen.
         */
        @Override
        public void releaseResources(LocalDateTime endTime) {
            endReservationsFromTask(this.getTask(), endTime);
        }

    }

}
