package be.swop.groep11.main.controller;


import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.model.TaskMan;
import be.swop.groep11.main.view.View;

import java.io.IOException;


public class ShowProjectsController extends Controller {
    private final TaskMan taskMan;
    public ShowProjectsController(TaskMan model,View view) {
        super(view);
        this.taskMan = model;
        this.escapeCommand = Command.CANCEL;
        addAcceptedCommands(Command.SELECTPROJECT);

    }

    public boolean resolveCommand(Command cmd) throws IOException {
        boolean result = super.resolveCommand(cmd);
        switch (cmd) {
            case SELECTPROJECT:
                taskMan.setProperty(cmd.getParameter("PID"));
                break;

        }
        return result;
    }
    @Override
    protected String getControllerInfo() {
        return "ShowProjectsController info";
    }
}
