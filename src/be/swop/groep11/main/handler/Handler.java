package be.swop.groep11.main.handler;

import be.swop.groep11.main.commands.Command;

import java.util.Observable;

/**
 * Handlers genereren de events
 * Created by Ronald on 2/03/2015.
 */
public abstract class Handler extends Observable{
    public abstract Handler resolveCommand(Command cmd);
}
