package be.swop.groep11.main.exception;

/**
 * Exception die gegooid wordt indien de gebruiker "cancel" ingeeft.
 */
public class CancelException extends RuntimeException {
    /**
     * Constructor voor een nieuwe CancelException.
     * @param message   Het bericht mee te geven aan de nieuwe CancelException.
     */
    public CancelException(String message) {
        super(message);
    }
}
