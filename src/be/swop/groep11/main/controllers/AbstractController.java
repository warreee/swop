package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.CommandStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ronald on 17/04/2015.
 */
public abstract class AbstractController {

    private final UserInterface userInterface;

    public AbstractController(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    protected UserInterface getUserInterface(){
        return userInterface;
    }
    //TODO andere exception, geeft problemen want illegalargument wordt gebruikt indien eind gebruiker verkeerde input geeft.
    public void createTask()throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");
    }
    //TODO implement planTask in juiste controller
    public void planTask()throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");
    }

    public void updateTask() throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");

    }
    public void advanceTime()throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");

    }
    public void showProjects() throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");

    }
    public void createProject() throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");

    }
    public void startSimulation() throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");
    }

    public void endSimulation() throws IllegalArgumentException{
        throw new IllegalArgumentException("Niet ondersteund");
    }


    private String format = "%-2s%s%-2s";
    private CommandStrategy invalid = () -> getUserInterface().printMessage("Ongeldig commando, Abstract controller");
    public CommandStrategy getInvalidStrategy(){
        //TODO fix hack?
        return this.invalid;
    }
//    public void showHelp() throws IllegalArgumentException{
//        ArrayList<Command> list = new ArrayList<>();
//
//        for(Map.Entry<Command,CommandStrategy> entry : getCommandStrategies().entrySet()){
//            if(!entry.getValue().equals(invalid)){
//                list.add(entry.getKey());
//            }
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for(Command cmd : list){
//            sb.append(String.format(format, "|", cmd.getCommandStr()," "));
//        }
//        sb.append("|");
//        getUserInterface().printMessage(sb.toString());
//    }

    protected void exitProgram() {
        System.out.println("want to exit,TODO IMPLEMENT");
    }

    /**
     * Set's this controller on top of stack in UI.
     */
    protected void activate(){
        getUserInterface().addControllerToStack(this);
    }

    /**
     * Removes this controller from the stack in UI.
     */
    protected void deActivate(){
        getUserInterface().removeControllerFromStack(this);
    }


    public HashMap<Command,CommandStrategy> getCommandStrategies(){
        HashMap<Command,CommandStrategy> map = new HashMap<>();
        //Alles standaard invalid strategy gedrag
        ArrayList<Command> list = new ArrayList<>(Arrays.asList(Command.values()));
        for(Command cmd : list){
            map.put(cmd,invalid);
        }
        map.put(Command.EXIT, this::exitProgram);
        map.put(Command.INVALIDCOMMAND, invalid);
        map.put(Command.HELP, () -> getUserInterface().showHelp(this));

        return map;
    }
}
