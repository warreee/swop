package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.RequirementListing;

/**
 * Created by Ronald on 9/04/2015.
 */
public abstract class TypeConstraint {

    private final IResourceType ownerType;
    private final IResourceType constrainingType;

    private final int min;
    private final int max;

    TypeConstraint(IResourceType ownerType, IResourceType constrainingType, int min, int max) {
        if(!areValidTypes(ownerType, constrainingType)){
            throw new IllegalArgumentException("Ongeldig ResourceType's");
        }
        if(!areValidBounds(ownerType, constrainingType, min, max)){
            //TODO andere exception gooien?
            throw new IllegalArgumentException("Ongeldige min en max voor constraint");
        }

        this.ownerType = ownerType;
        this.constrainingType = constrainingType;
        this.min = min;
        this.max = max;
    }

    private boolean areValidTypes(IResourceType ownerType,IResourceType constrainingType) {
        if(ownerType == null || constrainingType == null){
            return false;
        }
        return true;
    }

    public IResourceType getOwnerType() {
        return ownerType;
    }

    public IResourceType getConstrainingType() {
        return constrainingType;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    private boolean areValidBounds(IResourceType typeA,IResourceType typeB,int bMin, int bMax) {
        if(!typeB.hasConstraintFor(typeA)){
            return true;
        }else{
            //wel een constraint voor typeA aanwezig in typeB
            TypeConstraint consA = typeB.getConstraintFor(typeA);
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

    public boolean isSatisfied(RequirementListing requirementList){
        boolean result = false;
        if(requirementList.containsRequirementFor(getOwnerType())){
            int count = requirementList.countRequiredInstances(getConstrainingType());
            result = isAcceptableAmount(count);
        }
        return result;
    }

    public boolean contradictsWith(TypeConstraint otherConstraint,int requestedAmount){
       //TODO documentatie contradictsWith
        //Moet een beperking zijn op dezelfde ResourceType, en de minimum hoeveelheid van de otherConstraint
        //moet aanvaardbaar zijn voor deze constraint. alsook de aangevraagde hoeveelheid
       boolean result = false;
        if(otherConstraint.getConstrainingType().equals(getConstrainingType())){
            result = !isAcceptableAmount(otherConstraint.getMin());
            result |= !isAcceptableAmount(requestedAmount);
        }
//        return otherConstraint.getConstrainingType().equals(getConstrainingType()) && !isAcceptableAmount(otherConstraint.getMin()) ;
        return result;

    }

    public boolean isAcceptableAmount(int amount) {
        return amount >= getMin() && amount <= getMax();
    }

}
