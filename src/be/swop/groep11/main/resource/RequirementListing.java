package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 9/04/2015.
 */
public interface RequirementListing {

    boolean containsRequirementFor(IResourceType ownerType);
    int countRequiredInstances(IResourceType constrainingType);

    boolean isSatisfiableForRequirement(ResourceRequirement requirement) ;
}
