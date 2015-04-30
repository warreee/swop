package be.swop.groep11.main.resource;

import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.UnsatisfiableRequirementException;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;

import java.util.*;

/**
 * Created by Ronald on 9/04/2015.
 *
 * Aanmaken van requirements en bijhouden in een lijst
 *
 */
public class RequirementListBuilder {

    private RequirementList reqList;

    /**
     * Constructor voor nieuwe RequirementListBuilder
     */
    public RequirementListBuilder() {
        this.reqList = new RequirementList();
    }

    /**
     * Voeg een nieuwe Requirement toe voor het gegeven type en de gevraagde amount
     * @param type      Het IResourceType voor de Requirement
     * @param amount    De hoeveelheid voor de Requirement
     * @throws IllegalArgumentException
     *
     * @throws IllegalRequirementAmountException
     *
     */
    public void addNewRequirement(AResourceType type, int amount) throws IllegalRequirementAmountException,IllegalArgumentException,UnsatisfiableRequirementException {
        this.reqList.addRequirement(type,amount);
    }

    public IRequirementList getRequirements(){
        return this.reqList;
    }

    private class RequirementList implements IRequirementList {

        RequirementList() {
        }

        private final HashMap<AResourceType,ResourceRequirement> requirements = new HashMap<>();

        @Override
        public boolean containsRequirementFor(AResourceType type) {
            return requirements.containsKey(type);
        }

        @Override
        public int countRequiredInstances(AResourceType type) {
            ResourceRequirement req = requirements.get(type);
            return (req == null) ? 0 : req.getAmount();
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
        public boolean isSatisfiableFor(AResourceType requestedType,int amount) {
            // Niet waar indien de DailyAvailability voor het aangevraagde ResourceType niet overlapt (min. 1 uur)
            // met de huidige DailyAvailabilities overeenkomstig met iedere Requirement in de lijst
            if(!hasOverlappingAvailability(requestedType)){
                return false;
            }
            ArrayList<ResourceTypeConstraint> cons = new ArrayList<>();
            for (AResourceType type : requirements.keySet()) {
                cons.addAll(type.getTypeConstraints());
            }

            for (ResourceTypeConstraint constraint : cons) {
                if (!constraint.isAcceptableAmount(requestedType, amount)) {
                    return false;
                }
                //Nog nodig?
        /*        for (ResourceTypeConstraint requestedCon : requestedType.getTypeConstraints()) {
                    if (constraint.contradictsWith(requestedCon, amount)) {
                        return false;
                    }
                }*/
            }

            return true;
        }

        @Override
        public Iterator<ResourceRequirement> iterator() {
            return this.requirements.values().iterator();
        }

        private boolean hasOverlappingAvailability(AResourceType requestedType){
            List<DailyAvailability> availabilities = new ArrayList<>();
            for(AResourceType type : requirements.keySet()){
                availabilities.add(type.getDailyAvailability());
            }
            if(availabilities.isEmpty()){
                // Er zijn geen requirements, dus het requestedType is het enige dat beschikbaar moet zijn.
                return true;
            }else{
                return requestedType.getDailyAvailability().overlapsWith(availabilities);
            }
        }

        private void addRequirement(AResourceType requiredType,int amount) throws IllegalRequirementAmountException,IllegalArgumentException,UnsatisfiableRequirementException{
            if(!isSatisfiableFor(requiredType, amount)){
                throw new UnsatisfiableRequirementException();
            }else {
                //Indien er al een requirement is voor type zijn we reeds zeker dat een nieuwe requirement de plaats mag vervangen.
                // Plus de gevraagde hoeveelheid is geen probleem
                ResourceRequirement requirement = new ResourceRequirement(requiredType, amount);
                requirements.put(requiredType,requirement);
                requiredType.getTypeConstraints().forEach(constraint -> {
                    if (constraint.getMin() > 0 && constraint.getMax() >= constraint.getMin()) {
                        addRequirement(constraint.getConstrainingType(), constraint.getMin());
                    }
                });
            }
        }

        public ResourceRequirement getRequirementFor(AResourceType type) {
            return requirements.get(type);
        }
    }
}
