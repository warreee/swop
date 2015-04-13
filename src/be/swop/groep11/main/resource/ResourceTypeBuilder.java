package be.swop.groep11.main.resource;

import java.time.LocalTime;

/**
 * Created by Ronald on 13/04/2015.
 */
class ResourceTypeBuilder {
    private ResourceType type;

    public ResourceTypeBuilder(String typeName) throws IllegalArgumentException{
        newType(typeName);
    }

    //TODO private, update tests
    private void newType(String typeName)throws IllegalArgumentException{
        this.type = new ResourceType(typeName);
    }

    public void withDailyAvailability(DailyAvailability availability)throws IllegalArgumentException{
        this.type.setDailyAvailability(availability);
    }

    public void withDailyAvailability(LocalTime start,LocalTime end)throws IllegalArgumentException{
        withDailyAvailability(new DailyAvailability(start, end));
    }

    public void withConflictConstraint(IResourceType constrainingType)throws IllegalArgumentException{
        type.addConflictConstraint(constrainingType);
    }

    //Is dit oké?
    public void setConflictWithSelf(boolean conflict){
        if(conflict){
            withConflictConstraint(this.type);
        }
    }

    public void withRequirementConstraint(IResourceType constrainingType)throws IllegalArgumentException{
        type.addRequirementConstraint(constrainingType);
    }

    public void addResourceInstance(String instanceName){
        type.addResourceInstance(instanceName);
    }

    public IResourceType getResourceType(){
        return this.type;
    }

}
