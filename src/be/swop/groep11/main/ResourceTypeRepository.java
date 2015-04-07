package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ResourceTypeRepository {

    public ResourceTypeRepository(){
        addDeveloperType();
    }

    /**
     * Voegt een "Developer" type toe aan deze repository. Doet niets als het "Developer" type al bestaat of er een
     * probleem is met de naam.
     */
    private void addDeveloperType(){
        if(isValidResourceTypeName("Developer")) {
            ResourceType developerType = new ResourceType("Developer", new ArrayList<RequirementConstraint>(), new ArrayList<ConflictConstraint>());
            resourceTypes.add(developerType);
        }
    }

    /**
     * Voegt een nieuwe ResourceType toe zonder start en eindtijd voor de beschikbaarheid.
     * @param name De naam van de toe te voegen ResourceType
     * @param requiredTypes Een lijst van RequirementConstraint voor deze ResourceType.
     * @param conflictingTypes Een lijst van ConflictConstraint voor deze ResourceType.
     */
    public void addResourceType(String name, List<RequirementConstraint> requiredTypes, List<ConflictConstraint> conflictingTypes) {
        ResourceType resourceType = new ResourceType(name, requiredTypes, conflictingTypes);
        resourceTypes.add(resourceType);
    }

    /**
     * Voegt een nieuwe ResourceType toe met start en eindtijd voor de beschikbaarheid.
     * @param name
     * @param requiredTypes
     * @param conflictingTypes
     * @param availableFrom
     * @param availableUntil
     */
    public void addResourceType(String name, List<RequirementConstraint> requiredTypes, List<ConflictConstraint> conflictingTypes, LocalDateTime availableFrom, LocalDateTime availableUntil){
        ResourceType resourceType = new ResourceType(name, requiredTypes, conflictingTypes, availableFrom, availableUntil);
        resourceTypes.add(resourceType);
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
        if(!ResourceType.isValidName(name)){
            return false;
        }

        try {
            getResourceTypeByName(name);
            return false;
        } catch (IllegalArgumentException e){
            return true;
        }
    }

    /**
     * Een lijst die alle bekende resourceType van deze repository bevat.
     */
    private ArrayList<ResourceType> resourceTypes = new ArrayList<ResourceType>();

    public ImmutableList<ResourceType> getResourceTypes() {
        return ImmutableList.copyOf(resourceTypes);
    }
}
