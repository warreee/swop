package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequiresConstraint;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import be.swop.groep11.main.exception.IllegalInputException;
import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ronald on 13/04/2015.
 */
public abstract class AResourceType {

    private final String name;
    private DailyAvailability dailyAvailability;

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven parameters.
     *
     * @param typeName De naam van deze ResourceType
     */
    protected AResourceType(String typeName) throws IllegalArgumentException {
        if (!isValidResourceTypeName(typeName)) {
            throw new IllegalArgumentException("Ongeldige naam voor ResourceType");
        }
        this.name = typeName;
        setDailyAvailability(new DailyAvailability(LocalTime.MIN, LocalTime.MAX));
    }

    protected void setDailyAvailability(DailyAvailability availability) throws IllegalArgumentException {
        if (!isValidDailyAvailability(availability)) {
            throw new IllegalArgumentException("Ongeldige DailyAvailability");
        }
        this.dailyAvailability = availability;
    }

    private boolean isValidDailyAvailability(DailyAvailability availability) {
        return availability != null;
    }

    /**
     * Controleert of de gegeven naam wel correct is.
     *
     * @param name De naam die aan dit ResourceType gegeven wordt.
     * @return True als de naam niet null en niet leeg is.
     */
    private boolean isValidResourceTypeName(String name) {
        return name != null && !name.isEmpty();
    }

    private HashMap<AResourceType, ResourceTypeConstraint> constraintMap = new HashMap<>();


    /**
     * @return een immutable lijst van deze constraints.
     */
    public ArrayList<ResourceTypeConstraint> getTypeConstraints() {
        return new ArrayList<>(constraintMap.values());
    }

    protected void addConflictConstraint(AResourceType constrainingType) {
        ResourceTypeConstraint constraint = new ConflictConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    protected void addRequirementConstraint(AResourceType constrainingType){
        ResourceTypeConstraint constraint = new RequiresConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    /**
     * Controleer of er een constraint van toepassing is op dit type
     * met het gegeven type als het constraining type.
     * @param typeA Het te controleren constrainingType
     * @return      Waar indien dit type een constraint heeft met typA als constrainingType
     */
    public boolean hasConstraintFor(AResourceType typeA) {
        return constraintMap.containsKey(typeA);
    }

    /**
     * Geeft de constraint terug voor het gegeven een IResourceType
     * @param typeA De constraining type van het terug te geven ResourceTypeConstraint.
     * @return      De corresponderende ResourceTypeConstraint, of null indien er geen is.
     */
    public ResourceTypeConstraint getConstraintFor(AResourceType typeA) {
        return constraintMap.get(typeA);
    }

    /**
     * @return geeft terug hoeveel constraints er van toepassing zijn op dit type.
     */
    public int amountOfConstraints() {
        return constraintMap.size();
    }


    /**
     * @return geeft de naam voor het type terug.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return dailyAvailability voor dit type.
     */
    public DailyAvailability getDailyAvailability() {
        return dailyAvailability;
    }

    /**
     * Een lijst die alle ResourceInstance van dit ResourceType bevat.
     */
    private ArrayList<ResourceInstance> instances = new ArrayList<>();

    /**
     * Voegt een nieuwe instantie van dit type resource toe aan deze ResourceType.
     *
     * @param name De naam van de ResourceInstance die moet worden toegevoegd.
     */
    protected abstract void addResourceInstance(String name) throws IllegalArgumentException;

    protected void addResourceInstance(ResourceInstance resourceInstance) {
        if (resourceInstance == null) {
            throw new IllegalInputException("Ongeldige resourceInstance, moet ge?nitialiseerd zijn");
        }
        instances.add(resourceInstance);
    }

    /**
     * @return een immutable lijst van alle ResourceInstances voor dit type.
     */
    public ImmutableList<ResourceInstance> getResourceInstances() {
        return ImmutableList.copyOf(instances);
    }

    /**
     * @return geef terug hoeveel ResourceInstances er zijn voor dit type.
     */
    public int amountOfInstances() {
        return instances.size();
    }

    @Override
    public String toString() {
        return "AResourceType{" +
                "name='" + name + '\'' +
                '}';
    }
}
