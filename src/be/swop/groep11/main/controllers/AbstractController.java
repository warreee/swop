package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by Ronald on 17/04/2015.
 */
public abstract class AbstractController {

    private final UserInterface userInterface;
    private final ActionBehaviourMapping actionBehaviourMapping;

    public AbstractController(ActionBehaviourMapping actionBehaviourMapping) {
        this.actionBehaviourMapping = actionBehaviourMapping;
        this.userInterface = actionBehaviourMapping.getUserInterface();

    }
    protected UserInterface getUserInterface() {
        return userInterface;
    }

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

    /**
     * Set's this controller on top of stack in UI.
     */
    protected void activate() {
        actionBehaviourMapping.activateController(this);
    }

    /**
     * Removes this controller from the stack in UI.
     */
    protected void deActivate() {
        actionBehaviourMapping.deActivateController(this);
    }
}
