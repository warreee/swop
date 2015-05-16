package be.swop.groep11.test.integration;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testklasse om de usecase logon te testen.
 */
public class LogonScenarioTest {

    private UserInterface mockedUI;
    private BranchOffice branchOffice;

    @Before
    public void setUp() throws Exception {
        Company company = new Company("company", mock(ResourceTypeRepository.class));
        this.mockedUI = mock(UserInterface.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ResourcePlanner resourcePlanner = mock(ResourcePlanner.class);
        this.branchOffice = new BranchOffice("Branchoffice 1", "Leuven", projectRepository, resourcePlanner);
        Developer developer1 = new Developer("dev1", mock(ResourceType.class)); // TODO: moet er echt telkends een resourcetype worden meegegeven
        Developer developer2 = new Developer("dev2", mock(ResourceType.class)); // TODO: kunnen we geen standaard constructor in devloper aanmaken?
        ProjectManager projectManager = new ProjectManager("projectmanager1");
        branchOffice.addEmployee(developer1);
        branchOffice.addEmployee(developer2);
        branchOffice.addEmployee(projectManager);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void logonValidTest() throws Exception {
        fail("implement tests");
        //when(mockedUI.selectBranchOfficeFromList()
    }
}
