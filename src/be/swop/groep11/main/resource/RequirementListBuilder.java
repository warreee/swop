package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 9/04/2015.
 */
public class RequirementListBuilder {

    public RequirementListBuilder() {
    }

    public void addRequirement(ResourceType type,int amount){
        new ResourceRequirement(type,amount);

    }

    public void removeRequirement(ResourceType type){
        //TODO wat indien met de hoeveelheid wil veranderen?
    }

    public boolean containsRequirement(ResourceType type){
        return false;
    }

}
