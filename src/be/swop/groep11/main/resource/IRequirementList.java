package be.swop.groep11.main.resource;

import java.util.NoSuchElementException;

/**
 * Created by Ronald on 9/04/2015.
 */
public interface IRequirementList {

    public boolean containsRequirementFor(IResourceType ownerType);

    public int countRequiredInstances(IResourceType constrainingType);

    public boolean isSatisfiableFor(IResourceType requestedType, int amount) ;

    public void addRequirement(IResourceType type, int amount) throws IllegalRequirementAmountException,IllegalArgumentException,UnsatisfiableRequirementException;

    public void removeRequirementFor(IResourceType type)throws NoSuchElementException;
}
