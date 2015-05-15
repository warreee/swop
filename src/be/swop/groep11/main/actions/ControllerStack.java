package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Klasse verantwoordelijk voor het bijhouden van ActionBehaviour's voor Action's, specifiek voor AbstractController's.
 * Extra verantwoordelijkheid is het bijhouden van de ControllerStack (execution stack).
 */
public class ControllerStack {

    // Houdt lijst van Controllers bij die "actief zijn". De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit. Soort van execution stack
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();

    private ActionProcedure invalidProcedure;


    public HashSet<Action> getAcceptableActions(AbstractController controller) {
        HashSet<Action> actions = new HashSet<>();
        HashMap<Action, ActionProcedure> actionProcedureHashMap = getControllerToActionMap().get(controller);
        actionProcedureHashMap.keySet().forEach(action -> actions.add(action));
        System.out.println(actions);
        return actions;
    }


    /**
     *  Constructor voor aanmaken van een ActionBehaviourMapping
     * @throws IllegalArgumentException
     *                          Gooi indien de gegeven invalidStrategy niet geinitialiseerd is.
     */
    public ControllerStack(ActionProcedure invalidProcedure) {
        this.invalidProcedure = invalidProcedure;
    }

    public void execute(Action action) {
        //step 1 determine action to ActionProcedure map based on current Active Controller.
        HashMap<Action, ActionProcedure> actionMap = getControllerToActionMap().get(getActiveController());
        //step 2 get the corresponding ActionProcedure
        ActionProcedure procedure = actionMap.getOrDefault(action, getInvalidProcedure());
        printStack("before");
        //Step 2 activate controller
        if (procedure.hasNewPreController()) {
            activateController(procedure.getNewPreController());
        }
        //step 4 uitvoeren
        printStack("during");
        procedure.perform();

        //Step 5 deactivateAfter
        if (procedure.toDeleteFromStack()) {
            deActivateController(getActiveController());
        }
        printStack("after");
    }

    private void printStack(String message) {
        StringBuilder sb = new StringBuilder("\n" + message + "\n");
        for (int i = controllerStack.size() -1 ; i>=0; i--) {
            AbstractController controller = controllerStack.get(i);
            sb.append(i + ". " + controller.getClass().getSimpleName() + "\n");
        }
        System.out.println(sb.toString());
    }

    /**
     * @return  De laatste toegevoegde AbstractController aan de ExecutionStack
     */
    public AbstractController getActiveController(){
        return controllerStack.getLast();
    }
    /**
     * Voeg de gegeven AbstractController toe aan de executionStack.
     * De laatste AbstractController op de stack is de actieve AbstractController.
     * @param abstractController    De toe te voegen AbstractController, dewelke dus ook de actieve wordt.
     */
    public void activateController(AbstractController abstractController){
        controllerStack.addLast(abstractController);
    }

    /**
     * Verwijder de gegeven AbstractController van de executionStack.
     * @param controller    De te verwijderen AbstractController
     */
    public void deActivateController(AbstractController controller){
        if(controller != null && controller.equals(controllerStack.peekLast())){
            //Gegeven controller is idd laatst toegevoegde controller.
            controllerStack.remove(controller);
        }
    }
    private boolean isValidActionProcedure(ActionProcedure actionProcedure) {
        return true;
    }

    public ActionProcedure getInvalidProcedure() {
        return invalidProcedure;
    }

    public void addActionProcedure(AbstractController topStackController, Action action, ActionProcedure actionProcedure) {
        if(!isValidActionProcedure(actionProcedure)) {
            throw new IllegalArgumentException("Invalid actionProcedure");
        }

        HashMap<Action,ActionProcedure> m = controllerToActionMap.getOrDefault(topStackController, new HashMap<>(getDefaultActionProcedureMap()));
        m.put(action, actionProcedure);
        controllerToActionMap.put(topStackController, m);

    }

    private HashMap<AbstractController, HashMap<Action, ActionProcedure>> controllerToActionMap = new HashMap<>();

    public void addDefaultActionProcedure(Action action, ActionProcedure actionProcedure) {
        if(!isValidActionProcedure(actionProcedure)) {
            throw new IllegalArgumentException("Invalid actionProcedure");
        }
        this.defaultActionProcedureMap.put(action, actionProcedure);
    }

    public HashMap<Action, ActionProcedure> getDefaultActionProcedureMap() {
        return defaultActionProcedureMap;
    }

    private HashMap<Action, ActionProcedure> defaultActionProcedureMap = new HashMap<>();

    private  HashMap<AbstractController, HashMap<Action, ActionProcedure>> getControllerToActionMap() {
        return controllerToActionMap;
    }
}