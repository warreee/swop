package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.resource.Developer;
import be.swop.groep11.main.resource.ProjectManager;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by warreee on 9/05/15.
 */
public class LogonController extends AbstractController implements ILogin {

    private boolean identifiedPM = false;
    private Company company;
    private BranchOffice branchOffice;

    public LogonController(UserInterface userInterface, Company company) {
        super(userInterface);
        this.company = company;
    }
    @Override
    public void logon() throws IllegalArgumentException {
        showBranchOffices();
        selectBranchOffice();
    }

    @Override
    public void logOut() throws IllegalArgumentException {
        System.out.println("log out");
        identifiedPM = false;

    }

    /**
     * Lijst weergave van de branchoffices.
     */
    private void showBranchOffices() {
        getUserInterface().showBranchOffices(this.company.getBranchOffices());

    }

    /**
     * Vraagt aan de user op welke brancheoffice hij wilt inloggen.
     */
    private void selectBranchOffice() {
        int branchOfficeIndex = getUserInterface().requestNumber("Kies een branchoffice uit bovenstaande lijst");
        this.branchOffice = this.company.getBranchOffices().get(branchOfficeIndex);
    }


    @Override
    public boolean hasIdentifiedProjectManager() {
        return identifiedPM;
    }

    @Override
    public boolean hasIdentifiedDeveloper() {
        return false;
    }

    @Override
    public boolean hasIdentifiedBranchOffice() {
        return false;
    }

    @Override
    public boolean hasIdentifiedUserAtBranchOffice() {
        return false;
    }

    @Override
    public ProjectManager getProjectManager() {
        return null;
    }

    @Override
    public Developer getDeveloper() {
        return null;
    }

    @Override
    public BranchOffice getBranchOffice(){
        return null;
    }
}
