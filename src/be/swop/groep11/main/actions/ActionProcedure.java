package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.exception.FailedConditionException;

/**
 * Klasse die een actionProcedure voorstel, het bestaat het het gedrag dat wordt uitgevoerd en de voorwaarde om dit gedrag mogen uit te voeren.
 */
public class ActionProcedure {

    private ProcedureCondition condition;
    private boolean deleteFromStack;
    private AbstractController newController;
    private ActionBehaviour behaviour;

    /**
     * Constructer die defaultconstructor oproept.
     * @param behaviour het gedrag van deze action procedrue dat moet worden uitgevoerd
     */
    public ActionProcedure(ActionBehaviour behaviour) {
        this(null,behaviour, () -> true, false);
    }

    /**
     * Constructer die defaultconstructor oproept.
     * @param behaviour het gedrag van deze action procedrue dat moet worden uitgevoerd
     * @param condition de voorwaardie voldoen moet zijn voordat de behaviour mag worden uitgevoerd.
     */
    public ActionProcedure(ActionBehaviour behaviour, ProcedureCondition condition) {
        this(null,behaviour, condition, false);
    }

    /**
     * Constructer die defaultconstructor oproept.
     * @param newController de controller die bij op de stack moet komen
     * @param behaviour het gedrag van deze action procedrue dat moet worden uitgevoerd
     * @param condition de voorwaardie voldoen moet zijn voordat de behaviour mag worden uitgevoerd.
     */
    public ActionProcedure(AbstractController newController, ActionBehaviour behaviour, ProcedureCondition condition) {
        this(newController,behaviour, condition, true);
    }

    /**
     * Defaultconstructor voor een actionprocedure.
     * @param newController de controller die bij op de stack moet komen
     * @param behaviour het gedrag van deze action procedrue dat moet worden uitgevoerd
     * @param condition de voorwaardie voldoen moet zijn voordat de behaviour mag worden uitgevoerd.
     * @param deleteFromStack specifieert of de controller na de uitvoering ervan op de stak moet blijven staan.
     */
    public ActionProcedure(AbstractController newController, ActionBehaviour behaviour, ProcedureCondition condition, boolean deleteFromStack) {
        // TODO check valid parameters
        this.newController = newController;
        this.behaviour = behaviour;
        this.condition = condition;
        this.deleteFromStack = deleteFromStack;
    }

    /**
     * Voer de ActionProcedure uit.
     * @throws FailedConditionException
     */
    public void perform() throws FailedConditionException {
        if (!condition.test()) {
            throw new FailedConditionException("Condition test failed");
        } else {
            behaviour.execute();
        }

    }

    /**
     * Kijkt na of de controller van de stack moet worden verwijderd
     * @return true indien deleteFromStack && getNewController()!= null
     */
    public boolean hasToDeleteControllerFromStack() {
        return deleteFromStack && getNewController()!= null;
    }

    /**
     * Kijkt na of de nieuwe controller moeten worden geactiveerd
     * @return getNewController() != null
     */
    public boolean hasToActivateNewController() {
        return getNewController() != null;
    }

    /**
     * Geeft de AbstractController van de ActionProcedure terug
     * @return de AbstractController
     */
    public AbstractController getNewController() {
        return newController;
    }
}
