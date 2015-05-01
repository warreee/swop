package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.AResourceType;

/**
 * Stelt een ConflictConstraint tussen twee Tasks voor.
 */
public class ConflictConstraint extends ResourceTypeConstraint {

    /**
     * Private constructor om deze ConflictConstraint te initialiseren volgens het model van de superklasse
     * @param ownerType         Het IResourceType waarop deze constraint van toepassing is.
     * @param constrainingType  Het IResourceTYpe die de beperkende rol vervult in de constraint.
     * @param min Het minimaal aantal van het constrainingType dat aanwezig moet zijn.
     * @param max Het maximaal aantal van het constrainingType dat aanwezig moet zijn.
     */
    private ConflictConstraint(AResourceType ownerType, AResourceType constrainingType, int min, int max) {
        super(ownerType, constrainingType, min, max);
    }


    /**
     * Constructor voor een nieuwe ConflictConstraint.
     * @param ownerType         Het IResourceType waarop deze constraint van toepassing is.
     * @param constrainingType  Het IResourceTYpe die de beperkende rol vervult in de constraint.
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
