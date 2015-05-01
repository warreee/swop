package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.AResourceType;

/**
 * Created by Ronald on 9/04/2015.
 */
public class RequiresConstraint extends ResourceTypeConstraint {

    /**
     * Private constructor om deze RequiresConstraint te initialiseren volgens het model van de superklasse
     * @param ownerType         Het IResourceType waarop deze constraint van toepassing is.
     * @param constrainingType  Het IResourceType die de beperkende rol vervult in de constraint.
     * @param min Het minimaal aantal van het constrainingType dat aanwezig moet zijn.
     * @param max Het maximaal aantal van het constrainingType dat aanwezig moet zijn.
     */
    private RequiresConstraint(AResourceType ownerType, AResourceType constrainingType, int min, int max) throws IllegalArgumentException{
        super(ownerType, constrainingType, min, max);
        if(ownerType.equals(constrainingType)){
            throw new IllegalArgumentException("mag zichzelf niet requiren.");
        }
    }

    /**
     * Constructor voor een nieuwe RequiresConstraint.
     * @param ownerType         Het IResourceType waarop deze constraint van toepassing is.
     * @param constrainingType  Het IResourceTYpe die de beperkende rol vervult in de constraint.
     * @throws IllegalArgumentException
     *                          Gooi indien constrainingType of ownerType niet geinitialiseerd zijn.
     *                          Gooi indien constrainingType == ownerType
     */
    public RequiresConstraint(AResourceType ownerType, AResourceType constrainingType)throws IllegalArgumentException{
        this(ownerType, constrainingType, 1, Integer.MAX_VALUE);
    }
}
