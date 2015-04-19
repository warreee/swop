package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ui.UserInterface;

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
}
