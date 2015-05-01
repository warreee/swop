package be.swop.groep11.main.exception;

/**
 * Exception die gegooid wordt wanneer er een lege lijst is.
 */
public class EmptyListException extends RuntimeException {
    public EmptyListException(String message) {
        super(message);
    }
}
