package be.swop.groep11.main.ui.commands;

import be.swop.groep11.main.controllers.AbstractController;

/**
 * Created by Ronald on 17/04/2015.
 */
public interface CommandStrategy {
    void execute(AbstractController abstractController);
}
