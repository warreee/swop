package be.swop.groep11.main.resource;

public class ResourceType extends AResourceType {

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven parameters.
     *
     * @param typeName De naam van deze ResourceType
     */
    public ResourceType(String typeName) throws IllegalArgumentException {
        super(typeName);
    }

    @Override
    protected void addResourceInstance(String name) throws IllegalArgumentException {
        Resource resource = new Resource(name,this);
        addResourceInstance(resource);
    }


}
