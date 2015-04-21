package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TMSystem;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.CommandStrategy;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Bevat de stappen om de use case "Advance Time" uit te voeren.
 */
public class AdvanceTimeController extends AbstractController {

    /**
     * Constructor om een nieuwe advance time controller te maken.
     * @param TMSystem Task man die de systeemtijd bijhoudt
     * @param ui Gebruikersinterface
     */
    public AdvanceTimeController(UserInterface ui, SystemTime systemTime) {
        super(ui,systemTime);
    }

    /**
     * Voert de stappen voor de use case "Advance Time" uit.
     */
    public void advanceTime(){
        try {
            LocalDateTime newSystemTime =  getUserInterface().requestDatum("Nieuwe systeemtijd:");
            getSysteTime().updateSystemTime(newSystemTime);
            getUserInterface().printMessage("Systeemtijd aangepast");
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            advanceTime();
        } catch (CancelException e){
            getUserInterface().printException(e);
        }
    }

    @Override
    public HashMap<Command,CommandStrategy> getCommandStrategies(){
        HashMap<Command,CommandStrategy> map = new HashMap<>(super.getCommandStrategies());
        map.put(Command.ADVANCETIME,this::advanceTime);
        return map;
    }
}
