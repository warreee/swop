package be.swop.groep11.main.controller;

import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.view.View;

import java.io.IOException;


public class NewProjectController extends Controller {
    public NewProjectController(View view) {
        super(view);
        this.escapeCommand = Command.CANCEL;
    }

    @Override
    public boolean resolveCommand(Command cmd) throws IOException {
    return super.resolveCommand(cmd);
    }
    @Override
    protected String getControllerInfo() {
        return "NewProjectController info";
    }
}
