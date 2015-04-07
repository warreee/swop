package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robin on 2/04/15.
 */
public class ResourceTypeRepository {

    public ResourceTypeRepository(){

    }

    private ArrayList<ResourceType> resourceTypes = new ArrayList<ResourceType>();

    public ImmutableList<ResourceType> getResourceTypes() {
        return ImmutableList.copyOf(resourceTypes);
    }

    public void addResourceType(String name, List<RequirementConstraint> requiredTypes, List<ConflictConstraint> conflictingTypes) {
        ResourceType resourceType = new ResourceType(name, requiredTypes, conflictingTypes);
        resourceTypes.add(resourceType);
    }

    public void addResourceType(String name, List<RequirementConstraint> requiredTypes, List<ConflictConstraint> conflictingTypes, LocalDateTime availableFrom, LocalDateTime availableUntil){
        ResourceType resourceType = new ResourceType(name, requiredTypes, conflictingTypes, availableFrom, availableUntil);
        resourceTypes.add(resourceType);
    }
}
