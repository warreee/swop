package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Basis controller die alle methodes van de concrete controllers definieerd.
 */
public abstract class AbstractController {
    private final UserInterface userInterface;
    private ControllerStack controllerStack;

    public AbstractController(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    protected UserInterface getUserInterface() {
        return userInterface;
    }


    public void createTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

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

    public void logon() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    //TODO finish associatie AbstractController & ControllerStack Plus nieuwe Testen
    public void logOut() throws IllegalArgumentException  {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    public void delegateTask() {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    public void selectDelegatedTo() {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    public void performDelegations() {
        throw new IllegalArgumentException("Niet ondersteund");
    }

//    public void setControllerStack(ControllerStack controllerStack)throws IllegalArgumentException {
//        if (!canHaveAsControllerStack(controllerStack)) {
//            throw new IllegalArgumentException("Ongeldige controllerStack voor deze AbstractController");
//        }
//        this.controllerStack = controllerStack;
//    }
//
//    public boolean canHaveAsControllerStack(ControllerStack controllerStack) {
//        return controllerStack != null && getControllerStack() == null;
//    }
//
//    public ControllerStack getControllerStack(){
//        return this.controllerStack;
//    }



}
