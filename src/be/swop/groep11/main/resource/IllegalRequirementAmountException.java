package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 9/04/2015.
 */
public class IllegalRequirementAmountException extends RuntimeException {
    private final IResourceType type;
    private final int amount;

    public IllegalRequirementAmountException(String message, IResourceType type, int amount) {
        super(message);
        this.type = type;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public IResourceType getType() {
        return type;
    }
}
