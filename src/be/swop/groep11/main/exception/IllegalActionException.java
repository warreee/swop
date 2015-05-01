package be.swop.groep11.main.exception;

/**
 * Exception die wordt gegooid wanneer de gebruiker een ongeldige actie doet.
 */
public class IllegalActionException extends RuntimeException {

    public IllegalActionException(String message) {
        super(message);

    }

}
