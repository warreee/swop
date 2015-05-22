package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.exception.FailedConditionException;
import be.swop.groep11.main.exception.InterruptedAProcedureException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Klasse verantwoordelijk voor het bijhouden van ActionBehaviour's voor Action's, specifiek voor AbstractController's.
 * Extra verantwoordelijkheid is het bijhouden van de ControllerStack (execution stack).
 */
public class ControllerStack {


    /**
     *  Constructor voor aanmaken van een ControllerStack
     */
    public ControllerStack() {
        setInvalidActionProcedure(new ActionProcedure(() -> {}));
    }

    /**
     * Vraag de verzameling Actions die voor de gegeven AbstractController een corresponderende ActionProcedure bezitten die uitvoerbaar zijn.
     * @param controller    De gegeven AbstractController
     * @return              Een HashSet<Action> die alle Actions bevat die een uitvoerbare procedure bezitten voor de gegeven controller.
     */
    public HashSet<Action> getAcceptableActions(AbstractController controller) {
        HashSet<Action> actions = new HashSet<>();
        HashMap<Action, ActionProcedure> actionProcedureHashMap = getControllerToActionMap().get(controller);
        actionProcedureHashMap.keySet().forEach(action -> actions.add(action));
        return actions;
    }

    /**
     * Voor de gegeven Action, vraag de ActionProcedure op, en voer deze uit.
     * @param action    De action waarvoor een ActionProcedure wordt uitgevoerd indien er één is.
     *                  Anders wordt getInvalidProcedure() uitgevoerd.
     */
    public void executeAction(Action action) {
        //Step 0, backup current ControllerStack
        ControllerStackMemento backup = getBackup();

        try {
            //step 1 determine action to ActionProcedure map based on current Active Controller.
            HashMap<Action, ActionProcedure> actionMap = getControllerToActionMap().getOrDefault(getActiveController(), new HashMap<>());
            //step 2 get the corresponding ActionProcedure
            ActionProcedure procedure = actionMap.getOrDefault(action, getInvalidProcedure());
            //Step 3 activate controller
//            printStack("before");
            if (procedure.hasToActivateNewController()) {
                activateController(procedure.getNewController());
            }
            //step 4 uitvoeren
//            printStack("during");
            procedure.perform();
            //Step 5 deactivateAfter
            if (procedure.hasToDeleteControllerFromStack()) {
                deActivateController(getActiveController());
            }
//            printStack("after");
        } catch (InterruptedAProcedureException e) {
            restoreBackup(backup);
//            printStack("restored");
        }catch (FailedConditionException e) {
            restoreBackup(backup);
//            printStack("restored");
            throw e;
        }
    }

    private ControllerStackMemento getBackup() {
        return new ControllerStackMemento(controllerStack);
    }

    private void restoreBackup(ControllerStackMemento memento) {
        this.controllerStack = memento.getControllerStack();
    }

//    private void printStack(String message) {
//        StringBuilder sb = new StringBuilder(message + "\n");
//        for (int i = controllerStack.size() -1 ; i>=0; i--) {
//            AbstractController controller = controllerStack.get(i);
//            sb.append(i + ". " + controller.getClass().getSimpleName() + "\n");
//        }
////      System.out.println(sb.toString());
//    }

    /**
     * @return  De laatste toegevoegde AbstractController aan de ExecutionStack
     */
    public AbstractController getActiveController() throws NoSuchElementException {
        return controllerStack.getLast();
    }

    //TODO add setFirstController method
    //TODO make activate & deActivate private

    /**
     * Voeg de gegeven AbstractController toe aan deze controllerStack.
     * @param abstractController    De toe te voegen AbstractController.
     */
    public void activateController(AbstractController abstractController) {
        if (!controllerStack.contains(abstractController)) {
            controllerStack.addLast(abstractController);
        }
    }

    /**
     * Verwijder de gegeven AbstractController van deze controllerStack.
     * @param controller    De te verwijderen AbstractController
     */
    public void deActivateController(AbstractController controller){
        if(controller != null && controller.equals(controllerStack.peekLast())){
            //Gegeven controller is idd laatst toegevoegde controller.
            controllerStack.remove(controller);
        }
    }

    // Houdt lijst van Controllers bij die "actief zijn". De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit. Soort van execution stack
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();

    /**
     * Controleer of de gegeven ActionProcedure een geldige ActionProcedure is.
     * @param actionProcedure   De te controleren ActionProcedure
     * @return                  Waar indien actionProcedure geïnitialiseerd is, anders niet waar.
     */
    private boolean isValidActionProcedure(ActionProcedure actionProcedure) {
        return actionProcedure != null;
    }

    /**
     * Geeft de ActionProcedure die wordt uitgevoerd indien men een Action heeft proberen uitvoeren waarvoor er geen corresponderende ActionProcedure is.
     */
    public ActionProcedure getInvalidProcedure() {
        return invalidProcedure;
    }

    private ActionProcedure invalidProcedure;

    /**
     * Toevoegen van een ActionProcedure aan deze ControllerStack.
     * @param topStackController    De AbstractController waarvoor deze ActionProcedure uitvoerbaar moet zijn.
     * @param action                De Action waarmee de ActionProcedure wordt opgeroepen.
     * @param actionProcedure       De toe te voegen ActionProcedure.
     * @throws IllegalArgumentException Gooi indien de gegeven actionProcedure niet geïnitialiseerd is.
     */
    public void addActionProcedure(AbstractController topStackController, Action action, ActionProcedure actionProcedure) throws IllegalArgumentException{
        if(!isValidActionProcedure(actionProcedure)) {
            throw new IllegalArgumentException("Invalid actionProcedure");
        }
        HashMap<Action,ActionProcedure> actionToProcedureMap = controllerToActionMap.getOrDefault(topStackController, new HashMap<>(getDefaultActionProcedureMap()));

        actionToProcedureMap.put(action, actionProcedure);
        controllerToActionMap.put(topStackController, actionToProcedureMap);
    }

    private HashMap<AbstractController, HashMap<Action, ActionProcedure>> controllerToActionMap = new HashMap<>();

    /**
     * Toevoegen van een Default ActionProcedure, die voor alle AbstractControllers uitvoerbaar is.
     * @param action            De Action waarmee de ActionProcedure wordt opgeroepen.
     * @param actionProcedure    De toe te voegen ActionProcedure.
     * @throws IllegalArgumentException Gooi indien de gegeven actionProcedure niet geïnitialiseerd is.
     */
    public void addDefaultActionProcedure(Action action, ActionProcedure actionProcedure) {
        if(!isValidActionProcedure(actionProcedure) || action == null) {
            throw new IllegalArgumentException("Invalid actionProcedure");
        }
        this.defaultActionProcedureMap.put(action, actionProcedure);
    }

    /**
     * Geeft de DefaultActionProcedureMap terug.
     */
    private HashMap<Action, ActionProcedure> getDefaultActionProcedureMap() {
        return defaultActionProcedureMap;
    }

    private HashMap<Action, ActionProcedure> defaultActionProcedureMap = new HashMap<>();

    /**
     * Geeft de ControllerToActionMap terug.
     */
    private  HashMap<AbstractController, HashMap<Action, ActionProcedure>> getControllerToActionMap() {
        return controllerToActionMap;
    }
    /**
     * Definieer de ActionProcedure die wordt uitgevoerd indien er een onverwachte Action ontvangen werd.
    *  @param invalidActionProcedure        De toe te voegen actionProcedure.
    *  @throws IllegalArgumentException     Gooi indien de gegeven ActionProcedure niet geinitialiseerd is.
    */
    public void setInvalidActionProcedure(ActionProcedure invalidActionProcedure) {
        if(!isValidActionProcedure(invalidActionProcedure)) {
            throw new IllegalArgumentException("Invalid actionProcedure");
        }
        this.invalidProcedure = invalidActionProcedure;
    }


    private class ControllerStackMemento{
        private final LinkedList<AbstractController> controllerStack;

        public ControllerStackMemento(LinkedList<AbstractController> controllerStack) {
            this.controllerStack = new LinkedList<>(controllerStack);
        }

        public LinkedList<AbstractController> getControllerStack() {
            return controllerStack;
        }
    }
}