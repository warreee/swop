package be.swop.groep11.main.resource;

import be.swop.groep11.main.util.ResourceSchedule;

/**
 * Stelt een interface voor die elke instantie van een resource type moet implementeren.
 */
public interface ResourceInstance {

    /**
     * Geeft de naam van de resource instantie terug.
     */
    String getName();

    /**
     * Geeft het resource type van de resource instantie.
     */
    AResourceType getResourceType();

    ResourceSchedule getResourceSchedule();//TODO return schedule

    /**
     * @return Waar indien getResourceSchedule() niet null terug geeft.
     */
    default boolean hasResourceSchedule(){
            return getResourceSchedule() != null;
    }
}
