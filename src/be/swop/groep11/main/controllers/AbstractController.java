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

    public void showHelp() throws IllegalArgumentException{

        throw new IllegalArgumentException("Niet ondersteund");

    }

    protected void exitProgram() {
        System.out.println("want to exit");
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

    private CommandStrategy invalid = () -> getUserInterface().printMessage("Ongeldig commando, Abstract controller");

    public HashMap<Command,CommandStrategy> getCommandStrategies(){
        HashMap<Command,CommandStrategy> map = new HashMap<>();
        map.put(Command.EXIT, this::exitProgram);
        map.put(Command.INVALIDCOMMAND, invalid);

        ArrayList<Command> list = new ArrayList<>(Arrays.asList(Command.values()));
        list.remove(Command.EXIT);
        list.remove(Command.INVALIDCOMMAND);
        for(Command cmd : list){
            map.put(cmd,map.get(Command.INVALIDCOMMAND));
        }
        return map;
    }
}
