package be.swop.groep11.main.ui.commands;

/**
 * Created by Ronald on 17/04/2015.
 */
public interface CommandStrategy {
    //TODO dit is in principe een Consumer functionalInterface met als generisch type AbstractController
    void execute();
}