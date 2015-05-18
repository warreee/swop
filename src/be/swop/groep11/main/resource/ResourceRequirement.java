package be.swop.groep11.main.resource;

import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;

import java.util.List;

/**
 * Een klasse die een ResourceRequirement voorstelt.
 */
public class ResourceRequirement {
    /**
     * De hoeveelheid ResourceInstances er required zijn.
     */
    private final int amount;
    /**
     * Het type voor required ResourceInstances.
     */
    private final AResourceType type;

    /**
     * Constructor voor een nieuwe ResourceRequirement met een IResourceType en een hoeveelheid
     * @param type      Het IResourceType voor deze ResourceRequirement
     * @param amount    De aangevraagde hoeveelheid
     * @throws IllegalRequirementAmountException
     *                  Gooi indien het type een conflictConstraint op zichzelf heeft,
     *                  en de gevraagde hoeveelheid groter is dan 1.
     *                  Indien de gevraagde hoeveelheid groter is als het aantal ResourceInstances dat er zijn voor het
     *                  gegeven type.
     * @throws IllegalArgumentException
     *                  Gooi indien type == null
     */
    //TODO: moeten we de ResourceRepository ook bijhouden of niet?
    public ResourceRequirement(ResourceRepository resourceRepository, AResourceType type, int amount)
            throws IllegalRequirementAmountException,IllegalArgumentException {
        if(!isValidType(type)){
            throw new IllegalArgumentException("Type mag niet null zijn.");
        }
        if(!isValidAmountFor(resourceRepository, type, amount)){
            throw new IllegalRequirementAmountException("Ongeldige hoeveelheid voor het gegeven ResourceType.",type,amount);
        }
        this.type = type;
        this.amount = amount;
    }

    /**
     * Controleer of een type geldig is.
     * @param type  Het te controleren IResourceType
     * @return      Waar indien type != null
     *              Anders niet waar.
     */
    private boolean isValidType(AResourceType type) {
        return type != null;
    }

    /**
     * @return Hoeveel ResourceInstances er required zijn voor deze requirement.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return Het IResourceType van de required ResourceInstances.
     */
    public AResourceType getType() {
        return type;
    }

    /**
     * Controleer of de gegeven amount geldig is voor het gegeven IResourceType
     * @param type      Het type waarvoor er gecontroleerd wordt of het een geldige hoeveelheid is.
     * @param amount    De te controleren hoeveelheid
     * @return          Waar indien type een ConflictConstraint met zichzelf heeft en amount geldig is voor die
     *                  ConflictConstraint (amount <= 1). En amount <= de hoeveelheid ResourceInstances een type bevat.
     *                  Waar indien er geen ConflictConstraint met zichzelf is, en amount > 0 en
     *                  amount <= de hoeveelheid ResourceInstances een type bevat.
     *                  Anders niet waar.
     */
    private boolean isValidAmountFor(ResourceRepository resourceRepository, AResourceType type, int amount) {
        if( type.hasConstraintFor(type)){//constraint op zichzelf
            ResourceTypeConstraint constraintB = type.getConstraintFor(type);
            return constraintB.isAcceptableAmount(type,amount) && amount <= resourceRepository.getResources(type).size();
        }
        List<ResourceInstance> instances = resourceRepository.getResources(type);
        return amount > 0 && amount <= resourceRepository.getResources(type).size();
    }

    @Override
    public String toString() {
        return "ResourceRequirement{" +
                "amount=" + amount +
                ", type=" + type +
                '}';
    }
}
