package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Ronald on 9/04/2015.
 *
 * Aanmaken van requirements en bijhouden in een lijst
 *
 */
public class RequirementListBuilder {

    //TODO finish RequirementListBuilder.
    //WIP

    private IRequirementList reqList;

    public RequirementListBuilder() {
        this.reqList = new RequirementList();
    }

    public void addNewRequirement(ResourceType type, int amount) throws IllegalArgumentException,IllegalRequirementAmountException{
        this.reqList.addRequirement(type,amount);
    }

    public void removeRequirementFor(ResourceType type) throws NoSuchElementException{
        this.reqList.removeRequirementFor(type);
    }


    public IRequirementList getRequirements(){
        // copy?
        return this.reqList;
    }

    private class RequirementList implements IRequirementList {

        public RequirementList() {
            this(null);
        }
        public RequirementList(HashMap<ResourceType, ResourceRequirement> reqs) {
            if(reqs != null && !reqs.isEmpty()){
                requirements.putAll(reqs);
            }
        }
        private final HashMap<IResourceType,ResourceRequirement> requirements = new HashMap<>();;

        @Override
        public boolean containsRequirementFor(IResourceType ownerType) {
            return requirements.containsKey(ownerType);
        }

        @Override
        public int countRequiredInstances(IResourceType constrainingType) {
            ResourceRequirement req = requirements.get(constrainingType);
            return (req == null)?0:req.getAmount();
        }

        /**
         * Controleer of deze lijst met Requirements geldig blijft. Indien er een requirement voor het gegeven IResourceType
         * en hoeveelheid zou worden toegevoegd.
         *
         * @param requestedType Het aangevraagde IResourceType
         * @param amount        De aangevraagde hoeveelheid
         * @return
         *        | Niet waar indien de DailyAvailability voor het aangevraagde ResourceType niet (min. 1 uur) overlapt
         *        met de huidige DailyAvailabilities geassocieerd met iedere Requirement in de lijst.
         *        | Niet waar indien aangevraagde hoeveelheid niet aanvaard kan worden
         *        door de aanwezige ResourceTypes geassocieerd met de requirements.
         *        | anders wel waar
         */
        @Override
        public boolean isSatisfiableFor(IResourceType requestedType,int amount) {
            // Niet waar indien de DailyAvailability voor het aangevraagde ResourceType niet overlapt (min. 1 uur)
            // met de huidige DailyAvailabilities overeenkomstig met iedere Requirement in de lijst
            boolean result = hasOverlappingAvailability(requestedType);
            if(!result){
                return false;
            }

            //Niet waar indien aangevraagde hoeveelheid niet aanvaard kan worden door de aanwezige ResourceTypes geassocieerd met de requirements
            for (IResourceType type : requirements.keySet()) {
                for (ResourceTypeConstraint conInList : type.getTypeConstraints()) {
                    for (ResourceTypeConstraint requestedCon : requestedType.getTypeConstraints()) {
                        if (conInList.contradictsWith(requestedCon,amount)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        private boolean hasOverlappingAvailability(IResourceType requestedType){
            List<DailyAvailability> availabilities = new ArrayList<>();
            for(IResourceType type : requirements.keySet()){
                availabilities.add(type.getDailyAvailability());
            }
            if(availabilities.isEmpty()){
                return true;
            }else{
                return requestedType.getDailyAvailability().overlapsWith(availabilities);
            }
        }

        @Override
        public void addRequirement(IResourceType requiredType,int amount) throws IllegalRequirementAmountException,IllegalArgumentException,UnsatisfiableRequirementException{
            if(!isSatisfiableFor(requiredType, amount)){
                throw new UnsatisfiableRequirementException(requiredType, amount);
            }else {
                //Indien er al een requirement is voor type zijn we reeds zeker dat een nieuwe requirement de plaats mag vervangen.
                // Plus de gevraagde hoeveelheid is geen probleem
                ResourceRequirement requirement = new ResourceRequirement(requiredType, amount);
                requirements.put(requiredType,requirement);
            }
        }

        private ResourceRequirement getRequirementFor(IResourceType type) {
            return requirements.get(type);
        }

        @Override
        public void removeRequirementFor(IResourceType type)throws NoSuchElementException{
            if(containsRequirementFor(type)){
                requirements.remove(type);
            }else{
                throw new NoSuchElementException("Geen requirement voor dit type aanwezig.");
            }
        }

    }
}
