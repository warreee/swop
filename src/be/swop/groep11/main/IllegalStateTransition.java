package be.swop.groep11.main;

/**
 * Created by warreee on 4/8/15.
 */
public class IllegalStateTransition extends RuntimeException {

    public IllegalStateTransition(String message) {
        super(message);
    }
}
