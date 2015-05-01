package be.swop.groep11.main.exception;

/**
 * IllegalDependencyException die wordt gegooid wanneer er een ongeldige dependency is.
 */
public class IllegalDependencyException extends RuntimeException {
    public IllegalDependencyException(String message) {
        super(message);
    }
}
