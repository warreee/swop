package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.DelegateTaskController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test om de usecase delegate task scenario na te gaan.
 */
public class DelegateTaskScenarioTest {

    private UserInterface mockedUI;
    private BranchOffice branchOffice;
    private Company company;

    private DelegateTaskController delegateTaskController;

    @Before
    public void setUp() throws Exception {

        this.mockedUI = mock(UserInterface.class);
    }

    /**
     * Test voor het tonen van de ongeplande taken in de ingelogde branchoffice.
     * Hierna wordt er 1 taak geselecteerd.
     */
    @Test
    public void delegateTaskShowUnplannedTasksTest() {
        fail("Nog niet geimplementeerd.");
    }

    /**
     * Test voor het tonen van de branchoffices. Hierna wordt er 1 geselecteerd.
     */
    @Test
    public void delegateTaskShowBranchOfficesTest() {
        fail("Nog niet geimplementeerd.");
    }

    /**
     * Test om het volledige scenario te testen.
     */
    @Test
    public void delegateTaskTest() {
        fail("Nog niet geimplementeerd.");
    }


}
