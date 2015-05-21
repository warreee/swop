package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequiresConstraint;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstracte klasse die als basis dient voor ResourceTypes.
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
    }

    /**
     * Zet de dagelijkse beschikbaar van dit AResourceType.
     * @param availability De dagelijkse beschikbaarheid.
     * @throws IllegalArgumentException Wordt gegooid indien die DailyAvailability ongeldig is.
     */
    public void setDailyAvailability(DailyAvailability availability) throws IllegalArgumentException {
        if (!canHaveAsDailyAvailability(availability)) {
            throw new IllegalArgumentException("Ongeldige DailyAvailability");
        }
        this.dailyAvailability = availability;
    }

    /**
     * Controleert of de gegeven DailyAvailability een geldige DailyAvailability is.
     * @param availability De te controleren DailyAvailability.
     * @return  True als de gegeven DailyAvailability geldig is en de huidige
     *          dailyAvailability voor deze ResourceType is null, anders False.
     */
    private boolean canHaveAsDailyAvailability(DailyAvailability availability) {
        return availability != null && this.getDailyAvailability() == null;
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

    /**
     * Voegt een ConflictConstraint toe.
     * @param constrainingType Het AResourceType waarvoor deze ConflictConstraint wordt toegevoegd.
     */
    protected void addConflictConstraint(AResourceType constrainingType) {
        ResourceTypeConstraint constraint = new ConflictConstraint(this, constrainingType);
        constraintMap.put(constrainingType, constraint);
    }

    /**
     * Voegt een RequiresConstraint toe.
     * @param constrainingType Het AResourceType waarvoor deze ConflictConstraint wordt toegevoegd.
     */
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
    public String getTypeName() {
        return this.name;
    }

    /**
     * @return dailyAvailability voor dit type.
     */
    public DailyAvailability getDailyAvailability() {
        return dailyAvailability;
    }


    public abstract LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration);



        @Override
    public String toString() {
            return name ;
        }
}
