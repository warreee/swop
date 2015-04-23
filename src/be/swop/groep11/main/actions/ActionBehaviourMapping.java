package be.swop.groep11.main.actions;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.ui.UserInterface;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Ronald on 22/04/2015.
 */
public class ActionBehaviourMapping {
    //TODO betere naam voor de klasse ActionMapping zoeken. (ActionBehaviour?)
    /**
     *  Constructor voor aanmaken van een ActionBehaviourMapping
     * @param userInterface     De userInterface waarmee deze ActionBehaviourMapping gebruikt wordt.
     * @param invalidStrategy   De ActionStrategy(gedrag) uit te voeren, indien een action niet geldig is.
     * @throws IllegalArgumentException
     *                          Gooi indien de gegeven userInterface of invalidStrategy niet geinitialiseerd zijn.
     *                          Gooi indien de gegeven userInterface alreeds een ActionBehaviourMapping bezit.
     */
    public ActionBehaviourMapping(UserInterface userInterface, ActionBehaviour invalidStrategy) {
        if (!canHaveAsUserInterface(userInterface)) {
            throw new IllegalArgumentException("Ongeldige userInterface");
        }
        if (!canHaveAsInvalidStrategy(invalidStrategy)) {
            throw new IllegalArgumentException("Ongeldige invalidBehaviour strategy");
        }
        this.userInterface = userInterface;
        this.invalidBehaviour = invalidStrategy;
        //ActionMapping moet eerst een UI hebben alvorens de UI een ActionMapping kan "setten".
        userInterface.setActionBehaviourMapping(this);
    }

    /**
     * Check of deze ActionBehaviourMapping geassocieerd kan worden met de gegeven userInterface
     * @param userInterface De te controleren userInterface
     * @return              Waar indien de userInterface geinitialiseerd is en
     *                      de userInterface reeds geen ActionBehaviourMapping bezit
     */
    private boolean canHaveAsUserInterface(UserInterface userInterface) {
        return userInterface != null && userInterface.getActionBehaviourMapping() == null;
    }

    /**
     * Check of voor deze ActionBehaviourMapping de gegeven ActionStrategy een geldige invalidStrategy kan zijn.
     * InvalidStrategy is degene die wordt uitgevoerd indien er geen mapping bestaat.
     * @param invalidStrategy   De te controleren ActionStrategy
     * @return                  Waar indien de invalidStrategy geinitialiseerd is.
     */
    private boolean canHaveAsInvalidStrategy(ActionBehaviour invalidStrategy) {
        return invalidStrategy != null;
    }

    private ActionBehaviour invalidBehaviour;
    private UserInterface userInterface;
    // Houdt lijst van Controllers bij die "actief zijn". De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit. Soort van execution stack
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();
    //Gegeven een controller verkrijg de corresponderende ActionBehaviours voor de aanvaarde commands in die controller.
    private HashMap<AbstractController,HashMap<Action,ActionBehaviour>> controllerActionBehavioursMap = new HashMap<>();
    //Een Map die voor een action een standaard ActionStrategy bepaald. Zodat iedere nieuwe controller die gemapt wordt reeds enkele standaard behaviours bevat
    private HashMap<Action,ActionBehaviour> defaultActionBehavioursMap = new HashMap<>();

    /**
     * Het toevoegen an een ActionBehaviour voor de gegeven Action voor een specifieke AbstractController.
     * @param controller    De AbstractController waarvoor de nieuwe ActionBehaviour wordt gedefinieerd
     * @param action        De Action geassocieerd met de nieuwe ActionBehaviour
     * @param behaviour     De toe te voegen ActionBehaviour
     */
    public void addActionBehaviour(AbstractController controller, Action action, ActionBehaviour behaviour) {
        HashMap<Action,ActionBehaviour> map = controllerActionBehavioursMap.get(controller);
        if(map == null){  //Niets te vinden, geen mapping voor gegeven controller
            map = new HashMap<>(getDefaultMap()); //Start van default Map<Action,ActionBehaviour>
        }
        map.put(action,behaviour);
        controllerActionBehavioursMap.put(controller, map);
    }

    /**
     * Het verwijderen van een ActionBehaviour geassocieerd met de Gegeven Action voor de gegeven AbstractController.
     * @param controller    De AbstractController waarvoor men de ActionBehaviour geassocieerd met de Action wilt verwijderen
     * @param action        De Action geassocieerd met de ActionBehaviour, dewelke men wil verwijderen
     */
    private void removeActionBehaviour(AbstractController controller, Action action) {
        HashMap<Action,ActionBehaviour> map = controllerActionBehavioursMap.get(controller);
        if(map == null){
            //Niets te vinden
            throw new IllegalArgumentException("Geen mapping aanwezig voor de gegeven controller");
        }
        map.remove(action);
    }

    /**
     *
     * @param abstractController
     */
    public void activateController(AbstractController abstractController){
        controllerStack.addLast(abstractController);
    }

    /**
     *
     * @param abstractController
     */
    public void deActivateController(AbstractController abstractController){
        controllerStack.remove(abstractController);
    }

    /**
     *
     * @param action
     */
    public void executeAction(Action action){
        HashMap<Action,ActionBehaviour> map = getMappingFor(getActiveController());

        ActionBehaviour strategy = map.get(action);
        if(strategy != null){
            strategy.execute();
        }else{
            //Action niet herkend!
            getInvalidBehaviour().execute();
        }
    }

    public HashMap<Action,ActionBehaviour> getMappingFor(AbstractController controller)throws IllegalArgumentException{
        HashMap<Action,ActionBehaviour> map = controllerActionBehavioursMap.get(controller);
        if(map == null){//Niets te vinden
            throw new IllegalArgumentException("Geen mapping aanwezig voor de gegeven controller");
        }
        return map;
    }

    public void addDefaultStrategy(Action action, ActionBehaviour strategy) {
        if(!isValidActionStrategy(action, strategy)) {
            throw new IllegalArgumentException("Invalid command & commandStrategy");
        }
        getDefaultMap().put(action, strategy);
    }

    private boolean isValidActionStrategy(Action action, ActionBehaviour strategy) {
        return action != null & strategy != null;
    }

    private HashMap<Action,ActionBehaviour> getDefaultMap(){
        return defaultActionBehavioursMap;
    }

    public AbstractController getActiveController(){
        return controllerStack.getLast();
    }
    public UserInterface getUserInterface() {
        return userInterface;
    }

    public ActionBehaviour getInvalidBehaviour() {
        return this.invalidBehaviour;
    }
}