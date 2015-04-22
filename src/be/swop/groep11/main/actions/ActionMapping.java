package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.ui.UserInterface;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Ronald on 22/04/2015.
 */
public class ActionMapping {

    /**
     *
     * @param userInterface
     * @param invalidStrategy   De strategy uit te voeren, indien command niet herkent wordt.
     */
    public ActionMapping(UserInterface userInterface,ActionStrategy invalidStrategy ) {
        if (!canHaveAsUserInterface(userInterface)) {
            throw new IllegalArgumentException("Ongeldige userInterface");
        }
        if (!canHaveAsInvalidStrategy(invalidStrategy)) {
            throw new IllegalArgumentException("Ongeldige invalid strategy");
        }
        this.userInterface = userInterface;
        this.invalid = invalidStrategy;
        //ActionMapping moet eerst een UI hebben alvorens de UI een ActionMapping kan "setten".
        userInterface.setActionMapping(this);
    }

    private boolean canHaveAsUserInterface(UserInterface userInterface) {
        return userInterface != null;
    }
    private boolean canHaveAsInvalidStrategy(ActionStrategy invalidStrategy) {
        return invalidStrategy != null;
    }

    private ActionStrategy invalid;
    private UserInterface userInterface;
    // Houdt lijst van Controllers bij die "actief zijn". De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit. Soort van execution stack
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();
    //Gegeven een controller verkrijg de corresponderende CommandStrategies voor de aanvaarde commands in die controller.
    private HashMap<AbstractController,HashMap<Action,ActionStrategy>> controllerActionStrategies = new HashMap<>();
    private HashMap<Action,ActionStrategy> defaultActionStrategies = new HashMap<>();

    public void addCommandStrategy(AbstractController controller,Action action,ActionStrategy strategy) {
        HashMap<Action,ActionStrategy> map = controllerActionStrategies.get(controller);
        if(map == null){
            //Niets te vinden
            //Geen mapping voor gegeven controller
            map = new HashMap<>(getDefaultMap()); //Start van default Command,CommandStrategy map
        }
        map.put(action,strategy);
        controllerActionStrategies.put(controller, map);
    }

    private void removeCommandStrategy(AbstractController controller, Action action) {
        HashMap<Action,ActionStrategy> map = controllerActionStrategies.get(controller);
        if(map == null){
            //Niets te vinden
            throw new IllegalArgumentException("Geen mapping aanwezig voor de gegeven controller");
        }
        map.remove(action);
    }

    public void activateController(AbstractController abstractController){
        controllerStack.addLast(abstractController);
    }
    public void deActivateController(AbstractController abstractController){
        controllerStack.remove(abstractController);
    }
    public void executeAction(Action action){
        HashMap<Action,ActionStrategy> map = getMappingFor(getActiveController());

        ActionStrategy strategy = map.get(action);
        if(strategy != null){
            strategy.execute();
        }else{
            //Command niet herkend!
            getInvalid().execute();
        }
    }

    public HashMap<Action,ActionStrategy> getMappingFor(AbstractController controller)throws IllegalArgumentException{
        HashMap<Action,ActionStrategy> map = controllerActionStrategies.get(controller);
        if(map == null){//Niets te vinden
            throw new IllegalArgumentException("Geen mapping aanwezig voor de gegeven controller");
        }
        return map;
    }

    public void addDefaultStrategy(Action action, ActionStrategy strategy) {
        if(!isValidCommandStrategy(action, strategy)) {
            throw new IllegalArgumentException("Invalid command & commandStrategy");
        }
        getDefaultMap().put(action, strategy);
    }

    private boolean isValidCommandStrategy(Action action, ActionStrategy strategy) {
        return action != null & strategy != null;
    }

    private HashMap<Action,ActionStrategy> getDefaultMap(){
        return defaultActionStrategies;
    }

    public AbstractController getActiveController(){
        return controllerStack.getLast();
    }
    public UserInterface getUserInterface() {
        return userInterface;
    }

    public ActionStrategy getInvalid() {
        return this.invalid;
    }
}