package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 3/04/2015.
 */
public class ResourceRequirement {

    private final int amount;
    private final IResourceType type;

    public ResourceRequirement(IResourceType type, int amount) throws IllegalRequirementAmountException,IllegalArgumentException {
        if(!isValidType(type)){
            throw new IllegalArgumentException("Type mag niet null zijn.");
        }
        if(!isValidAmount(amount)){
            throw new IllegalArgumentException("Ongeldige hoeveelheid voor een requirement. Mag niet negatief zijn.");
        }
        this.type = type;
        this.amount = amount;
    }

    private boolean isValidType(IResourceType type) {
        return type != null;
    }

    private boolean isValidAmount(int amount) {
        return amount >= 0;
    }

    public int getAmount() {
        return amount;
    }

    public IResourceType getType() {
        return type;
    }

    //TODO override equals

}
