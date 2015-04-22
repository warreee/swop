package be.swop.groep11.main.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ronald on 28/02/2015.
 */
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
    ENDSIMULATION("End Simulation"),
    INVALIDCOMMAND("");

    Action(String action){
        this.action = action;
        String regex = "((?i)(\\b"+ action +"\\b))"; //Niet hoofdletter gevoelig
        this.pattern = Pattern.compile(regex);
    }
    private final String action;
    private final Pattern pattern;

    public String getCommandStr(){
        return this.action;
    }

    /**
     * Geeft een Command terug corresponderend met de gebruikers invoer.
     * @effect  Indien action parameters verwacht worden deze opgeslagen in de hashmap.
     * @param input
     * @return
     * @throws IllegalActionException
     *         Indien er geen corresponderende Command is voor de gebruikers invoer.
     *         Of indien er onvoldoende of verkeerde parameters gegeven zijn door de gebruiker.
     *
     */
    public static Action getInput(String input)throws IllegalActionException {
        Action result = null;
        for (Action action : Action.values()) {
            Matcher matcher = action.pattern.matcher(input);
            if(matcher.matches()){
                result = action;
            }
        }
        if(result == null){
            return INVALIDCOMMAND;
        }
        return result;
    }

    public static boolean checkCancel(String input){
        Matcher matcher = CANCEL.pattern.matcher(input);
        return matcher.matches();
    }
}