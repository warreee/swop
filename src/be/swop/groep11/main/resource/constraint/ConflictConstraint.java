package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.IResourceType;

/**
 * Created by Ronald on 9/04/2015.
 */
public class ConflictConstraint extends ResourceTypeConstraint {

    private ConflictConstraint(IResourceType ownerType, IResourceType constrainingType, int min, int max) {
        super(ownerType, constrainingType, min, max);
    }

    public ConflictConstraint(IResourceType ownerType, IResourceType constrainingType) {
        this(ownerType, constrainingType, 0, (ownerType!=null && ownerType.equals(constrainingType)) ? 1 : 0);
        /**
         * (ownerType.equals(constrainingType))?1:0
         * indien ownerType conflictConstraint op zichzelf wilt toevoegen
         * mag er hoogstens 1 instance van required worden.
         */
    }


}
