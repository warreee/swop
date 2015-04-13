package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


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
        if(!containtsType("Developer")) {
            addResourceType("Developer", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(17, 0)));
        }
    }

    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
     * @param constrainingTypes Een lijst van ResourceTypeConstraints voor deze ResourceType.
     */
    public void addResourceType(String name, DailyAvailability availability, List<ResourceTypeConstraint> constrainingTypes) {
        /**
         * Er kunnen geen resourceTypeConstraints zijn omdat ze al een verwijzing naar de resourcetype nodig hebben, terwijl we die nog moeten aanmaken.
         */
        if(containtsType(name)){
            throw new IllegalArgumentException("Er bestaat reeds een ResourceType met de naam " +name);
        }
        ResourceType resourceType = new ResourceType(name,availability);
        resourceTypes.add(resourceType);

        resourceType.setResourceTypeConstraints(constrainingTypes);
    }
    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
     * @param availability //TODO documentatie
     */
    public void addResourceType(String name, DailyAvailability availability) {
        addResourceType(name, availability, new ArrayList<>());
    }

    /**
     * Voegt een nieuwe ResourceType met een DailyAvailability voor een ganse dag.
     * @param name De naam van de toe te voegen ResourceType
     */
    public void addResourceType(String name) {
        addResourceType(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX), new ArrayList<>());
    }

    /**
     *
     * @param name De naam van het op te vragen ResourceType
     * @throws IllegalArgumentException wordt gegooid als de naam niet in deze repository zit.
     * @return
     */
    public ResourceType getResourceTypeByName(String name){
        for(ResourceType r: resourceTypes){
            if (r.getName().equals(name)){
                return r;
            }
        }
        throw new IllegalArgumentException("Resource type met de gegeven naam kon niet gevonden worden.");
    }

    /**
     * Controleert of de gegeven naam een geldige naam is voor een nieuwe ResourceType instantie.
     * @param name De naam die de nieuwe ResourceType moet hebben
     * @return true asa dit gaat, anders false
     */
    public boolean isValidResourceTypeName(String name){
        if(!ResourceType.isValidResourceTypeName(name)){
            return false;
        }

        try {
            getResourceTypeByName(name);
            return false;
        } catch (IllegalArgumentException e){
            return true;
        }
    }


    public boolean containtsType(String typeName){

        for(ResourceType r: resourceTypes){
            if (r.getName().equals(typeName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Een lijst die alle bekende resourceType van deze repository bevat.
     */
    private ArrayList<ResourceType> resourceTypes = new ArrayList<ResourceType>();

    public ImmutableList<ResourceType> getResourceTypes() {
        return ImmutableList.copyOf(resourceTypes);
    }

}
