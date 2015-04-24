package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.IRequirementList;

/**
 * Created by Ronald on 9/04/2015.
 */
public abstract class ResourceTypeConstraint {

    private final IResourceType ownerType;
    private final IResourceType constrainingType;

    private final int min;
    private final int max;

    /**
     * Constructor voor nieuwe ResourceTypeConstraint
     * @param ownerType         Het IResourceType dewelke "eigenaar" is van de constraint
     * @param constrainingType  Het IResourceType dewelke de beperkende rol vervult.
     * @param min               Het minimum aantal toegelaten instances van IResourceType
     * @param max               Het maximum aantal toegelaten instances van IResourceType
     */
    protected ResourceTypeConstraint(IResourceType ownerType, IResourceType constrainingType, int min, int max) {
        if(!areValidTypes(ownerType, constrainingType)){
            throw new IllegalArgumentException("Ongeldig ResourceType's");
        }
        if(!areValidBounds(ownerType, constrainingType, min, max)){
            throw new IllegalArgumentException("Ongeldige min en max voor constraint");
        }
        this.ownerType = ownerType;
        this.constrainingType = constrainingType;
        this.min = min;
        this.max = max;
    }

    /**
     * @return Waar indien ownerType & constrainingType niet null zijn.
     */
    private boolean areValidTypes(IResourceType ownerType,IResourceType constrainingType) {
        return ownerType != null && constrainingType != null;
    }

    /**
     * @return Het IResourceType dewelke eigenaar is van deze constraint.
     */
    public IResourceType getOwnerType() {
        return ownerType;
    }
    /**
     * @return Het IResourceType dewelke de beperkende rol vervult.
     */
    public IResourceType getConstrainingType() {
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
     * Controleer voor de gegeven IResourceTypes of de gegeven grenzen kunnen kloppen
     * @param typeA De eigenaar van de constraint
     * @param typeB Het beperkende IResourceType voor deze constraint
     * @param bMin  Het minimum verwacht aantal resource instances
     * @param bMax  Het maximum toegelaten aantal resource instances
     * @return      //TODO document return
     */
    private boolean areValidBounds(IResourceType typeA,IResourceType typeB,int bMin, int bMax) {
        if(!typeB.hasConstraintFor(typeA)){
            return true;
        }else{
            //wel een constraint voor typeA aanwezig in typeB
            ResourceTypeConstraint consA = typeB.getConstraintFor(typeA);
            int aMin = consA.getMin();
            int aMax = consA.getMax();
            boolean result;

            if(aMin < bMin){
                result = false;
            }else if(aMin > bMax) {
                result = false;
            }else if (aMax < bMin){
                result = false;
            }else{
                //aMax < bMax is ok.
                //aMax > bMax is ok.
                result = true;
            }
            return result;
        }
    }

    /**
     * Controleer of deze Constraint vervuld is voor de gegeven IRequirementList
     * @param requirementList   De te controleren lijst requirements.
     * @return  Waar indien de getConstrainingType() een aanvaardbaar keer voor komt in de lijst requirements
     */
    public boolean isSatisfied(IRequirementList requirementList){
        boolean result = false;
        if(requirementList.containsRequirementFor(getOwnerType())){
            int count = requirementList.countRequiredInstances(getConstrainingType());
            result = isAcceptableAmount(getConstrainingType(),count);
        }
        return result;
    }

    /**
     * Controleer of deze ResourceTypeConstraint tegenstrijdig is met de gegeven ResourceTypeConstraint en een gevraagde hoeveelheid
     * @param otherConstraint   De gegeven ResourceTypeConstraint dewelke eventueel tegenstrijdig is met deze.
     * @param requestedAmount   De aangevraagde hoeveelheid van otherConstraint.getConstrainingType()
     * @return                  Waar indien otherConstraint dezelfde constrainingType heeft. En de minimum van
     *                          otherConstraint aanvaardbaar is voor deze constraint, als ook de aangevraagde hoeveelheid
     *                          een geldige hoeveelheid is voor deze constraint.
     */
    public boolean contradictsWith(ResourceTypeConstraint otherConstraint,int requestedAmount){
        //Moet een beperking zijn op dezelfde ResourceType, en de minimum hoeveelheid van de otherConstraint
        //moet aanvaardbaar zijn voor deze constraint. alsook de aangevraagde hoeveelheid
        boolean result = false;
        if(otherConstraint.getConstrainingType().equals(getConstrainingType())){
            result = isAcceptableAmount(otherConstraint.getConstrainingType(),otherConstraint.getMin());
            result &= isAcceptableAmount(otherConstraint.getConstrainingType(),requestedAmount);
        }
        return result;

    }

    /**
     * Controleer of de gegeven hoeveelheid van resourceType een geldige hoeveelheid van getConstrainingType() resource instances zou zijn.
     * @param amount        De te controleren hoeveelheid
     * @param resourceType  De IResourceType die "amount" keer aangevraagd is.
     * @return              Waar indien resourceType == getConstrainingType() && amount >= getMin() && amount <= getMax()
     */
    public boolean isAcceptableAmount(IResourceType resourceType,int amount) {
        return resourceType != getConstrainingType() || (amount >= getMin() && amount <= getMax());
    }

}
