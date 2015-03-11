package be.swop.groep11.main.controllers;

import be.swop.groep11.main.TaskMan;
import be.swop.groep11.main.ui.CancelException;
import be.swop.groep11.main.ui.UserInterface;

import java.time.LocalDateTime;

/**
 * Created by warreee on 3/9/15.
 */
public class AdvanceTimeController {

    private final TaskMan taskMan;
    private final UserInterface ui;

    public AdvanceTimeController(TaskMan taskMan,UserInterface ui) {
        this.ui = ui;
        this.taskMan = taskMan;
    }

    public void advanceTime(){
        try {
            LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd");
            taskMan.updateSystemTime(newSystemTime);
        } catch (IllegalArgumentException e) {
            ui.printException(e);
            advanceTime();
        } catch (CancelException e){
            ui.printException(e);
        }
    }
}
