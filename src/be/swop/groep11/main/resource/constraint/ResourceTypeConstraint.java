package be.swop.groep11.main.resource.constraint;

import be.swop.groep11.main.resource.ResourceType;
import be.swop.groep11.main.resource.ResourceRequirement;

import java.util.List;

/**
 * Created by Ronald on 3/04/2015.
 */
public abstract class ResourceTypeConstraint {

    /**
     * TODO werken met SET ipv List om ervoor te zorgen dat de constrainingTypes uniek zijn?
     */

    /**
     * Constructor voor ResourceTypeConstraint
     * @param owner    Het ResourceType waarvoor deze constraint van toepassing is.
     *
     * @throws  IllegalArgumentException    Indien ownerType niet geïnitialiseerd is of indien de
     */
    public ResourceTypeConstraint(ResourceType owner,List<ResourceType> constrainingTypes) throws IllegalArgumentException{
        if(!isValidResourceType(owner)){
            throw new IllegalArgumentException("Ongeldige ownerType ResourceType");
        }
        this.ownerType = owner;
        if(!canHaveAsConstrainingTypes(constrainingTypes)){
            throw new IllegalArgumentException("Ongeldige constrainingTypes");
        }
        this.constrainingTypes = constrainingTypes;
    }

    private boolean isValidResourceType(ResourceType onType) {
        return onType != null;
    }

    /**
     * Check of de gegeven lijst van ResourceTypes een correcte lijst is voor deze nieuwe ResourceTypeConstraint.
     * @param constrainingTypes     De gegeven lijst van ResourceType
     */
    protected abstract boolean canHaveAsConstrainingTypes(List<ResourceType> constrainingTypes);

    /**
     * @return  Geeft de lijst van ResourceType die een voorwaardelijke rol vervullen.
     */
    public List<ResourceType> getConstrainingTypes() {
        return constrainingTypes;
    }
    private final List<ResourceType> constrainingTypes;

    /**
     * @return  Geeft het ResourceType waarop deze constraint van toepassing is.
     */
    public ResourceType getOwnerType() {
        return ownerType;
    }
    private final ResourceType ownerType;

    /**
     * Controleer dat deze ResourceTypeConstraint voldoet a.d.h.v. gegeven lijst ResourceTypeRequirement
     *
     * @param requirements   De lijst ResourceTypeRequirements
     */
    public abstract boolean isSatisfied(List<ResourceRequirement> requirements);

    /**
     * Genereer voor deze Constraint een lijst ResourceTypeRequirements gebaseerd op de gegeven lijst requirements,
     * dewelke geen tegenstrijdigheden bevat.
     * @param requirements      Lijst van ResourceTypeRequirement
     */
    public abstract List<ResourceRequirement> resolve(List<ResourceRequirement> requirements);

    /**
     * Controleer of deze ConflictConstraint tegenstrijdig is met de gegeven ResourceTypeConstraint.
     * @param otherConstraint   De andere ResourceTypeConstraint om te controleren
     */
    public abstract boolean isValidOtherConstraint(ResourceTypeConstraint otherConstraint);
}
