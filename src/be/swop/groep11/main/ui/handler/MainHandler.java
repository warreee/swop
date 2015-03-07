package be.swop.groep11.main.ui.handler;

import be.swop.groep11.main.ui.CommandLine;
import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.SimpleTUI;

import java.io.IOException;


public class MainHandler extends Handler {

    public MainHandler(SimpleTUI simpleTUI) {
        super(simpleTUI);

        addAcceptedCommands(Command.SHOWPROJECTS,Command.NEWPROJECTS,Command.ADVANCETIME,Command.UPDATETASK,Command.NEWTASK);
    }

    @Override
    public boolean resolveCommand(Command cmd) throws IOException {
        boolean result = super.resolveCommand(cmd);
        switch (cmd) {
            case SHOWPROJECTS:
                CommandLine.getCMDL().getShowProjectsController().run();
                result = true; //indien run volledig gedaan, betekend dat de focus terug op de MainController is
                break;
            case CANCEL:
                simpleTUI.print("CANCEL");
                break;
        }
        return result;
    }

    @Override
    protected String getControllerInfo() {
        return "MainController";
    }
}
