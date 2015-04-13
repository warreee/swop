package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.TypeConstraint;

import java.util.HashMap;

/**
 * Created by Ronald on 9/04/2015.
 *
 * Aanmaken van requirements en bijhouden in een lijst
 *
 */
public class RequirementListBuilder {

    private RequirementList reqList;

    public RequirementListBuilder() {
        this.reqList = new RequirementList();
    }

    boolean canHaveAsRequirement(ResourceType requestedType,int amount){

        return false;
    }
    //TODO dailyAvailabilty overlappen?

    public void addRequirement(ResourceType type, int amount) throws IllegalArgumentException,IllegalRequirementAmountException{
        if(!isValidAmountForType(type,amount)){
            throw new IllegalRequirementAmountException("Ongeldige hoeveelheid voor het gegeven ResourceType.",type,amount);
        }

        ResourceRequirement req = new ResourceRequirement(type, amount);
        this.reqList.addRequirement(req);


    }

    private boolean isValidAmountForType(ResourceType type, int amount) {
        if( type.hasConstraintFor(type)){
            TypeConstraint constraintB = type.getConstraintFor(type);
            return constraintB.isAcceptableAmount(amount);
        }
        return true;
    }

    public RequirementListing getRequirements(){
        //TODO copy?
        return this.reqList;
    }



    private class RequirementList implements RequirementListing {

        public RequirementList() {
            this(null);
        }
        public RequirementList(HashMap<ResourceType, ResourceRequirement> reqs) {
            this.requirements = new HashMap<>();
            if(reqs != null && !reqs.isEmpty()){
                requirements.putAll(reqs);
            }
        }
        private final HashMap<IResourceType,ResourceRequirement> requirements;

        @Override
        public boolean containsRequirementFor(IResourceType ownerType) {
            return requirements.containsKey(ownerType);
        }

        @Override
        public int countRequiredInstances(IResourceType constrainingType) {
            ResourceRequirement req = requirements.get(constrainingType);
            return (req == null)?0:req.getAmount();
        }


        @Override
        public boolean isSatisfiableForRequirement(ResourceRequirement requirement) {
            //Indien de hoeveelheid in requirement niet aanvaard kan worden door de reeds aanwezige requirements => false
            IResourceType requestedType = requirement.getType();
            int amount = requirement.getAmount();

            for (IResourceType type : requirements.keySet()) {
                for (TypeConstraint conInList : type.getRequirementConstraints()) {
                    for (TypeConstraint requestedCon : requestedType.getRequirementConstraints()) {
                        if (conInList.contradictsWith(requestedCon,amount)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private void addRequirement(ResourceRequirement requirement){
            IResourceType type = requirement.getType();
            if(!isSatisfiableForRequirement(requirement)){
                throw new UnsatisfiableRequirementException(requirement);
            }else {
                //if(containsRequirementFor(type))
                //Indien er al een requirement is voor type
                //zijn we reeds zeker dat de nieuwe requirement de plaats mag vervangen.
                //De gevraagde hoeveelheid is geen probleem
                requirements.put(type,requirement);
            }
        }

        private ResourceRequirement getRequirementFor(ResourceType type) {
            return  requirements.get(type);
        }

        private void removeRequirement(ResourceRequirement req){

        }

        private boolean containsRequirement(ResourceRequirement req){
            return false;
        }


    }
}
