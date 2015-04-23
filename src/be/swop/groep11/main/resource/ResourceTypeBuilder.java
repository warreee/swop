package be.swop.groep11.main.resource;

import java.time.LocalTime;

/**
 * Created by Ronald on 13/04/2015.
 */
public class ResourceTypeBuilder {
    private ResourceType type;

    public ResourceTypeBuilder(String typeName) throws IllegalArgumentException{
        newType(typeName);
    }

    private void newType(String typeName)throws IllegalArgumentException{
        if (!isValidResourceTypeName(typeName)) {
            throw new IllegalArgumentException("Ongeldige naam voor ResourceType");
        }
        this.type = new ResourceType(typeName);
    }

    public void withDailyAvailability(DailyAvailability availability)throws IllegalArgumentException{
        if (!isValidDailyAvailability(availability)) {
            throw new IllegalArgumentException("Ongeldige DailyAvailability");
        }
        this.type.setDailyAvailability(availability);
    }

    public void withDailyAvailability(LocalTime start,LocalTime end)throws IllegalArgumentException{
        withDailyAvailability(new DailyAvailability(start, end));
    }

    public void withConflictConstraint(IResourceType constrainingType)throws IllegalArgumentException{
        if (!isValidConstrainingType(constrainingType)) {
            throw new IllegalArgumentException("constrainingType mag niet null zijn");
        }
        type.addConflictConstraint(constrainingType);
    }

    //TODO Is dit oké? overbodig?
    public void setConflictWithSelf(boolean conflict){
        if(conflict){
            withConflictConstraint(this.type);
        }
    }

    public void withRequirementConstraint(IResourceType constrainingType)throws IllegalArgumentException{
        if (!isValidConstrainingType(constrainingType)) {
            throw new IllegalArgumentException("constrainingType mag niet null zijn");
        }
        type.addRequirementConstraint(constrainingType);
    }

    /**
     * Voeg aan het ResourceTpe een resource instance toe met de gegeven naam.
     * @param instanceName  De naam voor de nieuwe instance
     * @throws IllegalArgumentException Gooi exception indien instanceName null is of lege string.
     */
    public void addResourceInstance(String instanceName)throws IllegalArgumentException{
        if(!isValidResourceInstanceName(instanceName)) {
            throw new IllegalArgumentException("Ongeldige naam voor resource instance");
        }
        type.addResourceInstance(instanceName);
    }

    private boolean isValidResourceInstanceName(String instanceName) {
        return instanceName != null && !instanceName.isEmpty();
    }

    public IResourceType getResourceType(){
        return this.type;
    }

    private boolean isValidConstrainingType(IResourceType type) {
        return type != null;
    }
    private boolean isValidDailyAvailability(DailyAvailability availability) {
        return availability != null;
    }

    /**
     * Controleert of de gegeven naam wel correct is.
     *
     * @param name De naam die aan dit ResourceType gegeven wordt.
     * @return True als de naam niet null en niet leeg is.
     */
    private boolean isValidResourceTypeName(String name) {
        return name != null && !name.isEmpty();
    }

}
