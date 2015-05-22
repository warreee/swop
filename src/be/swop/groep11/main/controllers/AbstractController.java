package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ui.UserInterface;

/**
 * Basis controller die alle methodes van de concrete controllers definieerd.
 */
public abstract class AbstractController {
    private final UserInterface userInterface;

    /**
     * Maakt een nieuwe abstract controller aan
     * @param userInterface
     */
    public AbstractController(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    /**
     * Haalt de userinterface variabele op.
     * @return
     */
    protected UserInterface getUserInterface() {
        return userInterface;
    }

    /**
     * Voert de usecase create task uit.
     * @throws IllegalArgumentException
     */
    public void createTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Voert de usecase plan task uit.
     * @throws IllegalArgumentException
     */
    public void planTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Voert de usecase update task uit.
     * @throws IllegalArgumentException
     */
    public void updateTask() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    /**
     * Voert de use case advance time uit.
     * @throws IllegalArgumentException
     */
    public void advanceTime() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    /**
     * Voert de usecase show projects uit.
     * @throws IllegalArgumentException
     */
    public void showProjects() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    /**
     * Voert de usecase create project uit.
     * @throws IllegalArgumentException
     */
    public void createProject() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");

    }

    /**
     * Voer de use case simulation uit.
     * @throws IllegalArgumentException
     */
    public void startSimulation() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Voert de use case logon uit.
     * @throws IllegalArgumentException
     */
    public void logon() throws IllegalArgumentException {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Voert de usecase logout uit.
     * @throws IllegalArgumentException
     */
    public void logOut() throws IllegalArgumentException  {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Voert de usecase delegate task uit.
     */
    public void delegateTask() {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Laat de gebruiker een branch office kiezen om naar te delegeren.
     */
    public void selectDelegatedTo() {
        throw new IllegalArgumentException("Niet ondersteund");
    }

    /**
     * Voert de delegaties uit voor de branch offices.
     */
    public void performDelegations() {
        throw new IllegalArgumentException("Niet ondersteund");
    }


}
