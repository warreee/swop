package be.swop.groep11.main.exception;

/**
 * Exception die gegooid wordt wanneer er ongeldige overgang van status plaats vind.
 */
public class IllegalStateTransitionException extends RuntimeException {

    public IllegalStateTransitionException(String message) {
        super(message);
    }
}
