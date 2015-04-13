package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;

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
        if(!isValidAmountFor(type, amount)){
            throw new IllegalRequirementAmountException("Ongeldige hoeveelheid voor het gegeven ResourceType.",type,amount);
        }
        this.type = type;
        this.amount = amount;
    }

    private boolean isValidType(IResourceType type) {
        return type != null;
    }

    public int getAmount() {
        return amount;
    }

    public IResourceType getType() {
        return type;
    }

    private boolean isValidAmountFor(IResourceType type, int amount) {
        if( type.hasConstraintFor(type)){
            ResourceTypeConstraint constraintB = type.getConstraintFor(type);
            return constraintB.isAcceptableAmount(amount);
        }

        return amount > 0 && amount <= type.amountOfInstances();
    }

    //TODO override equals?

}
