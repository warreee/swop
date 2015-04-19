package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.TMSystem;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.UserInterface;

import java.time.LocalDateTime;

/**
 * Bevat de stappen om de use case "Advance Time" uit te voeren.
 */
public class AdvanceTimeController extends AbstractController {

    private final TMSystem TMSystem;

    /**
     * Constructor om een nieuwe advance time controller te maken.
     * @param TMSystem Task man die de systeemtijd bijhoudt
     * @param ui Gebruikersinterface
     */
    public AdvanceTimeController(TMSystem TMSystem, UserInterface ui) {
        super(ui);
        this.TMSystem = TMSystem;
    }

    /**
     * Voert de stappen voor de use case "Advance Time" uit.
     */
    public void advanceTime(){
        try {
            LocalDateTime newSystemTime =  getUserInterface().requestDatum("Nieuwe systeemtijd:");
            TMSystem.updateSystemTime(newSystemTime);
            getUserInterface().printMessage("Systeemtijd aangepast");
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            advanceTime();
        } catch (CancelException e){
            getUserInterface().printException(e);
        }
    }
}
