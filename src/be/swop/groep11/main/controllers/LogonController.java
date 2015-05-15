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

    public LogonController(UserInterface userInterface, Company company) {
        super(userInterface);
        this.company = company;
    }
    @Override
    public void logon() throws IllegalArgumentException {
        showBranchOffices();
    }

    @Override
    public void logOut() throws IllegalArgumentException {
        System.out.println("log out");
        identifiedPM = false;

    }

    public void showBranchOffices() {
        getUserInterface().printMessage("Kies een Branch Office uit onderstaande lijst.");
        getUserInterface().showBranchOffices(this.company.getBranchOffices());
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
