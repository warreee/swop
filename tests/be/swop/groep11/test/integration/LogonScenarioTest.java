package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
        SystemTime systemTime = new SystemTime();
        company = new Company("company", mock(ResourceTypeRepository.class),systemTime);
        this.mockedUI = mock(UserInterface.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
        ResourcePlanner resourcePlanner = mock(ResourcePlanner.class);
        when(resourcePlanner.hasEnoughResourcesToPlan(any())).thenReturn(true);
        when(resourcePlanner.getResourceRepository()).thenReturn(resourceRepository);
        this.branchOffice = new BranchOffice("BranchOffice 1", "Leuven", projectRepository, resourcePlanner);
        AResourceType developerType = resourceTypeRepository.getDeveloperType();
        Developer developer1 = new Developer("dev1", developerType);
        Developer developer2 = new Developer("dev2", developerType);
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
        when(mockedUI.selectEmployeeFromList(employees)).thenReturn(employees.get(1));

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
        when(mockedUI.selectEmployeeFromList(employees)).thenReturn(employees.get(0));

        logonController.logon();

        assertTrue(logonController.hasIdentifiedBranchOffice());
        assertTrue(logonController.hasIdentifiedUserAtBranchOffice());
        assertFalse(logonController.hasIdentifiedDeveloper());
        assertTrue(logonController.hasIdentifiedProjectManager());
    }


    @Test(expected = StopTestException.class)
    public void logon_CancelSelectBranchofficeTest() {
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenThrow(new CancelException("Cancel in Test"));
        when(mockedUI.selectEmployeeFromList(employees)).thenReturn(employees.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        logonController.logon();
    }

    @Test(expected = StopTestException.class)
    public void logon_CancelSelectEmployeeTest() {
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenReturn(branchOffices.get(0));
        when(mockedUI.selectEmployeeFromList(employees)).thenThrow(new CancelException("Cancel in Test"));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        logonController.logon();
    }

    @Test
    public void logoutTest() {
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenReturn(branchOffices.get(0));
        when(mockedUI.selectEmployeeFromList(employees)).thenReturn(employees.get(1));

        logonController.logon();
        assertTrue(logonController.hasIdentifiedBranchOffice());
        assertTrue(logonController.hasIdentifiedUserAtBranchOffice());
        assertTrue(logonController.hasIdentifiedDeveloper());
        assertFalse(logonController.hasIdentifiedProjectManager());

        logonController.logOut();
        assertFalse(logonController.hasIdentifiedBranchOffice());
        assertFalse(logonController.hasIdentifiedUserAtBranchOffice());
        assertFalse(logonController.hasIdentifiedDeveloper());
        assertFalse(logonController.hasIdentifiedProjectManager());

    }
}
