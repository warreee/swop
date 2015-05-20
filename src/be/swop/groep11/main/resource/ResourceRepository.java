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

    /**
     * Voeg een ResourceInstance toe aan deze ResourceRepository
     * @param resource  De toe te voegen ResourceInstance
     * @throws IllegalArgumentException     Gooi indien resource null is.
     *                                      Gooi indien het AResoureType van resource niet geldig is.
     *                                      Gooi indien deze ResourceRepository resource alreeds bezit.
     */
    public void addResourceInstance(ResourceInstance resource) throws IllegalArgumentException {
        if (!canAddAsNewResourceInstance( resource)) {
            throw new IllegalArgumentException("ongeldige combinatie van type en resourceInstance");
        }
        ArrayList<ResourceInstance> resourceList = resources.getOrDefault(resource.getResourceType(), new ArrayList<>());
        resourceList.add(resource);
        resources.put(resource.getResourceType(), resourceList);
    }

    /**
     * Controleer of de gegeven ResourceInstance geldig is voor deze ResourceRepository
     * @param resource  De te controleren ResourceInstance
     * @return          Waar indien resource niet null is, resource een ondersteund AResourceType bevat
     *                  en resource al niet aanwezig is in deze ResourceRepository. Anders Niet waar.
     */
    public boolean canAddAsNewResourceInstance(ResourceInstance resource) {
        return resource != null && getResourceTypeRepository().containsType(resource.getResourceType()) && !containsResourceInstance(resource);
    }

    /**
     * Bepaal de resourceTypeRepository voor deze ResourceRepository.
     * @param resourceTypeRepository    De nieuwe ResourceTypeRepository.
     * @throws IllegalArgumentException Gooi indien resourceTypeRepository null is of deze ResourceRepository alreeds een ResourceTypeRepository heeft.
     */
    public void setResourceTypeRepository(ResourceTypeRepository resourceTypeRepository) throws IllegalArgumentException{
        if (!canHaveAsResourceTypeRepository(resourceTypeRepository)) {
            throw new IllegalArgumentException("Ongeldige resourceTypeRepository");
        }
        this.resourceTypeRepository = resourceTypeRepository;
    }

    /**
     * Controleer of de gegeven ResourceTypeRepository geldig is voor deze ResourceRepository.
     * @param resourceTypeRepository    De te controleren ResourceTypeRepository.
     * @return  Waar indien resourceTypeRepository niet null en deze ResourceRepository nog geen ResourceTypeRepository bezit.
     *          Anders niet waar.
     */
    public boolean canHaveAsResourceTypeRepository(ResourceTypeRepository resourceTypeRepository) {
        return resourceTypeRepository != null && getResourceTypeRepository() == null ;
    }

    /**
     * @return De ResourceTypeRepository voor deze ResourceRepository.
     */
    public ResourceTypeRepository getResourceTypeRepository() {
        return resourceTypeRepository;
    }

    /**
     * Geef alle ResourceInstances van het gegeven AResourceType
     * @param type  Het AResourceType waarvoor de ResourceInstances opgevraagd worden.
     * @return      Een lijst van ResourceInstances waarvan iedere ResourceInstance van het gegeven type is.
     */
    public List<ResourceInstance> getResources(AResourceType type) {
        return new ArrayList<>(resources.getOrDefault(type, new ArrayList<>()));
    }

    /**
     * Geeft terug hoeveel ResourceInstances bijgehouden worden in deze ResourceRepository van het gegeven AResourceType.
     * @param type  Het AResourceType waarvoor de ResourceInstances opgevraagd worden.
     * @return      De grootte van de lijst ResourceInstances overeenkomstig met het gegeven type.
     */
    public int amountOfResourceInstances(AResourceType type) {
        return resources.getOrDefault(type, new ArrayList<>()).size();
    }

    /**
     * Controleer of de gegeven ResourceInstance aanwezig is in deze ResourceRepository.
     * @param instance  De te controleren ResourceInstance.
     * @return  Waar indien instance aanwezig is, anders niet waar.
     */
    public boolean containsResourceInstance(ResourceInstance instance) {
        return resources.getOrDefault(instance.getResourceType(), new ArrayList<>()).contains(instance);
    }

    /**
     * Geeft het Type terug dat Developers voorsteld.
     * @return getResourceTypeRepository().getDeveloperType()
     */
    public AResourceType getDeveloperType() {
        return getResourceTypeRepository().getDeveloperType();
    }

    /**
     * Geeft een lijst van alle aanwezige AResourceTypes, exclusief DeveloperType.
     * @return
     */
    public ImmutableList<AResourceType> getPresentResourceTypes() {
        ArrayList<AResourceType> types = new ArrayList<>(resources.keySet());
        types.remove(getDeveloperType());
        return ImmutableList.copyOf(types);
    }
}
