package be.swop.groep11.main.exception;

import be.swop.groep11.main.resource.AResourceType;

/**
 * Created by Ronald on 9/04/2015.
 */
public class IllegalRequirementAmountException extends RuntimeException {
    private final AResourceType type;
    private final int amount;

    public IllegalRequirementAmountException(String message, AResourceType type, int amount) {
        super(message);
        this.type = type;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public AResourceType getType() {
        return type;
    }
}
