package be.swop.groep11.main.ui.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ronald on 28/02/2015.
 */
public enum Command {

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
    ENDSIMULATION("End Simulation"),
    INVALIDCOMMAND("");

    Command(String command){
        this.command = command;
        String regex = "((?i)(\\b"+ command +"\\b))"; //Niet hoofdletter gevoelig
        this.pattern = Pattern.compile(regex);
    }
    private final String command;
    private final Pattern pattern;

    public String getCommandStr(){
        return this.command;
    }

    /**
     * Geeft een Command terug corresponderend met de gebruikers invoer.
     * @effect  Indien command parameters verwacht worden deze opgeslagen in de hashmap.
     * @param input
     * @return
     * @throws IllegalCommandException
     *         Indien er geen corresponderende Command is voor de gebruikers invoer.
     *         Of indien er onvoldoende of verkeerde parameters gegeven zijn door de gebruiker.
     *
     */
    public static Command getInput(String input)throws IllegalCommandException {
        Command result = null;
        for (Command com : Command.values()) {
            Matcher matcher = com.pattern.matcher(input);

            if(matcher.matches()){
                result = com;
            }
        }
        if(result == null){
            return INVALIDCOMMAND;
//            throw new IllegalCommandException("Ongeldig commando");
        }
        return result;
    }

    public static boolean checkCancel(String input){
        Matcher matcher = CANCEL.pattern.matcher(input);
        return matcher.matches();
    }
}