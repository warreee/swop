package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.User;
import be.swop.groep11.main.util.ResourceSchedule;

/**
 * Stelt een developer voor met een naam en resource type.
 */
public class Developer extends User implements ResourceInstance {

    /**
     * Constructor om een nieuwe developer aan te maken met een naam en een resource type.
     * @param name         Naam van de developer
     * @param resourceType Resource type van de developer
     * @throws java.lang.IllegalArgumentException Ongeldige resource type
     */
    public Developer(String name, AResourceType resourceType) throws IllegalArgumentException {
        super(name);
        if (resourceType == null)
            throw new IllegalArgumentException("Resource type mag niet null zijn");
        this.resourceType = resourceType;
    }

    @Override
    public ResourceSchedule getResourceSchedule() {
        return null;
    }

    /**
     * Haalt het DeveloperType op van deze Developer.
     * @return
     */
    @Override
    public AResourceType getResourceType() {
        return resourceType;
    }

    private final AResourceType resourceType;

    /**
     * Geeft aan of een user een developer is.
     * @return true
     */
    @Override
    public boolean isDeveloper() {
        return true;
    }
}
