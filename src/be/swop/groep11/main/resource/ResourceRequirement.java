package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ConflictConstraint;

import java.util.stream.Collectors;

/**
 * Created by Ronald on 3/04/2015.
 */
public class ResourceRequirement {

    private final int amount;
    private final ResourceType type;
    //TEMP class
    public ResourceRequirement(ResourceType type, int amount) throws IllegalArgumentException {
        if(!isValidAmount(amount)){
            throw new IllegalArgumentException("Ongeldige hoeveelheid voor een requirement.");
        }
        if(!isValidAmountForType(type,amount)){
            throw new IllegalArgumentException("Ongeldige hoeveelheid voor het gegeven ResourceType.");
        }
        //TODO isValidType & isValidAmount & isValidRequirement (of in ResourceType)
        this.type = type;
        this.amount = amount;
    }

    private boolean isValidAmountForType(ResourceType type, int amount) {
        type.getResourceTypeConstraints().stream()
                .filter(t -> t.getClass().equals(ConflictConstraint.class))
                .filter(c -> c.getConstrainingTypes().contains(type))
                .collect(Collectors.toList());

        return false;
    }


    private boolean isValidAmount(int amount) {
        return false;
    }

    public int getAmount() {
        return amount;
    }

    public ResourceType getType() {
        return type;
    }

    //TODO boolean containsType?


}
