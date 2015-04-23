package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequiresConstraint;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;


class ResourceType implements  IResourceType{

    private final String name;
    private DailyAvailability dailyAvailability;

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven parameters.
     *
     * @param typeName De naam van deze ResourceType
     */
    protected ResourceType(String typeName) throws IllegalArgumentException {
        this.name = typeName;
        this.dailyAvailability = new DailyAvailability(LocalTime.MIN, LocalTime.MAX);
    }

    protected void setDailyAvailability(DailyAvailability dailyAvailability){
        this.dailyAvailability = dailyAvailability;
    }

    //ConstrainingType,constraint
    private HashMap<IResourceType, ResourceTypeConstraint> constraintMap = new HashMap<>();

    @Override
    public ImmutableList<ResourceTypeConstraint> getTypeConstraints() {
        return ImmutableList.copyOf(constraintMap.values());
    }

    protected void addConflictConstraint(IResourceType constrainingType) {
        ResourceTypeConstraint constraint = new ConflictConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    protected void addRequirementConstraint(IResourceType constrainingType){
        ResourceTypeConstraint constraint = new RequiresConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    @Override
    public boolean hasConstraintFor(IResourceType typeA) {
        return constraintMap.containsKey(typeA);
    }

    @Override
    public ResourceTypeConstraint getConstraintFor(IResourceType typeA) {
        //TODO documentatie kan null terug geven
        return constraintMap.get(typeA);
    }

    @Override
    public int amountOfConstraints() {
        return constraintMap.size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
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
    protected void addResourceInstance(String name) {
        Resource resource = new Resource(name, this);
        instances.add(resource);
    }

    @Override
    public ImmutableList<ResourceInstance> getResourceInstances() {
        return ImmutableList.copyOf(instances);
    }

    @Override
    public int amountOfInstances() {
        return instances.size();
    }
}
