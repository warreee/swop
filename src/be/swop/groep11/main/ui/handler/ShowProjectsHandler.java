package be.swop.groep11.main.ui.handler;


import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.Foo;
import be.swop.groep11.main.ui.SimpleTUI;

import java.io.IOException;


public class ShowProjectsHandler extends Handler {
    private final Foo foo;
    public ShowProjectsHandler(Foo model, SimpleTUI simpleTUI) {
        super(simpleTUI);
        this.foo = model;
        this.escapeCommand = Command.CANCEL;
        addAcceptedCommands(Command.SELECTPROJECT);

    }

    public boolean resolveCommand(Command cmd) throws IOException {
        boolean result = super.resolveCommand(cmd);
        switch (cmd) {
            case SELECTPROJECT:
                foo.setProperty(cmd.getParameter("PID"));
                break;

        }
        return result;
    }
    @Override
    protected String getControllerInfo() {
        return "ShowProjectsController info";
    }
}
