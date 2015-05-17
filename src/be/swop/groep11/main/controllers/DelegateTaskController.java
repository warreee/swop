package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Klasse voor de controller van de usecase delegatetask.
 */
public class DelegateTaskController  extends AbstractController {

    private Company company;
    private LogonController logonController;

    public DelegateTaskController(UserInterface userInterface, Company company, LogonController logonController) {
        super(userInterface);
        this.company = company;
        this.logonController = logonController;
    }

    @Override
    public void delegateTask() {
        System.out.println("nog te implementeren");
    }
}
