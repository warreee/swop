package be.swop.groep11.main.resource;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ronald on 13/05/2015.
 */
public class ResourceRepository {
    private ResourceTypeRepository resourceTypeRepository;
    private HashMap<AResourceType, ArrayList<ResourceInstance>> resources;


    /**
     * Maakt een nieuwe ResourceRepository aan.
     * @param resourceTypeRepository De centrale ResourceTypeRepository waarin alle AResourceType beschikbaar zijn.
     */
    public ResourceRepository(ResourceTypeRepository resourceTypeRepository){
        setResourceTypeRepository(resourceTypeRepository);
        resources = new HashMap<>();
    }

    public void addResourceInstance(ResourceInstance resource) {
        if (!canAddAsNewResourceInstance( resource)) {
            throw new IllegalArgumentException("ongeldige combinatie van type en resourceInstance");
        }
        ArrayList<ResourceInstance> resourceList = resources.getOrDefault(resource, new ArrayList<>());
        resourceList.add(resource);
        resources.put(resource.getResourceType(), resourceList);
    }

    public void setResourceTypeRepository(ResourceTypeRepository resourceTypeRepository) throws IllegalArgumentException{
        if (!canHaveAsResourceTypeRepository(resourceTypeRepository)) {
            throw new IllegalArgumentException("Ongeldige resourceTypeRepository");
        }
        this.resourceTypeRepository = resourceTypeRepository;
    }

    public boolean canHaveAsResourceTypeRepository(ResourceTypeRepository resourceTypeRepository) {
        return resourceTypeRepository != null && getResourceTypeRepository() == null ;
    }

    public ResourceTypeRepository getResourceTypeRepository() {
        return resourceTypeRepository;
    }

    public List<ResourceInstance> getResources(AResourceType type) {
        return ImmutableList.copyOf(resources.get(type));
    }

    public int amountOfResourceInstances(AResourceType type) {
        return resources.get(type).size();
    }

    public boolean containsResourceInstance(ResourceInstance instance) {
        return resources.getOrDefault(instance.getResourceType(), new ArrayList<>()).contains(instance);
    }

    public boolean canAddAsNewResourceInstance(ResourceInstance resource) {
        return getResourceTypeRepository().containsType(resource.getResourceType()) && !containsResourceInstance(resource);
    }

    public ImmutableList<AResourceType> getPresentResourceTypes() {
        //TODO zonder developers?
        return null;
    }

    //TODO methode request present resource types / resources
}
