package be.swop.groep11.main.resource;

import java.util.Iterator;

/**
 * Created by Ronald on 9/04/2015.
 */
public interface IRequirementList{

    boolean containsRequirementFor(IResourceType ownerType);

    int countRequiredInstances(IResourceType constrainingType);

    boolean isSatisfiableFor(IResourceType requestedType, int amount) ;

    Iterator<ResourceRequirement> iterator();
}
