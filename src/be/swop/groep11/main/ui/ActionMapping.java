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


    private AbstractController getCurrentController() {
        return controllerStack.getLast();
    }

    public void addControllerToStack(AbstractController abstractController){
        controllerStack.addLast(abstractController);
        currentCommandStrategies.put(getCurrentController(), getCurrentController().getCommandStrategies());
    }

    public void removeControllerFromStack(AbstractController abstractController){
        controllerStack.remove(abstractController);
        currentCommandStrategies.remove(abstractController);
    }
    /**
     * Houdt lijst van Controllers bij die "actief zijn".
     * De laatst toegevoegde Controller stelt het use case voor waarin de gebruiker zit.
     *
     */
    private LinkedList<AbstractController> controllerStack = new LinkedList<>();


    private HashMap<Command,CommandStrategy> map;

    /**
     * Gegeven een controller verkrijg de corresponderende CommandStrategies voor de aanvaarde commands in die controller.
     */
    private HashMap<AbstractController,HashMap<Command,CommandStrategy>> currentCommandStrategies = new HashMap<>();

    private void addCommandStrategy(AbstractController controller,Command command,CommandStrategy strategy){

    }
}
