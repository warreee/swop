package be.swop.groep11.main.ui.commands;

import be.swop.groep11.main.ui.IllegalInputException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ronald on 28/02/2015.
 */
public enum Param {

    NUMBERS("(\\d+)"),
    LETTERS("([a-zA-Z]+)");

    private Pattern pattern;

    Param(String regex){
        this.pattern = Pattern.compile(regex);
    }

    public static Param getParameter(String input) throws IllegalInputException{
        Param result = null;
        for (Param param : Param.values()) {
            Matcher matcher = param.pattern.matcher(input);

            if (matcher.matches()) {
                result = param;
            }
        }
        if (result == null) {
            throw new IllegalInputException("Something went wrong in commandParam");
        }

        return result;
    }


}
