package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class ResourceType {
    private final DailyAvailability dailyAvailability;

    /**
     * Gemakkelijksheidcnstructor om een ResourceType met enkel een naam aan te maken.
     * @param name De naam van dit ResourceType.
     */
    public ResourceType(String name){
        this(name, new ArrayList<ResourceTypeConstraint>());
    }

    /**
     * Gemakkelijksheidconstructor om een ResourceType aan te maken zonder restricties op de beschikbaarheid van dit
     * ResourceType.
     *
     * @param name De naam van dit ResourceType
     * @param constraints De constraints die aan dit ResourceType moeten worden toegewezen.
     */
    public ResourceType(String name, List<ResourceTypeConstraint> constraints){
        this(name, constraints, new DailyAvailability(LocalTime.MIN,LocalTime.MAX));
    }

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven parameters.
     *
     * @param name De naam van deze ResourceType
     * @param constraints De constraints die aan dit ResourceType moeten worden toegewezen.
     * @param availableFrom Vanaf wanneer dit ResourceType beschikbaar is.
     * @param availableUntil Tot wanneer dit ResourceType beschikbaar is.
     */
    public ResourceType(String name, List<ResourceTypeConstraint> constraints,DailyAvailability availability) {
        if(!isValidResourceTypeName(name)){
            throw new IllegalArgumentException("Ongeldige naam voor ResourceType");
        }
        if(!isValidDailyAvailability(availability)){
            throw new IllegalArgumentException("Ongeldige DailyAvailability");
        }
        setResourceTypeConstraints(constraints);
        this.name = name;
        this.dailyAvailability = availability;
    }


    private boolean isValidResourceTypeName(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isValidDailyAvailability(DailyAvailability availability) {
        return availability != null;
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
    private final String name;


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

    /**
     * Geeft een ImmutableList van ResourceTypeConstraints terug die alle constraint van dit ResourceType bevat.
     */
    public ImmutableList<ResourceTypeConstraint> getResourceTypeConstraints() {
        return ImmutableList.copyOf(resourceTypeConstraints);
    }

    /**
     * Zet de gegeven resourceTypeConstraints lijst als constraints voor dit ResourceType object. De lijst wordt
     * gecontroleerd op mogelijke conflicten.
     * @param resourceTypeConstraints De lijst met ResourceTypeConstraint
     * @throws IllegalArgumentException Wordt gegooid als er een conflict is bij de gegeven resourceTypeConstraints.
     */
    public void setResourceTypeConstraints(List<ResourceTypeConstraint> resourceTypeConstraints) {
        if(!canHaveAsResourceTypeConstraints(resourceTypeConstraints)){
            throw new IllegalArgumentException("In de doorgegeven lijst van constraint zit een conflict waardoor deze constraints nooit kunnen voldaan zijn.");
        }
        this.resourceTypeConstraints = new ArrayList<>(resourceTypeConstraints);
    }

    private boolean canHaveAsResourceTypeConstraints(List<ResourceTypeConstraint> resourceTypeConstraints){
        for(int i = 0; i < resourceTypeConstraints.size(); i++){
            for(int j = i+1; j < resourceTypeConstraints.size();j++){
                ResourceTypeConstraint constraint1 = resourceTypeConstraints.get(i);
                ResourceTypeConstraint constraint2 = resourceTypeConstraints.get(j);
                if(!constraint1.isValidOtherConstraint(constraint2)){
                   return false;
                }
            }
        }
        return true;
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
