package be.swop.groep11.main.ui;

import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.CommandStrategy;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Ronald on 22/04/2015.
 */
public class ActionMapping {

    private UserInterface userInterface;

    public ActionMapping(UserInterface userInterface ) {
        this.userInterface = userInterface;
    }

    public void activateController(AbstractController abstractController){
        controllerStack.addLast(abstractController);
        controllerCommandStrategies.put(getActiveController(), getActiveController().getCommandStrategies());
    }

    public void deActivateController(AbstractController abstractController){
        controllerStack.remove(abstractController);
        controllerCommandStrategies.remove(abstractController);
    }
    /**
     * Houdt lijst van Controllers bij die "actief zijn".
     * De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit.
     *
     * Soort van execution stack
     */
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();
    /**
     * Gegeven een controller verkrijg de corresponderende CommandStrategies voor de aanvaarde commands in die controller.
     */
    private HashMap<AbstractController,HashMap<Command,CommandStrategy>> controllerCommandStrategies = new HashMap<>();

    public void addCommandStrategy(AbstractController controller,Command command,CommandStrategy strategy) {
        HashMap<Command,CommandStrategy> map = controllerCommandStrategies.get(controller);
        if(map == null){
            //Niets te vinden
            //Geen mapping voor gegeven controller
            map = new HashMap<Command,CommandStrategy>();
        }
        map.put(command,strategy);
        controllerCommandStrategies.put(controller,map);
    }

    public void removeCommandStrategy(AbstractController controller, Command command) {
        HashMap<Command,CommandStrategy> map = controllerCommandStrategies.get(controller);
        if(map == null){
            //Niets te vinden
            throw new IllegalArgumentException("Geen mapping aanwezig voor de gegeven controller");
        }
        map.remove(command);
    }

    private AbstractController getActiveController(){
        return controllerStack.getLast();
    }

    private HashMap<Command,CommandStrategy> getMappingFor(AbstractController controller)throws IllegalArgumentException{
        HashMap<Command,CommandStrategy> map = controllerCommandStrategies.get(controller);
        if(map == null){
            //Niets te vinden
            throw new IllegalArgumentException("Geen mapping aanwezig voor de gegeven controller");
        }
        return map;
    }

    public void executeAction(Command command){
        HashMap<Command,CommandStrategy> map = getMappingFor(getActiveController());

        CommandStrategy strategy = map.get(command);
        if(strategy != null){
            strategy.execute();
        }
        //TODO foutmelding/exception indien geen strategy aanwezig?
    }

    private UserInterface getUserInterface() {
        return userInterface;
    }

    private CommandStrategy invalid = () -> getUserInterface().printMessage("Ongeldig commando, Abstract controller");

}
