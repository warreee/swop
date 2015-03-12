package be.swop.groep11.main.controllers;

import be.swop.groep11.main.TaskMan;
import be.swop.groep11.main.ui.commands.CancelException;
import be.swop.groep11.main.ui.UserInterface;

import java.time.LocalDateTime;

/**
 * Bevat de stappen om de use case "Advance Time" uit te voeren.
 */
public class AdvanceTimeController {

    private final TaskMan taskMan;
    private final UserInterface ui;

    /**
     * Constructor om een nieuwe advance time controller te maken.
     * @param taskMan Task man die de systeemtijd bijhoudt
     * @param ui Gebruikersinterface
     */
    public AdvanceTimeController(TaskMan taskMan,UserInterface ui) {
        this.ui = ui;
        this.taskMan = taskMan;
    }

    /**
     * Voert de stappen voor de use case "Advance Time" uit.
     */
    public void advanceTime(){
        try {
            LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd:");
            taskMan.updateSystemTime(newSystemTime);
            ui.printMessage("Systeemtijd aangepast");
        } catch (IllegalArgumentException e) {
            ui.printException(e);
            advanceTime();
        } catch (CancelException e){
            ui.printException(e);
        }
    }
}
