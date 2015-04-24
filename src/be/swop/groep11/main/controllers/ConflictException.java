package be.swop.groep11.main.controllers;

/**
 * Created by Arne De Brabandere_3 on 24/04/2015.
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
