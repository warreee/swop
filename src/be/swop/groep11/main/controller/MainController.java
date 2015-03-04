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
                result = true; //indien run volledig gedaan, betekend dat de focus terug op de MainController is
                break;
            case CANCEL:
                view.print("CANCEL");
                break;
            case NEWPROJECTS:
                CommandLine.getCMDL().getNewProjectController().run();
                result = true;
                break;
            case ADVANCETIME:
                CommandLine.getCMDL().getAdvanceTimeController().run();
                result = true;
                break;
            case UPDATETASK:
                CommandLine.getCMDL().getUpdateTaskController().run();
                result = true;
                break;
            case NEWTASK:
                CommandLine.getCMDL().getNewTaskController().run();
                result = true;
                break;
        }
        return result;
    }

    @Override
    protected String getControllerInfo() {
        return "MainController";
    }
}
