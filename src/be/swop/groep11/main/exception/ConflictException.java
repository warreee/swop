package be.swop.groep11.main.exception;

/**
 * Exception die gegooid wordt indien taken conflicteren.
 */
public class ConflictException extends RuntimeException {
    /**
     * Constructor voor een nieuwe ConflictException.
     * @param message   Het bericht mee te geven aan de nieuwe ConflictException.
     */
    public ConflictException(String message) {
        super(message);
    }
}
