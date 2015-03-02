package be.swop.groep11.main.commands;

/**
 * Created by Ronald on 28/02/2015.
 */
enum CommandParam {
    NONE(""),
    NUMBERS("(\\d+)"),
    LETTERS("([a-zA-Z]+)");

    private String regex;

    CommandParam(String regex){
        this.regex = regex;
    }

    public String getRegex(){
        return this.regex;
    }

    @Override
    public String toString() {
        return getRegex();
    }
}
