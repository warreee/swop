package be.swop.groep11.main.controller;

import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.view.View;

import java.io.IOException;

/**
 * Created by Ronald on 2/03/2015.
 */
public class UpdateTaskController extends Controller {
    public UpdateTaskController(View view) {
        super(view);
        this.escapeCommand = Command.CANCEL;
    }

    @Override
    public boolean resolveCommand(Command cmd) throws IOException {
        return super.resolveCommand(cmd);
    }

    @Override
    protected String getControllerInfo() {
        return "UpdateTaskController info";

    }
}