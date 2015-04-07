package be.swop.groep11.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronald on 3/04/2015.
 */
public class RequirementConstraint extends ResourceTypeConstraint {

    //TODO tests

    /**
     * Constructor om een nieuwe RequirementConstraint aan te maken.
     *
     * @param types     De lijst ResourceType's die bepalen welke andere resourceTypes zeker aanwezig moeten zijn.
     */
    public RequirementConstraint(List<ResourceType> types) {
        this.requiredTypes = types;
    }

    public List<ResourceType> getRequiredTypes() {
        return requiredTypes;
    }
    private final List<ResourceType> requiredTypes;


    /**
     * Controleer of alle nodige ResourceTypes aanwezig zijn in de lijst ResourceTypeRequirements
     *
     * @param requirements  De lijst ResourceTypeRequirements
     * @return              Waar indien alle nodige resourceTypes aanwezig zijn in de lijst van ResourceTypeRequirement.
     *                      Niet waar indien er minstens één nodig ResourceType niet aanwezig is in de lijst van ResourceTypeRequirements
     */
    @Override
    public boolean isSatisfied(List<ResourceTypeRequirement> requirements) {
        List<ResourceTypeRequirement> temp = resolve(requirements);
        return temp.isEmpty();//indien temp leeg is, zijn alles types aanwezig, dus ok.
    }

    /**
     * Geeft een lijst van ResourceTypeRequirements terug startend van de gegeven requirements.
     * Uitgebreid met de ontbrekende maar nodige ResourceTypeRequirements.
     *
     * @param requirements  De gegeven lijst ResourceTypeRequirements
     * @return              Indien alle nodige ResourceTypeRequirements reeds aanwezig geeft men requirements terug.
     *                      Anders worden de requirements uitgebreid met de ontbrekende ResourceTypeRequirements.
     *
     */
    @Override
    public List<ResourceTypeRequirement> resolve(List<ResourceTypeRequirement> requirements) {
        List<ResourceTypeRequirement> result = new ArrayList<>(requirements);

        //Voor iedere ResourceType dat aanwezig moet zijn in de requirements
        //Controleren of dat type aanwezig is in de gegeven lijst
        for(ResourceType  conType: this.getRequiredTypes()){
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

}
