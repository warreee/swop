package be.swop.groep11.main.resource;

import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;


public class ResourceTypeRepository {

    public ResourceTypeRepository(){
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
        /**
         * Er kunnen geen resourceTypeConstraints zijn omdat ze al een verwijzing naar de resourcetype nodig hebben, terwijl we die nog moeten aanmaken.
         */
        if(containsType(name)){
            throw new IllegalArgumentException("Er bestaat reeds een ResourceType met de naam " +name);
        }
        ResourceTypeBuilder newTypeBuilder = typeBuilders.put(name,new ResourceTypeBuilder(name));
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
     *
     * @param name De naam van het op te vragen ResourceType
     * @throws IllegalArgumentException wordt gegooid als de naam niet in deze repository zit.
     * @return
     */
    public IResourceType getResourceTypeByName(String name)throws NoSuchElementException{
        ResourceTypeBuilder b = typeBuilders.get(name);
        if(b == null){
            throw new NoSuchElementException("Resource type met de gegeven naam kon niet gevonden worden.");
        }
        return b.getResourceType();
    }

    public boolean containsType(String typeName){
        return typeBuilders.containsKey(typeName);
    }

    /**
     * Een lijst die alle bekende resourceType van deze repository bevat.
     */
    private ArrayList<IResourceType> resourceTypes = new ArrayList<>();

    public ImmutableList<IResourceType> getResourceTypes() {
        return ImmutableList.copyOf(resourceTypes);
    }

    private HashMap<String, ResourceTypeBuilder> typeBuilders = new HashMap<>();


    public void withDailyAvailability(IResourceType ownerType, DailyAvailability availability) {
        typeBuilders.get(ownerType).withDailyAvailability(availability);
    }

    public void withRequirementConstraint(IResourceType ownerType, IResourceType reqType) {
        typeBuilders.get(ownerType).withRequirementConstraint(reqType);
    }

    public void withConflictConstraint(IResourceType ownerType, IResourceType conflictType) {
        typeBuilders.get(ownerType).withConflictConstraint(conflictType);
    }

    public void addResourceInstance(IResourceType ownerType, String inst) {
        typeBuilders.get(ownerType).addResourceInstance(inst);

    }
}
