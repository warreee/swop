package be.swop.groep11.main.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enumeratie van Action, dewelke mogelijke commando's zijn die de gebruiker kan ingeven.
 **/
public enum Action {

    EXIT("exit"),
    CANCEL("cancel"),
    HELP("help"),
    CREATEPROJECT("Create Project"),
    ADVANCETIME("Advance Time"),
    UPDATETASK("Update Task"),
    CREATETASK("Create Task"),
    SHOWPROJECTS("Show Projects"),
    PLANTASK("Plan Task"),
    STARTSIMULATION("Start Simulation"),
    REALIZESIMULATION("Realize simulation"),
    INVALIDACTION("");

    /**
     * Constructor voor een Action, met een stringRepresentatie die.
     * @param actionString  De stringRepresentatie voor de nieuwe Action.
     */
    Action(String actionString){
        this.action = actionString;
        String regex = "((?i)(\\b"+ actionString +"\\b))"; //Niet hoofdletter gevoelig
        this.pattern = Pattern.compile(regex);
    }
    private final String action;
    private final Pattern pattern;

    /**
     * @return  Geef de string representatie van een Action terug.
     */
    public String getActionStr(){
        return this.action;
    }

    /**
     * Geeft een Action terug corresponderend met de gebruikers invoer.
     * @param input     De gebruikers invoer
     * @return          De Action met een string Representatie die overeenkomt met de gebruikers invoer.
     *                  Indien geen overeenkomst geeft men de INVALIDACTION terug.
     */
    public static Action getAction(String input){
        Action result = INVALIDACTION;
        for (Action action : Action.values()) {
            Matcher matcher = action.pattern.matcher(input);
            if(matcher.matches()){
                result = action;
            }
        }
        return result;
    }

    /**
     * Controleer of de gebruikersInput overeenkomt met Cancel.
     * @param input De gebruikersInput
     * @return      Waar indien de gebruikersInput overeenkomt met de CANCEL string representatie.
     */
    public static boolean checkCancel(String input){
        Matcher matcher = CANCEL.pattern.matcher(input);
        return matcher.matches();
    }
}