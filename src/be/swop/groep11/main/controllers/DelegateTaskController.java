package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Klasse voor de controller van de usecase delegatetask.
 */
public class DelegateTaskController  extends AbstractController {

    private Company company;
    private BranchOffice branchOffice;
    private  UserInterface ui;

    public DelegateTaskController(UserInterface userInterface, Company company, BranchOffice branchOffice) {
        super(userInterface);
        this.company = company;
        this.branchOffice = branchOffice;
    }


}
