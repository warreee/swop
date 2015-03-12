package be.swop.groep11.main.controllers;

import be.swop.groep11.main.System;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.UserInterface;

import java.time.LocalDateTime;

/**
 * Bevat de stappen om de use case "Advance Time" uit te voeren.
 */
public class AdvanceTimeController {

    private final System system;
    private final UserInterface ui;

    /**
     * Constructor om een nieuwe advance time controller te maken.
     * @param system Task man die de systeemtijd bijhoudt
     * @param ui Gebruikersinterface
     */
    public AdvanceTimeController(System system,UserInterface ui) {
        this.ui = ui;
        this.system = system;
    }

    /**
     * Voert de stappen voor de use case "Advance Time" uit.
     */
    public void advanceTime(){
        try {
            LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd:");
            system.updateSystemTime(newSystemTime);
            ui.printMessage("Systeemtijd aangepast");
        } catch (IllegalArgumentException e) {
            ui.printException(e);
            advanceTime();
        } catch (CancelException e){
            ui.printException(e);
        }
    }
}
