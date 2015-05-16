package be.swop.groep11.test.integration;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Testklasse om de usecase logon te testen.
 */
public class LogonScenarioTest {

    private UserInterface mockedUI;
    private BranchOffice branchOffice;

    @Before
    public void setUp() throws Exception {
        this.mockedUI = mock(UserInterface.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ResourcePlanner resourcePlanner = mock(ResourcePlanner.class);
        this.branchOffice = new BranchOffice("Branchoffice 1", "Leuven", projectRepository, resourcePlanner);


    }

    @Test
    public void test() throws Exception {
        fail("implement tests");

    }
}
