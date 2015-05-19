package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.DelegateTaskController;
import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
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
    private ImmutableList<BranchOffice> branchOffices;
    private ImmutableList<Task> unplannedTasks;

    private LogonController logonController;

    @Before
    public void setUp() throws Exception {

        this.mockedUI = mock(UserInterface.class);
    }

    /**
     * Test voor het tonen van de ongeplande taken in de ingelogde branchoffice.
     * Hierna wordt er 1 taak geselecteerd.
     * Test voor het tonen van de branchoffices. Hierna wordt er 1 geselecteerd.
     */
    @Test
    public void delegateTaskTest() {
        when(mockedUI.selectTaskFromList(unplannedTasks)).thenReturn(unplannedTasks.get(0));
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenReturn(branchOffices.get(0));

        delegateTaskController.delegateTask();

        assertTrue(branchOffices.get(0).getDelegatedTasks().contains(unplannedTasks.get(0)));
        assertTrue(unplannedTasks.get(0).getDelegatedTo() == branchOffices.get(0));
    }

    /**
     * Test voor het tonen van de branchoffices. Hierna wordt er 1 geselecteerd.
     * Daarna wordt er gecanceld.
     */
    @Test
    public void delegateTaskShowBranchOfficesCancelTest() {
        fail("Nog niet geimplementeerd.");
    }

    /**
     * Test om het tonen van de taken en daarna cancel
     */
    @Test
    public void delegateTaskShowUnplannedTasksCancelTest() {
        fail("Nog niet geimplementeerd.");
    }


}
