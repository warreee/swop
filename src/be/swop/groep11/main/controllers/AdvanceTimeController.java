package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.actions.ActionMapping;
import be.swop.groep11.main.actions.CancelException;

import java.time.LocalDateTime;

/**
 * Bevat de stappen om de use case "Advance Time" uit te voeren.
 */
public class AdvanceTimeController extends AbstractController {

    /**
     * Constructor om een nieuwe advance time controller te maken.
     * @param TMSystem Task man die de systeemtijd bijhoudt
     * @param ui Gebruikersinterface
     */
    public AdvanceTimeController(ActionMapping actionMapping, SystemTime systemTime) {
        super(actionMapping);
        this.systemTime = systemTime;
    }
    private SystemTime systemTime;

    private SystemTime getSystemTime() {
        return systemTime;
    }

    /**
     * Voert de stappen voor de use case "Advance Time" uit.
     */
    public void advanceTime(){
        try {
            LocalDateTime newSystemTime =  getUserInterface().requestDatum("Nieuwe systeemtijd:");
            getSystemTime().updateSystemTime(newSystemTime);
            getUserInterface().printMessage("Systeemtijd aangepast");
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            advanceTime();
        } catch (CancelException e){
            getUserInterface().printException(e);
        }
    }

}
