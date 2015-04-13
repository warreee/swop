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
        if (!isValidResourceTypeName(typeName)) {
            throw new IllegalArgumentException("Ongeldige naam voor ResourceType");
        }
        this.name = typeName;
        this.dailyAvailability = new DailyAvailability(LocalTime.MIN, LocalTime.MAX);
    }

    protected void setDailyAvailability(DailyAvailability dailyAvailability) throws IllegalArgumentException {
        if (!isValidDailyAvailability(dailyAvailability)) {
            throw new IllegalArgumentException("Ongeldige DailyAvailability");
        }
        this.dailyAvailability = dailyAvailability;
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
    protected boolean isValidResourceTypeName(String name) {
        return name != null && !name.isEmpty();
    }


    //ConstrainingType,constraint
    private HashMap<IResourceType, ResourceTypeConstraint> constraintMap = new HashMap<>();

    @Override
    public ImmutableList<ResourceTypeConstraint> getTypeConstraints() {
        return ImmutableList.copyOf(constraintMap.values());
    }

    protected void addConflictConstraint(IResourceType constrainingType) throws IllegalArgumentException {
        if (!isValidConstrainingType(constrainingType)) {
            throw new IllegalArgumentException("constrainingType mag niet null zijn");
        }
        ResourceTypeConstraint constraint = new ConflictConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    protected void addRequirementConstraint(IResourceType constrainingType) throws IllegalArgumentException {
        if (!isValidConstrainingType(constrainingType)) {
            throw new IllegalArgumentException("constrainingType mag niet null zijn");
        }
        ResourceTypeConstraint constraint = new RequiresConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    private boolean isValidConstrainingType(IResourceType type) {
        return type != null;
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
        //TODO controleren of het mogelijk is?
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
