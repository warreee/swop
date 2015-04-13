package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.TypeConstraint;
import com.google.common.collect.ImmutableList;

/**
 * Created by Ronald on 13/04/2015.
 */
public interface IResourceType {

    public boolean hasConstraintFor(IResourceType typeA);

    public TypeConstraint getConstraintFor(IResourceType typeA);

    public int amountOfConstraints();

    public String getName();

    public DailyAvailability getDailyAvailability();

    public ImmutableList<ResourceInstance> getResourceInstances();

    public int amountOfInstances();
}
