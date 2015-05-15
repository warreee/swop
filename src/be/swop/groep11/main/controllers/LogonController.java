package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.resource.Developer;
import be.swop.groep11.main.resource.ProjectManager;
import be.swop.groep11.main.ui.UserInterface;

/**
 * Created by warreee on 9/05/15.
 */
public class LogonController extends AbstractController implements ILogin {

    private Company company = null;
    private BranchOffice branchOffice = null;
    private Developer developer = null;
    private ProjectManager projectManager = null;

    public LogonController(UserInterface userInterface, Company company) {
        super(userInterface);
        this.company = company;
    }
    @Override
    public void logon() throws IllegalArgumentException {
        showBranchOffices();
        selectBranchOffice();
        showEmployees();
        identify();
    }

    private void identify() {
        int userIndex = getUserInterface().requestNumber("Kies een user uit bovenstaande lijst");
        User user = getBranchOffice().getEmployees().get(userIndex);
        if (user.isDeveloper()){
            setDeveloper((Developer) user);
        }
        if (user.isProjectManager()) {
            setProjectManager((ProjectManager) user);
        }

        getUserInterface().printMessage("Je bent nu ingelogd als " + user.getName());
    }

    /**
     * Bij het uitloggen worden alle gegevens op null gezet.
     */
    @Override
    public void logOut() {
        setBranchOffice(null);
        setDeveloper(null);
        setProjectManager(null);
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

    private void showEmployees() {
        getUserInterface().showEmployees(this.branchOffice.getEmployees());
    }


    @Override
    public boolean hasIdentifiedProjectManager() {
        return getProjectManager() != null;
    }

    @Override
    public boolean hasIdentifiedDeveloper() {
        return getDeveloper() != null;
    }

    @Override
    public boolean hasIdentifiedBranchOffice() {
        return getBranchOffice() != null;
    }

    @Override
    public boolean hasIdentifiedUserAtBranchOffice() {
        return hasIdentifiedDeveloper() || hasIdentifiedProjectManager();
    }

    @Override
    public ProjectManager getProjectManager() {
        return this.projectManager;
    }

    @Override
    public Developer getDeveloper() {
        return this.developer;
    }

    @Override
    public BranchOffice getBranchOffice(){
        return this.branchOffice;
    }

    private void setBranchOffice(BranchOffice branchOffice) {
        this.branchOffice = branchOffice;
    }

    private void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    private void setProjectManager(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }
}
