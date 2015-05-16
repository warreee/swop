package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testklasse om de usecase logon te testen.
 */
public class LogonScenarioTest {

    private UserInterface mockedUI;
    private BranchOffice branchOffice;
    private Company company;
    private ImmutableList<User> employees;
    private ImmutableList<BranchOffice> branchOffices;
    private LogonController logonController;

    @Before
    public void setUp() throws Exception {
        company = new Company("company", mock(ResourceTypeRepository.class));
        this.mockedUI = mock(UserInterface.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ResourcePlanner resourcePlanner = mock(ResourcePlanner.class);
        this.branchOffice = new BranchOffice("Branchoffice 1", "Leuven", projectRepository, resourcePlanner);
        Developer developer1 = new Developer("dev1", mock(ResourceType.class)); // TODO: moet er echt telkends een resourcetype worden meegegeven
        Developer developer2 = new Developer("dev2", mock(ResourceType.class)); // TODO: kunnen we geen standaard constructor in devloper aanmaken?
        ProjectManager projectManager = new ProjectManager("projectmanager1");
        company.addBranchOffice(branchOffice);
        branchOffice.addEmployee(developer1);
        branchOffice.addEmployee(developer2);
        branchOffice.addEmployee(projectManager);
        this.employees = branchOffice.getEmployees();
        this.branchOffices = company.getBranchOffices();

        this.logonController = new LogonController(mockedUI, company);
    }

    /**
     * Controleert een valabele logon in het systeem als developer
     */
    @Test
    public void logonDeveloperValidTest() {
        // stubbing
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenReturn(branchOffices.get(0));
        when(mockedUI.selectEmployeeFromList(employees)).thenReturn(employees.get(0));

        logonController.logon();

        assertTrue(logonController.hasIdentifiedBranchOffice());
        assertTrue(logonController.hasIdentifiedUserAtBranchOffice());
        assertTrue(logonController.hasIdentifiedDeveloper());
        assertFalse(logonController.hasIdentifiedProjectManager());
    }

    /**
     * Controleert een valabele logon in het systeem als projectmanager
     */
    @Test
    public void logonProjectManagerValidTest() {
        // stubbing
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenReturn(branchOffices.get(0));
        when(mockedUI.selectEmployeeFromList(employees)).thenReturn(employees.get(2));

        logonController.logon();

        assertTrue(logonController.hasIdentifiedBranchOffice());
        assertTrue(logonController.hasIdentifiedUserAtBranchOffice());
        assertFalse(logonController.hasIdentifiedDeveloper());
        assertTrue(logonController.hasIdentifiedProjectManager());
    }


}
