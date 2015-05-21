package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.exception.FailedConditionException;

/**
 * Created by Ronald on 14/05/2015.
 */
public class ActionProcedure {

    private ActionCondition condition;
    private boolean deleteFromStack;
    private AbstractController controller;
    private ActionBehaviour behaviour;

    public ActionProcedure(ActionBehaviour behaviour, ActionCondition condition) {
        this(null,behaviour, condition, false);
    }

    public ActionProcedure(AbstractController controller, ActionBehaviour behaviour, ActionCondition condition) {
        this(controller,behaviour, condition, true);
    }

    public ActionProcedure(AbstractController controller, ActionBehaviour behaviour, ActionCondition condition, boolean deleteFromStack) {
        // TODO check valid prameters
        this.controller = controller;
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

    public boolean test(){
        return condition.test();
    }

    public void execute(){
        behaviour.execute();
    }

    public boolean toDeleteFromStack() {
        return deleteFromStack;
    }

    public boolean hasNewPreController() {
        return getNewPreController() != null;
    }

    public AbstractController getNewPreController() {
        return controller;
    }
}
