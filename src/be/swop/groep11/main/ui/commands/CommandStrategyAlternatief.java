package be.swop.groep11.main.ui.commands;

import be.swop.groep11.main.controllers.AbstractController;

/**
 * Created by Ronald on 17/04/2015.
 */
public interface CommandStrategyAlternatief <T extends AbstractController> {
    //TODO dit is in principe een Consumer functionalInterface,
    // het bounded generisch type is een garantie dat het minstens een AbstractController moet zijn
    void execute(T controller);
}
