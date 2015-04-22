package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ui.ActionMapping;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.CommandStrategy;

/**
 * Created by Ronald on 17/04/2015.
 */
public abstract class AbstractController {

    private final UserInterface userInterface;
    private final ActionMapping actionMapping;

    public AbstractController(UserInterface userInterface) {
        this.userInterface = userInterface;
        this.actionMapping = userInterface.getActionMapping();//Temp?
    }


    protected UserInterface getUserInterface() {
        return userInterface;
    }

    //TODO andere exception, geeft problemen want illegalargument wordt gebruikt indien eind gebruiker verkeerde input geeft.
    public void createTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    //TODO implement planTask in juiste controller
    public void planTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    public void updateTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    public void advanceTime() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    public void showProjects() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    public void createProject() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    public void startSimulation() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    private CommandStrategy invalid = () -> getUserInterface().printMessage("Ongeldig commando, Abstract controller");

    /**
     * Geeft het gedrag indien een Command niet ondersteund wordt door de controller.
     */
    public CommandStrategy getInvalidStrategy() {
        return invalid;
    }

    protected void exitProgram() {
        System.out.println("want to exit,TODO IMPLEMENT");
    }

    /**
     * Set's this controller on top of stack in UI.
     */
    protected void activate() {
        actionMapping.activateController(this);
    }

    /**
     * Removes this controller from the stack in UI.
     */
    protected void deActivate() {
        actionMapping.deActivateController(this);
    }

}
