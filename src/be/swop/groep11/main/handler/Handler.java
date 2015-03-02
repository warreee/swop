package be.swop.groep11.main.handler;

import be.swop.groep11.main.commands.Command;

/**
 * Created by Ronald on 2/03/2015.
 */
public abstract class Handler {
    //TODO observable?
    public abstract void resolveCommand(Command cmd);
}
