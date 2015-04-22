package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;


public class ResourceManager {

    public ResourceManager(){
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
        IResourceType result = null;
        for(IResourceType type : typeBuilders.keySet()){
            if(type.getName().equals(name)){
                result = type;
                break;
            }
        }

        if(result == null){
            throw new NoSuchElementException("Resource type met de gegeven naam kon niet gevonden worden.");
        }
        return result;
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

    //////////////////////////////////////// RESOURCE ALLOCATIONS //////////////////////////////////////////////////////

    /**
     * Maakt een reservatie voor een resource instantie gedurende een bepaalde tijdsspanne.
     * @param resourceInstance De te reserveren resource instantie
     * @param timeSpan         De gegeven tijdsspanne
     * @param isSpecific       True als de resource instantie specifiek gekozen is
     */
    public void makeReservation(ResourceInstance resourceInstance, TimeSpan timeSpan, boolean isSpecific) {
        // TODO: implementatie + exceptions!
    }

    /**
     * Controleert of een resource instantie beschikbaar is gedurende een gegeven tijdsspanne.
     * @param resourceInstance De te controleren resource instantie
     * @param timeSpan         De gegeven tijdsspanne
     * @return True als de resource instantie beschikbaar is.
     */
    public boolean isAvailable(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        /* TODO: controleren of er geen reservaties voor resourceInstance tijdens de huidige systeemtijd
                 en ook er ook geen utilizations zijn voor resourceInstance tijdens de huidige systeemtijd
         */
        return false;
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
     * Geeft een lijst van conflicterende reservaties voor een resource instantie in een bepaalde tijdsspanne.
     * @param resourceInstance De gegeven resource instantie
     * @param timeSpan         De gegeven tijdsspanne
     * @return Lijst van reservaties voor de resource instantie waarvan de tijdsspanne overlapt met de gegeven tijdsspanne
     */
    private List<ResourceReservation> getConflictingReservations(ResourceInstance resourceInstance, TimeSpan timeSpan) {
        List<ResourceReservation> conflictingReservations = new ArrayList<>();
        ImmutableList<ResourceReservation> reservations = null; // TODO: lijst van reservaties voor de resourceInstantie meegeven!
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

    // TODO: reservaties bijhouden + getter
}
