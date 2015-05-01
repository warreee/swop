package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.exception.UnsatisfiableRequirementException;

/**
 * Abstracte klasse die als basis dient voor alle constraints.
 */
public abstract class ResourceTypeConstraint {
    //Het type waarop de constraint van toepassing is.
    private final AResourceType ownerType;
    //Het type die de beperkende rol vervult.
    private final AResourceType constrainingType;

    //Het minimum toegelaten ResourceInstances om deze constraint te laten slagen.
    private final int min;
    //Het maximum toegelaten ResourceInstances om deze constraint te laten slagen.
    private final int max;

    /**
     * Constructor voor nieuwe ResourceTypeConstraint
     * @param ownerType         Het IResourceType dewelke "eigenaar" is van de constraint
     * @param constrainingType  Het IResourceType dewelke de beperkende rol vervult.
     * @param min               Het minimum aantal toegelaten instances van IResourceType
     * @param max               Het maximum aantal toegelaten instances van IResourceType
     * @throws UnsatisfiableRequirementException
     *                          Gooi indien de gegeven ownerType || constrainingType niet ge?nitialiseerd zijn.
     *                          Wordt gegooid als er dependencies zijn die niet kunnen.
     */
    protected ResourceTypeConstraint(AResourceType ownerType, AResourceType constrainingType, int min, int max) throws IllegalArgumentException{
        if(!areValidTypes(ownerType, constrainingType)){
            throw new IllegalArgumentException();
        }
        if(!areValidBounds(min, max)){
            throw new IllegalArgumentException("Ongeldige min en max voor constraint");
        }
        if (!recursiveValidTypesCheck(ownerType, constrainingType, min, max)) {
            throw new UnsatisfiableRequirementException();
        }

        this.ownerType = ownerType;
        this.constrainingType = constrainingType;
        this.min = min;
        this.max = max;
    }

    /**
     * @return Waar indien ownerType & constrainingType niet null zijn.
     */
    private boolean areValidTypes(AResourceType ownerType,AResourceType constrainingType) {
        return ownerType != null && constrainingType != null;
    }

    /**
     * Controlleerd dat voor het ownerType het newConstrainingType geen onmogelijke dependencies opleverd.
     * @param ownerType Het AResourceType dat de eigeneer is van deze constraint.
     * @param newConstrainingType Het AResourceType waarvoor de dependency wordt toegevoegd.
     * @param min Het minimun aantal instanties.
     * @param max Het maximum aantal instanties.
     * @return True als die kan, anders False.
     */
    private boolean recursiveValidTypesCheck(AResourceType ownerType,AResourceType newConstrainingType,int min,int max) {

        boolean result = true;
        for (ResourceTypeConstraint typeConstraint : ownerType.getTypeConstraints()) {
            result &= typeConstraint.getConstrainingType() != newConstrainingType &&
                    recursiveValidTypesCheck(typeConstraint.getConstrainingType(), newConstrainingType,min,max);
        }
        if (newConstrainingType.hasConstraintFor(ownerType)) {
            ResourceTypeConstraint rtc = newConstrainingType.getConstraintFor(ownerType);
            result &= !(rtc.getMin() > max);
        }
        return result;
    }

    /**
     * @return Het IResourceType dewelke eigenaar is van deze constraint.
     */
    public AResourceType getOwnerType() {
        return ownerType;
    }
    /**
     * @return Het IResourceType dewelke de beperkende rol vervult.
     */
    public AResourceType getConstrainingType() {
        return constrainingType;
    }

    /**
     * @return Het max aantal toegelaten ResourceInstances van getConstrainingType() voor deze constraint
     */
    public int getMax() {
        return max;
    }
    /**
     * @return Het minimum aantal verwachte ResourceInstances van getConstrainingType() voor deze constraint
     */
    public int getMin() {
        return min;
    }

    /**
     * Controleer voor de gegeven min en max grens mogenlijk zijn
     * @param bMin  Het minimum verwacht aantal resource instances
     * @param bMax  Het maximum toegelaten aantal resource instances
     * @return      Waar indien grenzen >= 0 en bMax is groter of gelijk aan bMin
     *
     */
    private static boolean areValidBounds(int bMin, int bMax) {
            return bMin >= 0 && bMax >= 0 && bMax-bMin >= 0;
    }


    /**
     * Controleer of de gegeven hoeveelheid van resourceType een geldige hoeveelheid van getConstrainingType() resource instances zou zijn.
     * @param amount        De te controleren hoeveelheid
     * @param resourceType  De IResourceType die "amount" keer aangevraagd is.
     * @return              Waar indien resourceType == getConstrainingType() && amount >= getMin() && amount <= getMax()
     */
    public boolean isAcceptableAmount(AResourceType resourceType,int amount) {
        return resourceType != getConstrainingType() || (amount >= getMin() && amount <= getMax());
    }

}
