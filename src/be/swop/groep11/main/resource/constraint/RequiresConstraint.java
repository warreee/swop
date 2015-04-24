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
     * Constructor voor een nieuwe RequiresConstraint.
     * @param ownerType         Het IResourceType waarop deze constraint van toepassing is.
     * @param constrainingType  Het IResourceTYpe die de beperkende rol vervult inde constraint.
     * @throws IllegalArgumentException
     *                          Gooi indien constrainingType of ownerType niet geinitialiseerd zijn.
     *                          Gooi indien constrainingType == ownerType
     */
    public RequiresConstraint(IResourceType ownerType, IResourceType constrainingType)throws IllegalArgumentException{
        this(ownerType, constrainingType, 1, Integer.MAX_VALUE);
    }
}
