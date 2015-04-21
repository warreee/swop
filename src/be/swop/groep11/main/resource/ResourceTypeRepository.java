package be.swop.groep11.main.resource;

import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;


public class ResourceTypeRepository {

    public ResourceTypeRepository(){
        //TODO garantie developers als resourceType niet in de constructor van ResourceTypeRepository?
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
}
