package be.swop.groep11.main.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ronald on 28/02/2015.
 */
public enum Command {

    SELECTPROJECT("select project",
            new CommandParam[]{CommandParam.NUMBERS},
            new String[]{"PID"}),
    EXIT("exit"),
    CANCEL("cancel"),
    HELP("help"),
    NEWPROJECTS("New Project"),
    ADVANCETIME("Advance Time"),
    UPDATETASK("Update Task"),
    NEWTASK("New Task"),
    SHOWPROJECTS("Show Projects");

    private Pattern commandPattern;
    private String commandString;
    private boolean hasParam;
    private int numParam;

    private CommandParam[] parameters;
    private String[] parameterNames;
    private HashMap<String,String> parameterToValue;

    Command(String commandString, CommandParam[] params, String[] parameterNames){
        setParameters(params, parameterNames);
        this.commandString = commandString;

        String regex = "((?i)("+ commandString +"))"; //Niet hoofdletter gevoelig
        if(hasParam){
            //TODO fix regex zodat paramaters meer kunnen zijn als opeenvolging van nummers of letters
            regex = regex + "(\\s(";
            for (int i = 0; i < params.length; i++) {
                regex = regex + params[i];
                regex = (i != params.length - 1)? regex + "(\\s+)" : regex ;
            }
            regex = regex + "))";
        }
        this.commandPattern = Pattern.compile(regex);
    }

    Command(String s) {
        this(s, new CommandParam[]{CommandParam.NONE},new String[]{""});
    }

    /**
     * Geeft een Hashmap terug met key-value paren die een parameter en zijn waarde voorstellen.
     */
    public HashMap<String,String> getParameters(){
        return this.parameterToValue;
    }

    /**
     * Geeft de waarde voor een parameter van een Command terug, gegeven de key.
     * @param key
     */
    public String getParameter(String key){
        return this.parameterToValue.get(key);
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
    public static Command getCommand(String input)throws IllegalCommandException {
        Command result = null;
        for (Command cmd : Command.values()) {
            Matcher matcher = cmd.commandPattern.matcher(input);

            if(matcher.matches()){
                if(cmd.hasParam){
                    int index = cmd.commandString.length();
                    for(int i = 0; i < cmd.numParam; i++){
                        CommandParam param = cmd.parameters[i];
                        String paramName = cmd.parameterNames[i];

                        Pattern pat = Pattern.compile(param.getRegex());
                        Matcher mat = pat.matcher(input);
                        if(mat.find(index)){
                            String p = mat.group();
                            index = index + p.length() + 1;//+1 voor spatie
                            cmd.setParam(paramName, p);
                        }else{
                            throw new IllegalCommandException(input);
                        }
                    }
                }
                result = cmd;
            }
        }
        if(result == null)
            throw new IllegalCommandException(input);
        return result;
    }

    private void setParam(String paramName ,String str){
        this.parameterToValue.replace(paramName, str);
    }

    private void setParameters(CommandParam[] commandParams,String[] paramNames){
        assert (commandParams.length == paramNames.length);
        if(commandParams[0]==CommandParam.NONE){
            this.hasParam = false;
            this.numParam  = 0;
        }else{
            this.hasParam = true;
            this.numParam  = commandParams.length;
        }
        this.parameterNames = paramNames;
        this.parameters = commandParams;

        this.parameterToValue = new HashMap<String, String>();
        for (String paramName :paramNames) {
            parameterToValue.put(paramName, "");
        }
    }

    @Override
    public String toString() {
        //TODO indien parameters aanwezig zijn moeten die ook vermeld worden!
        return commandString;
    }

    public String toVerboseString() {
        String result = "Command{" + commandPattern;
        result = (parameters.length > 0) ? result + ", parameters=" + Arrays.toString(parameters) : result;
        result = result +'}';
        return result;
    }
}