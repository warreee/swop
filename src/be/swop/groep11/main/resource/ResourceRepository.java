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
        ArrayList<ResourceInstance> resourceList = resources.getOrDefault(resource.getResourceType(), new ArrayList<>());
        resourceList.add(resource);
        resources.put(resource.getResourceType(), resourceList);
        //resource.getResourceType().addResourceInstance(resource); // TODO: is deze call nodig? Die lijkt heel raar.
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
        return new ArrayList<>(resources.getOrDefault(type, new ArrayList<>()));
    }

    public int amountOfResourceInstances(AResourceType type) {
        return resources.getOrDefault(type, new ArrayList<>()).size();
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

    /**
     * Geeft het Type terug dat Developers voorsteld.
     * @return getResourceTypeRepository().getDeveloperType()
     */
    public AResourceType getDeveloperType() {
        return getResourceTypeRepository().getDeveloperType();
    }

    //TODO methode request present resource types / resources
}
