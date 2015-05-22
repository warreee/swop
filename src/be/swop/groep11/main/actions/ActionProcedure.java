package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.exception.FailedConditionException;

/**
 * Created by Ronald on 14/05/2015.
 */
public class ActionProcedure {

    private ProcedureCondition condition;
    private boolean deleteFromStack;
    private AbstractController newController;
    private ActionBehaviour behaviour;

    public ActionProcedure(ActionBehaviour behaviour) {
        this(null,behaviour, () -> true, false);
    }

    public ActionProcedure(ActionBehaviour behaviour, ProcedureCondition condition) {
        this(null,behaviour, condition, false);
    }

    public ActionProcedure(AbstractController newController, ActionBehaviour behaviour, ProcedureCondition condition) {
        this(newController,behaviour, condition, true);
    }

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

    public boolean hasToDeleteControllerFromStack() {
        return deleteFromStack && getNewController()!= null;
    }

    public boolean hasToActivateNewController() {
        return getNewController() != null;
    }

    public AbstractController getNewController() {
        return newController;
    }
}
