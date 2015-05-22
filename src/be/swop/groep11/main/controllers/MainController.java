package be.swop.groep11.main.controllers;

import be.swop.groep11.main.ui.UserInterface;

/**
 * Lege controller nodig voor initialisatie controllerStack
 */
public class MainController extends AbstractController {


    /**
     * Maakt een nieuwe instantie aan van deze controller.
     * @param controllerStack Mapt per controller de beschikbare acties op de juiste actie.
     * @param showProjectsController De controller voor de usecase new project.
     * @param planningController De controller voor de usecase planning.
     * @param userInterface Een concrete implementatie van de UserInterfaceInterface.
     */
    public MainController(UserInterface userInterface) {
        super(userInterface);
    }


}
