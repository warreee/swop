package be.swop.groep11.main;

/**
 * Created by Ronald on 3/04/2015.
 */
public class ResourceTypeRequirement {

    private final int amount;
    private final ResourceType type;
    //TEMP class
    public ResourceTypeRequirement(ResourceType type, int amount) {
        //TODO isValidType & isValidAmount & isValidRequirement (of in ResourceType)
        this.type = type;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public ResourceType getType() {
        return type;
    }

    //TODO boolean containsType?


}
