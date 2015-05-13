package be.swop.groep11.main.resource;

/**
 * Stelt een resource voor met een naam en resource type.
 */
public class Resource implements ResourceInstance {

    /**
     * Constructor om een nieuwe resource aan te maken met een naam en een type, die 24/7 beschikbaar is.
     * @param name         De naam van de resource
     * @param resourceType Het resource type
     * @throws java.lang.IllegalArgumentException Ongeldige naam of type voor de resource
     */
    public Resource(String name, AResourceType resourceType) throws IllegalArgumentException {
        if (! isValidName(name))
            throw new IllegalArgumentException("Ongeldige naam voor de resource");
        if (resourceType == null)
            throw new IllegalArgumentException("Resource type mag niet null zijn");
        this.name = name;
        this.resourceType = resourceType;
    }

    @Override
    public ResourceSchedule getResourceSchedule() {
        return null;
    }

    private final AResourceType resourceType;

    /**
     * Geeft het resource type van deze resource.
     */
    @Override
    public AResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Geeft de naam van deze resource.
     */
    public String getName() {
        return name;
    }

    /**
     * Controleert of een naam een geldige naam is voor een resource.
     * @param name De te controleren naam
     * @return True als de naam niet null en niet leeg is.
     */
    private static boolean isValidName(String name) {
        return name != null && ! name.isEmpty();
    }

    private final String name;

}
