package be.swop.groep11.main.exception;

/**
 * Created by Ronald on 10/03/2015.
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
