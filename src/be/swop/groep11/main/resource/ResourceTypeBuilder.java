package be.swop.groep11.main.resource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse om een ResourceType te builden.
 */
public class ResourceTypeBuilder {

    private String name;
    private DailyAvailability availability = new DailyAvailability(LocalTime.MIN, LocalTime.MAX) ;
    private List<AResourceType> requireTypes = new ArrayList<>();
    private List<AResourceType> conflictingTypes = new ArrayList<>();

    /**
     * Zet de naam voor dit ResourceType.
     * @param name De naam
     */
    public void setName(String name) {
        if(!isValidName(name)){
            throw new IllegalArgumentException("Ongeldige naam. ");
        }
        this.name = name;
    }

    /**
     * Zet de DailyAvailability voor dit ResourceType.
     * @param availability De DailyAvailability.
     * @throws IllegalArgumentException Indien er een fout argument werd doorgegeven.
     */
    public void setAvailability(DailyAvailability availability) throws IllegalArgumentException {
        if (availability == null) {
            throw new IllegalArgumentException("availability == null");
        }
        this.availability = availability;
    }

    /**
     * Zet een lijst met conflicterende types voor dit ResourceType.
     * @param conflictingTypes De conflicterende types.
     * @throws IllegalArgumentException  Indien er een fout argument werd doorgegeven.
     */
    public void setConflictingTypes(List<AResourceType> conflictingTypes) throws IllegalArgumentException{
        if (conflictingTypes == null) {
            throw new IllegalArgumentException("conflictingTypes == null");
        }
        this.conflictingTypes = conflictingTypes;
    }

    /**
     * Zet een lijst met requirede types voor dit ResourceType.
     * @param requireTypes De ruiquirede types.
     * @throws IllegalArgumentException  Indien er een fout argument werd doorgegeven.
     */
    public void setRequireTypes(List<AResourceType> requireTypes)throws IllegalArgumentException {
        if (requireTypes == null) {
            throw new IllegalArgumentException("requireTypes == null");
        }
        this.requireTypes = requireTypes;
    }

    /**
     * Haalt het geconstrueerde ResourceType uit deze builder.
     * @return Het geconstrueerde ResourceType.
     */
    public AResourceType getResourceType(){
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
        return type;
    }

//    /**
//     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
//     * @param name De naam van de toe te voegen ResourceType
//     * @param availability De DailyAvailability van het toe te voegen ResourceType.
//     * @param requireTypes De ResourceTypes(lijst) waarvan het toe te voegen ResourceType afhangt.
//     * @param conflictingTypes De ResourceTypes(lijst) waarmee het toe te voegen ResourceType conflicteerd.
//     *
//     */
//    public AResourceType createNewResourceType(String name, DailyAvailability availability, List<AResourceType> requireTypes, List<AResourceType> conflictingTypes) {
//        if(!isValidName(name)){
//            throw new IllegalArgumentException("Ongeldige naam. ");
//        }
//        ResourceType type = new ResourceType(name);
//        type.setDailyAvailability(availability);
//
//        //Add require constraints
//        for (AResourceType reqType : requireTypes) {
//            type.addRequirementConstraint(reqType);
//        }
//
//        //Add conflicting constraints
//        for (AResourceType conflictType : conflictingTypes) {
//            type.addConflictConstraint(conflictType);
//        }
//        return type;
//    }

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

//    /**
//     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
//     * @param name De naam van de toe te voegen ResourceType
//     * @param availability
//     */
//    public AResourceType addNewResourceType(String name, DailyAvailability availability) {
//        return createNewResourceType(name, availability, new ArrayList<>(), new ArrayList<>());
//    }
//
//    /**
//     * Voegt een nieuwe ResourceType met een DailyAvailability voor een ganse dag.
//     * @param name De naam van de toe te voegen ResourceType
//     */
//    public AResourceType addNewResourceType(String name) {
//        return createNewResourceType(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX), new ArrayList<>(), new ArrayList<>());
//    }
//
//    /**
//     * Voegt een nieuwe ResourceType met requiredTypes en conflictingTypes toe. De DailyAvailability is voor een ganse
//     * dag.
//     * @param name De naam van de toe te voegen ResourceType
//     * @param requireTypes De ResourceTypes(lijst) waarvan het toe te voegen ResourceType afhangt.
//     * @param conflictingTypes De ResourceTypes(lijst) waarmee het toe te voegen ResourceType conflicteerd.
//     */
//    public AResourceType addNewResourceType(String name,  List<AResourceType> requireTypes, List<AResourceType> conflictingTypes){
//        return createNewResourceType(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX), requireTypes, conflictingTypes);
//    }
}
