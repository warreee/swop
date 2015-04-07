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
     * @param types     De lijst ResourceType's die bepalen welke andere resourceTypes zeker niet aanwezig mogen zijn.
     */
    public ConflictConstraint(List<ResourceType> types) {
        this.conflictingTypes = types;
    }

    public List<ResourceType> getConflictingTypes() {
        return conflictingTypes;
    }
    private final List<ResourceType> conflictingTypes;


    @Override
    public boolean isSatisfied(List<ResourceTypeRequirement> requirements) {
        //Controleer of ieder ResourceType die niet aanwezig mag zijn in de requirements, toch aanwezig is
        List<ResourceTypeRequirement> temp = resolve(requirements);
        return temp.size() == requirements.size(); //Indien alles ok, zijn er geen requirements verwijderd
    }

    /**
     * Geeft een lijst van ResourceTypeRequirements terug zonder Requirements die conflict zouden veroorzaken.
     *
     * @param constrainingTypes De gegeven lijst ResourceTypeRequirements
     * @return
     */
    @Override
    public List<ResourceTypeRequirement> resolve(List<ResourceTypeRequirement> constrainingTypes) {
        List<ResourceTypeRequirement> result = new ArrayList<>(constrainingTypes);


        //Controleer of ieder ResourceType die niet aanwezig mag zijn in de requirements, toch aanwezig is
        for(ResourceType  conType: this.getConflictingTypes()){
            for(ResourceTypeRequirement rq : constrainingTypes){
                if (conType.equals(rq.getType())) {
                    result.remove(rq); //Er is een ResourceType aanwezig wat niet mag. Verwijder uit lijst
                }
            }
        }

        return result;
    }


}
