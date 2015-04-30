package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.UnsatisfiableRequirementException;

/**
 * Created by Ronald on 9/04/2015.
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
     *                          TODO fix documentatie volgens recursive check
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



  /*  *//**
     * Controleer voor de gegeven min en max grens mogenlijk zijn
     * @param bMin  Het minimum verwacht aantal resource instances
     * @param bMax  Het maximum toegelaten aantal resource instances
     * @return      Waar indien grenzen >= 0 en bMax is groter of gelijk aan bMin
     *
     */
    private static boolean areValidBounds(int bMin, int bMax) {
            return bMin >= 0 && bMax >= 0 && bMax-bMin >= 0;
    }

 /*   *//**
     * Controleer of deze Constraint vervuld is voor de gegeven IRequirementList
     * @param requirementList   De te controleren lijst requirements.
     * @return  Waar indien de getConstrainingType() een aanvaardbaar keer voor komt in de lijst requirements
     *//*
    public boolean isSatisfied(IRequirementList requirementList){
        boolean result = false;
        if(requirementList.containsRequirementFor(getOwnerType())){
            int count = requirementList.countRequiredInstances(getConstrainingType());
            result = isAcceptableAmount(getConstrainingType(),count);
        }
        return result;
    }*/

    //TODO fix contradictsWith, is niet juist.
    /**
     * Controleer of deze ResourceTypeConstraint tegenstrijdig is met de gegeven ResourceTypeConstraint en een gevraagde hoeveelheid
     * @param otherConstraint   De gegeven ResourceTypeConstraint dewelke eventueel tegenstrijdig is met deze.
     * @param requestedAmount   De aangevraagde hoeveelheid van otherConstraint.getConstrainingType()
     * @return                  Waar indien otherConstraint dezelfde constrainingType heeft. En de minimum van
     *                          otherConstraint aanvaardbaar is voor deze constraint, als ook de aangevraagde hoeveelheid
     *                          een geldige hoeveelheid is voor deze constraint.
     */
  /*  public boolean contradictsWith(ResourceTypeConstraint otherConstraint,int requestedAmount){
        //Moet een beperking zijn op dezelfde ResourceType, en de minimum hoeveelheid van de otherConstraint
        //moet aanvaardbaar zijn voor deze constraint. alsook de aangevraagde hoeveelheid
        if(getMin() > requestedAmount || otherConstraint.getMin() > requestedAmount || requestedAmount > getMax() || requestedAmount > otherConstraint.getMax()){
            // De requested amount ligt niet binnen de grenzen van beide ResourceTypeConstraints.
            return true;
        }
        if(otherConstraint.getOwnerType().equals(getOwnerType())){
            if(otherConstraint.getConstrainingType().equals(getConstrainingType())){
                // Ownertype zelfde, ConstrainingType zelfde
                // Grenzen moeten hetzelfde zijn om niet te conflicteren en requestedAmount moet er binnen liggen.
                return getMax() != otherConstraint.getMax() || getMin() != otherConstraint.getMin();
            }
        }
        return false;*/
//            else {
//                // OwnerType zelfde, ConstrainingType verschillend.
//                // requestedAmount moet binnen beide grenzen liggen.
//                return false;
//            }
//        } else {
//            if(otherConstraint.getConstrainingType().equals(getConstrainingType())){
//                // OwnerType verschillend, ConstrainingType zelfde.
//                return false;
//            } else {
//                // OwnerType Verschillend, ConstrainingType verschillend.
//                // requestedAmount moet binnen beide grenzen liggen.
//                return false;
//            }
//        }
//    }

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
