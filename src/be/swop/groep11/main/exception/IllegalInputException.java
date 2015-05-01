package be.swop.groep11.main.exception;

/**
 * Exception die wordt gegooid wanneer er foute input wordt gegeven door de gebruiker.
 */
public class IllegalInputException extends RuntimeException {
    public IllegalInputException(String message) {
        super(message);
    }
}
