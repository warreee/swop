package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.IResourceType;

/**
 * Created by Ronald on 9/04/2015.
 */
public class RequiresConstraint extends ResourceTypeConstraint {

    private RequiresConstraint(IResourceType ownerType, IResourceType constrainingType, int min, int max) throws IllegalArgumentException{
        super(ownerType, constrainingType, min, max);
        if(ownerType.equals(constrainingType)){
            throw new IllegalArgumentException("mag zichzelf niet requiren.");
        }
    }

    /**
     * Minstens 1 hoogstens MAX
     *
     * @param ownerType
     * @param constrainingType
     */
    public RequiresConstraint(IResourceType ownerType, IResourceType constrainingType) {
        this(ownerType, constrainingType, 1, Integer.MAX_VALUE);
    }
}
