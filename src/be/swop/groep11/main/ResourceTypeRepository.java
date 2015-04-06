package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

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

    public void addResourceType(ResourceType type) {
        if (!isValidResourceType(type)){
            throw new IllegalArgumentException("Geen geldig RescourceType aan ResourceTypeRepository toegevoegd.");
        }
        resourceTypes.add(type);
    }

    public static boolean isValidResourceType(ResourceType type){
        return type != null;
    }
}
