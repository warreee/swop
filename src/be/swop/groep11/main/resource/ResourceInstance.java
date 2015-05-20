package be.swop.groep11.main.resource;

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

}
