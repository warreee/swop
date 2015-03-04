package be.swop.groep11.main.controller;

import be.swop.groep11.main.CommandLine;
import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.view.View;

import java.io.IOException;


public class MainController extends Controller {

    public MainController(View view) {
        super(view);

        addAcceptedCommands(Command.SHOWPROJECTS,Command.NEWPROJECTS,Command.ADVANCETIME,Command.UPDATETASK,Command.NEWTASK);
    }

    @Override
    public boolean resolveCommand(Command cmd) throws IOException {
        boolean result = super.resolveCommand(cmd);
        switch (cmd) {
            case SHOWPROJECTS:
                CommandLine.getCMDL().getShowProjectsController().run();
                break;
            case CANCEL:
                view.print("CANCEL");
                break;
            case NEWPROJECTS:
                CommandLine.getCMDL().getNewProjectController().run();
                break;
            case ADVANCETIME:
                CommandLine.getCMDL().getAdvanceTimeController().run();
                break;
            case UPDATETASK:
                CommandLine.getCMDL().getUpdateTaskController().run();
                break;
            case NEWTASK:
                CommandLine.getCMDL().getNewTaskController().run();
                break;
        }
        return result;
    }

    @Override
    protected String getControllerInfo() {
        return "MainController info";
    }
}
