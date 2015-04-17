package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by Ronald on 17/04/2015.
 */
public class SimulationController extends AbstractController {
    public SimulationController(ProjectRepository projectRepository, UserInterface userInterface) {
        super(userInterface);
    }

    @Override
    public void startSimulation() throws IllegalArgumentException {
        getUserInterface().printMessage("This is the simulation?");
    }
}
