package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.AResourceType;

/**
 * Created by Ronald on 9/04/2015.
 */
public class ConflictConstraint extends ResourceTypeConstraint {

    private ConflictConstraint(AResourceType ownerType, AResourceType constrainingType, int min, int max) {
        super(ownerType, constrainingType, min, max);
    }

    /**
     * Constructor voor een nieuwe ConflictConstraint.
     * @param ownerType         Het IResourceType waarop deze constraint van topeassing is.
     * @param constrainingType  Het IResourceTYpe die de beperkende rol vervult inde constraint.
     * @throws IllegalArgumentException
     *                          Gooi indien constrainingType of ownerType niet geinitialiseerd zijn.
     */
    public ConflictConstraint(AResourceType ownerType, AResourceType constrainingType)throws IllegalArgumentException{
        this(ownerType, constrainingType, 0, (ownerType != null && ownerType.equals(constrainingType)) ? 1 : 0);
        /**
         * (ownerType.equals(constrainingType))?1:0
         * indien ownerType conflictConstraint op zichzelf wilt toevoegen
         * mag er hoogstens 1 instance van required worden.
         */
    }
}
