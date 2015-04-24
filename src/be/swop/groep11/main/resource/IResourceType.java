package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import com.google.common.collect.ImmutableList;

/**
 * Created by Ronald on 13/04/2015.
 */
public interface IResourceType {

    //TODO implementatie voor DeveloperType

    /**
     * Geeft de constraint terug voor het gegeven een IResourceType
     * @param typeA De constraining type van het terug te geven ResourceTypeConstraint.
     * @return      De corresponderende ResourceTypeConstraint, of null indien er geen is.
     */
    ResourceTypeConstraint getConstraintFor(IResourceType typeA);

    /**
     * @return een immutable lijst van deze constraints.
     */
    ImmutableList<ResourceTypeConstraint> getTypeConstraints();

    /**
     * @return geeft de naam voor het type terug.
     */
    String getName();

    /**
     * @return dailyAvailability voor dit type.
     */
    DailyAvailability getDailyAvailability();

    /**
     * @return een immutable lijst van alle ResourceInstances voor dit type.
     */
    ImmutableList<ResourceInstance> getResourceInstances();

    /**
     * @return geef terug hoeveel ResourceInstances er zijn voor dit type.
     */
    int amountOfInstances();

    /**
     * @return geeft terug hoeveel constraints er van toepassing zijn op dit type.
     */
    int amountOfConstraints();

    /**
     * Controleer of er een constraint van toepassing is op dit type
     * met het gegeven type als het constraining type.
     * @param typeA Het te controleren constrainingType
     * @return      Waar indien dit type een constraint heeft met typA als constrainingType
     */
    boolean hasConstraintFor(IResourceType typeA);


}
