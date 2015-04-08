package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class ResourceType {
    private final DailyAvailability dailyAvailability;

    /**
     * Gemakkelijksheidconstructor om een ResourceType met enkel een naam aan te maken.
     * @param name De naam van dit ResourceType.
     */
    public ResourceType(String name){
        this(name, new DailyAvailability(LocalTime.MIN, LocalTime.MAX));
    }

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven parameters.
     *
     * @param name De naam van deze ResourceType
     * @param availability Hoelang dit ResourceType beschikbaar is per dag.
     */
    public ResourceType(String name, DailyAvailability availability) {
        if(!isValidResourceTypeName(name)){
            throw new IllegalArgumentException("Ongeldige naam voor ResourceType");
        }
        if(!isValidDailyAvailability(availability)){
            throw new IllegalArgumentException("Ongeldige DailyAvailability");
        }
        this.name = name;
        this.dailyAvailability = availability;
    }


    private boolean isValidDailyAvailability(DailyAvailability availability) {
        return availability != null;
    }

    public DailyAvailability getDailyAvailability() {
        return dailyAvailability;
    }

    /**
     * Een lijst die alle ResourceInstance van dit ResourceType bevat.
     */
    private ArrayList<ResourceInstance> instances = new ArrayList<>();

    /**
     * Voegt een nieuwe instantie van dit type resource toe aan deze ResourceType.
     * @param name De naam van de ResourceInstance die moet worden toegevoegd.
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
    public static boolean isValidResourceTypeName(String name){
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

    /**
     * Controleert of de gegeven lijst met resourceTypeConstraints wel mag.
     * @param resourceTypeConstraints de lijst met resourceTypeConstraintss
     * @return true als de lijst geen conflicten heeft en de lijst met instances leeg is..
     */
    private boolean canHaveAsResourceTypeConstraints(List<ResourceTypeConstraint> resourceTypeConstraints){

        // De lijst mag alleen veranderd worden als er nog geen instances zijn van dit ResourceType.
        if(instances.size() != 0){
            return false;
        }

        // Controleer of er een conflict is voor elke combinatie van resourceTypeConstraints.
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
}