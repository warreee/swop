package be.swop.groep11.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ronald on 3/04/2015.
 */
public class RequirementConstraint extends ResourceTypeConstraint {

    //TODO tests

    public RequirementConstraint(ResourceType onType, List<ResourceType> requiredTypes) {
        super(onType, requiredTypes);
    }

    /**
     * Check of de gegeven lijst van ResourceTypes een correcte lijst is voor deze nieuwe ResourceTypeConstraint.
     * @param constrainingTypes     De gegeven lijst van ResourceType
     * @return                      Waar indien constrainingTypes geïnstantieerd en niet leeg is.
     *                              Niet waar indien constrainingTypes getOwnerType() bevat.
     */
    @Override
    protected boolean canHaveAsConstrainingTypes(List<ResourceType> constrainingTypes) {
        boolean result = true;
        if(constrainingTypes == null){
            result = false;
        }else if(constrainingTypes.isEmpty()){
            result = false;
        }else if(constrainingTypes.contains(getOwnerType())){ //Een requirementConstraint van een type mag niet het type zelf als requirement bezitten.
            result = false;
        }
        return result;
    }

    /**
     * Controleer of alle nodige ResourceTypes aanwezig zijn in de lijst ResourceTypeRequirements
     *
     * @param requirements  De lijst ResourceTypeRequirements
     * @return              Waar indien alle nodige resourceTypes aanwezig zijn in de lijst van ResourceTypeRequirement.
     *                      Niet waar indien er minstens één nodig ResourceType niet aanwezig is in de lijst van ResourceTypeRequirements
     */
    @Override
    public boolean isSatisfied(List<ResourceTypeRequirement> requirements) {
        //Voor iedere ResourceType dat aanwezig moet zijn in de requirements
        //Controleren of dat type aanwezig is in de gegeven lijst
        for(ResourceType  conType: this.getConstrainingTypes()){
            boolean contains = false;
            for(ResourceTypeRequirement rq : requirements){
                contains = conType.equals(rq.getType());
            }
            if(contains == false){
                return false;//Het nodige type is niet aanwezig.
            }
        }
        return true;
    }

    /**
     * Geeft een lijst van ResourceTypeRequirements terug startend van de gegeven requirements.
     * Uitgebreid met de ontbrekende maar nodige ResourceTypeRequirements.
     *
     * @param requirements  De gegeven lijst ResourceTypeRequirements
     * @return              Indien alle nodige ResourceTypeRequirements reeds aanwezig geeft men requirements terug.
     *                      Anders worden de requirements uitgebreid met de ontbrekende ResourceTypeRequirements.
     */
    @Override
    public List<ResourceTypeRequirement> resolve(List<ResourceTypeRequirement> requirements) {
        List<ResourceTypeRequirement> result = new ArrayList<>(requirements);

        //Voor iedere ResourceType dat aanwezig moet zijn in de requirements
        //Controleren of dat type aanwezig is in de gegeven lijst
        for(ResourceType  conType: this.getConstrainingTypes()){
            boolean contains = false;
            for(ResourceTypeRequirement rq : requirements){

                contains = conType.equals(rq.getType());
            }
            if(contains == false){
                //Indien het nodige type niet aanwezig is voegen we het toe aan tijdelijke lijst.
                result.add(new ResourceTypeRequirement(conType,1)); // Enkel 1?
            }
        }
        return result;
    }

    /**
     * Controleer of deze RequirementConstraint tegenstrijdig is met de gegeven ResourceTypeConstraint.
     * @param otherConstraint   De andere ResourceTypeConstraint om te controleren
     * @return                  Waar indien otherConstraint als ownerType een verschillend ResourceType heeft als deze RequirementConstraint.
     *                          Indien otherConstraint instantie van ConflictConstraint
     *                                  Waar indien de constrainingTypes niet overlappen. Anders niet waar.
     */
    @Override
    public boolean isValidOtherConstraint(ResourceTypeConstraint otherConstraint) {
        boolean result = false;
        if(otherConstraint instanceof ConflictConstraint){
            List<ResourceType> list = (otherConstraint).getConstrainingTypes();
            result = Collections.disjoint(getConstrainingTypes(),list);
        }else if(otherConstraint instanceof RequirementConstraint){
            result = true;//Andere requirementConstraints vormen geen probleem
        }

        return result;
    }
}
