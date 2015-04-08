package be.swop.groep11.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronald on 3/04/2015.
 */
public class ConflictConstraint extends ResourceTypeConstraint {

    //TODO tests

    /**
     * Constructor om een nieuwe ConflictConstraint aan te maken.
     *
     * @param conflictingTypes     De lijst ResourceType's die bepalen welke andere resourceTypes zeker niet aanwezig mogen zijn.
     */
    public ConflictConstraint(ResourceType onType, List<ResourceType> conflictingTypes) {
        super(onType, conflictingTypes);

    }

    @Override
    protected boolean canHaveAsConstrainingTypes(List<ResourceType> constrainingTypes) {
        boolean result = true;
        if(constrainingTypes == null){
            result = false;
        }else if(constrainingTypes.isEmpty()){
            result = false;
        }else if(constrainingTypes.contains(getOwnerType())){ //Een ConflictConstraint van een type mag wel het type zelf als requirement bezitten
            result = true;
        }
        return result;
    }

    @Override
    public boolean isSatisfied(List<ResourceTypeRequirement> requirementList) {
        //Controleer of ieder ResourceType die niet aanwezig mag zijn in de requirements, toch aanwezig is
        for(ResourceType  conType: this.getConstrainingTypes()){
            for(ResourceTypeRequirement rq : requirementList){
                if (conType.equals(rq.getType())) {
                   return false; //Er is een ResourceType aanwezig wat niet mag.
                }
            }
        }
        return true; //Geen verkeerde requirements aanwezig.
    }

    /**
     * Geeft een lijst van ResourceTypeRequirements terug zonder Requirements die conflict zouden veroorzaken.
     *
     * @param requirementList De gegeven lijst ResourceTypeRequirements
     * @return  Lijst van ResourceTypeRequirements gebaseerd op requirementList,
     *          zonder de ResourceTypeRequirements met een types die conflicten geven.
     */
    @Override
    public List<ResourceTypeRequirement> resolve(List<ResourceTypeRequirement> requirementList) {
        List<ResourceTypeRequirement> result = new ArrayList<>(requirementList);
        //Controleer of ieder ResourceType die niet aanwezig mag zijn in de requirements, toch aanwezig is
        for(ResourceType  conType: this.getConstrainingTypes()){
            for(ResourceTypeRequirement rq : requirementList){
                if (conType.equals(rq.getType())) {
                    result.remove(rq); //Er is een ResourceType aanwezig wat niet mag. Verwijder uit lijst
                }
            }
        }
        return result;
    }

    /**
     * Controleer of deze ConflictConstraint tegenstrijdig is met de gegeven ResourceTypeConstraint.
     * @param otherConstraint   De andere ResourceTypeConstraint om te controleren
     * @return                  Waar indien otherConstraint als ownerType een verschillend ResourceType heeft als deze RequirementConstraint.
     *                          Indien otherConstraint instantie van RequirementConstraint:
     *                                  Waar indien de constrainingTypes niet overlappen. Anders niet waar.
     */
    @Override
    public boolean isValidOtherConstraint(ResourceTypeConstraint otherConstraint) {
        boolean result = false;
        if(otherConstraint instanceof RequirementConstraint){
            result = (otherConstraint).isValidOtherConstraint(this);
        }else if(otherConstraint instanceof ConflictConstraint){
            result = true;//Andere conflictConstraints vormen geen probleem
        }
        return result;
    }
}
