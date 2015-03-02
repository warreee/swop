package be.swop.groep11.main.commands;

/**
 * Created by Ronald on 28/02/2015.
 */
public class IllegalCommandException extends RuntimeException {

    /**
     * Initialize this new Illegal command exception with given input.
     * @param input
     *          The invalid input for this new illegal command exception.
     */
    public IllegalCommandException(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }
    private final String input;
}
