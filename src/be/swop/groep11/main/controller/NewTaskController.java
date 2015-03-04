package be.swop.groep11.main.controller;

import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.view.View;

import java.io.IOException;

/**
 * Created by Ronald on 2/03/2015.
 */
public class NewTaskController extends Controller {
    public NewTaskController(View view) {
        super(view);
    }

    @Override
    public boolean resolveCommand(Command cmd) throws IOException {
     return  super.resolveCommand(cmd);
    }
    @Override
    protected String getControllerInfo() {
        return "NewTaskController info";
    }
}
