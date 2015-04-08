package be.swop.groep11.main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ResourceType {

    /**
     * Gemakkelijksheidcnstructor om een ResourceType met enkel een naam aan te maken.
     * @param name De naam van dit ResourceType.
     */
    public ResourceType(String name){
        this(name, new ArrayList<ResourceTypeConstraint>(), null, null);
    }

    /**
     * Gemakkelijksheidconstructor om een ResourceType aan te maken zonder restricties op de beschikbaarheid van dit
     * ResourceType.
     *
     * @param name De naam van dit ResourceType
     * @param constraints De constraints die aan dit ResourceType moeten worden toegewezen.
     */
    public ResourceType(String name, List<ResourceTypeConstraint> constraints){
        this(name, constraints, null, null);
    }

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven paramters.
     *
     * @param name De naam van deze ResourceType
     * @param constraints De constraints die aan dit ResourceType moeten worden toegewezen.
     * @param availableFrom Vanaf wanneer dit ResourceType beschikbaar is.
     * @param availableUntil Tot wanneer dit ResourceType beschikbaar is.
     */
    public ResourceType(String name, List<ResourceTypeConstraint> constraints, LocalDateTime availableFrom, LocalDateTime availableUntil) {
        setName(name);
        setResourceTypeConstraints(constraints);
        setAvailableFrom(availableFrom);
        setAvailableUntil(availableUntil);
    }

    /**
     * Een lijst die alle ResourceInstance van dit ResourceType bevat.
     */
    private ArrayList<ResourceInstance> instances = new ArrayList<>();

    /**
     * Voegt een nieuwe instantie van dit type resource toe aan deze ResourceType.
     * @param name
     */
    public void addResourceInstance(String name){
        Resource resource = new Resource(name, this);
        instances.add(resource);
    }

    /**
     * De naam van dit ResourceType.
     */
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Controleert of de gegeven naam wel correct is.
     * @param name De naam die aan dit ResourceType gegeven wordt.
     * @return True als de naam niet null en niet leeg is.
     */
    public static boolean isValidName(String name){
        return name != null && !name.isEmpty();
    }

    /**
     * Een lijst met alle constraint die van toepassing zijn op dit ResourceType.
     */
    private List<ResourceTypeConstraint> resourceTypeConstraints;

    public List<ResourceTypeConstraint> getResourceTypeConstraints() {
        return resourceTypeConstraints;
    }

    public void setResourceTypeConstraints(List<ResourceTypeConstraint> resourceTypeConstraints) {
        for(int i = 0; i < resourceTypeConstraints.size(); i++){
            for(int j = i+1; j < resourceTypeConstraints.size();j++){
                ResourceTypeConstraint constraint1 = resourceTypeConstraints.get(i);
                ResourceTypeConstraint constraint2 = resourceTypeConstraints.get(j);
                if(!constraint1.isValidOtherConstraint(constraint2)){
                    throw new IllegalArgumentException("In de doorgegeven lijst van constraint zit een fout waardoor deze constraints nooit kunnen voldaan zijn.");
                }
            }
        }
        this.resourceTypeConstraints = resourceTypeConstraints.;
    }

    private LocalDateTime availableFrom;

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    private LocalDateTime availableUntil;

    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(LocalDateTime availableUntil) {
        this.availableUntil = availableUntil;
    }
}
