package be.swop.groep11.main.resource;

import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Stelt een resource type repository voor, die een lijst van resource types bevat.
 */
public class ResourceTypeRepository {

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

    public boolean containsType(AResourceType resourceType) {
        return resourceTypes.contains(resourceType);
    }

}
