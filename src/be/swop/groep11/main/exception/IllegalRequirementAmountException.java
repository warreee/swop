package be.swop.groep11.main.exception;

import be.swop.groep11.main.resource.AResourceType;

/**
 * Exception die wordt gegooid wanneer de gebruiker een foute hoeveelheid invoerd.
 */
public class IllegalRequirementAmountException extends RuntimeException {
    private final AResourceType type;
    private final int amount;

    /**
     * Initialiseerd deze exception.
     * @param message Het foutbericht.
     * @param type Voor welk type deze exception van toepassing is.
     * @param amount De foute hoeveelheid.
     */
    public IllegalRequirementAmountException(String message, AResourceType type, int amount) {
        super(message);
        this.type = type;
        this.amount = amount;
    }

    /**
     * Haalt het bericht op dat beschrijft waarom deze exception gegooid is.
     * @return Het bericht.
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "\n" +"ResourceType: " + type.getName() + "\n"+ "Gevraagde hoeveelheid: " + amount + "\n" + "Beschikbare hoeveelheid: " + type.getResourceInstances().size();
    }

    /**
     * Haalt de hoeveelheid op die de gebruiker fout heeft ingevoerd.
     * @return De hoeveelheid
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Haalt het AResourceType op waarvoor deze exception gegooid is.
     * @return Het AResourceType.
     */
    public AResourceType getType() {
        return type;
    }
}
