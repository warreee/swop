package be.swop.groep11.main.exception;

/**
 * Exception die gegooid wordt wanneer een conditie faalt.
 */
public class FailedConditionException extends RuntimeException {
    public FailedConditionException(String message) {
        super(message);
    }
}
