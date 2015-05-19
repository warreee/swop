package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.DelegateTaskController;
import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test om de usecase delegate task scenario na te gaan.
 */
public class DelegateTaskScenarioTest {

    private UserInterface mockedUI;

    private DelegateTaskController delegateTaskController;
    private ImmutableList<BranchOffice> branchOffices;
    private ImmutableList<Task> unplannedTasks;
    private LogonController logonController;

    private BranchOffice source;
    private BranchOffice destination;

    private Company company;


    @Before
    public void setUp() throws Exception {

        this.mockedUI = mock(UserInterface.class);
        this.company = mock(Company.class);
        logonController = mock(LogonController.class);

        delegateTaskController = new DelegateTaskController(mockedUI, company, logonController);

        ProjectRepository projectRepository = mock(ProjectRepository.class);
        ResourcePlanner resourcePlanner = mock(ResourcePlanner.class);
        //this.source = new BranchOffice("source", "china", projectRepository, resourcePlanner);
        this.source = mock(BranchOffice.class);
        this.destination = new BranchOffice("destination", "china", projectRepository, resourcePlanner);
        BranchOffice branchOffice2 = mock(BranchOffice.class);
        BranchOffice branchOffice3 = mock(BranchOffice.class);

        List<BranchOffice> BOList = new ArrayList<BranchOffice>();



        BOList.add(source);
        BOList.add(destination);
        BOList.add(branchOffice2);
        BOList.add(branchOffice3);

        Duration duration = Duration.ofDays(1);
        DependencyGraph dependencyGraph = mock(DependencyGraph.class);

        IRequirementList requirementList = mock(IRequirementList.class);

        Project project = mock(Project.class);

        Task unplannedTask1 = new Task("taak1", duration, 0.1, dependencyGraph, requirementList, project);
        Task unplannedTask2 = mock(Task.class);
        Task unplannedTask3 = mock(Task.class);

        List<Task>UTList = new ArrayList<>();

        UTList.add(unplannedTask1);
        UTList.add(unplannedTask2);
        UTList.add(unplannedTask3);

        // TODO ervoor zorgen dat er in de UI gewerkt wordt met Lists ipv immutable list
        unplannedTasks = ImmutableList.copyOf(UTList);
        branchOffices = ImmutableList.copyOf(BOList);


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


        when(logonController.getBranchOffice()).thenReturn(source);
        ArrayList<Task> list = new ArrayList<>();
        list.add(unplannedTasks.get(0));
        when(source.getUnplannedTasks()).thenReturn(list);
        ArrayList<BranchOffice> tempList = new ArrayList<>();
        tempList.add(source);
        tempList.add(destination);
        when(company.getBranchOffices()).thenReturn(ImmutableList.copyOf(tempList));
        Mockito.doNothing().when(source).delegateTask(any(), any());

        ArrayList<Task> tempList2 = new ArrayList<>();
        tempList2.add(unplannedTasks.get(0));

        when(source.getDelegatedTasks()).thenReturn(ImmutableList.copyOf(tempList2));
        delegateTaskController.delegateTask();

        assertTrue(branchOffices.get(0).getDelegatedTasks().contains(unplannedTasks.get(0)));
        //assertTrue(unplannedTasks.get(0).getDelegatedTo() == branchOffices.get(1));
    }

    /**
     * Test voor het tonen van de branchoffices. Hierna wordt er 1 geselecteerd.
     * Daarna wordt er gecanceld.
     */
    @Test(expected = StopTestException.class)
    public void delegateTaskShowBranchOfficesCancelTest() {
        when(mockedUI.selectTaskFromList(unplannedTasks)).thenReturn(unplannedTasks.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        when(logonController.getBranchOffice()).thenReturn(source);
        ArrayList<Task> list = new ArrayList<>();
        list.add(unplannedTasks.get(0));
        when(source.getUnplannedTasks()).thenReturn(list);
        ArrayList<BranchOffice> tempList = new ArrayList<>();
        tempList.add(source);
        tempList.add(destination);
        when(company.getBranchOffices()).thenThrow(new CancelException("Cancel in Test"));
        Mockito.doNothing().when(source).delegateTask(any(), any());

        delegateTaskController.delegateTask();
    }

    /**
     * Test om het tonen van de taken en daarna cancel
     */
    @Test(expected = StopTestException.class)
    public void delegateTaskShowUnplannedTasksCancelTest() {
        when(mockedUI.selectBranchOfficeFromList(branchOffices)).thenReturn(branchOffices.get(1));

        when(logonController.getBranchOffice()).thenReturn(source);

        when(source.getUnplannedTasks()).thenThrow(new CancelException("Cancel in Test"));
        ArrayList<BranchOffice> tempList = new ArrayList<>();
        tempList.add(source);
        tempList.add(destination);
        when(company.getBranchOffices()).thenReturn(ImmutableList.copyOf(tempList));
        Mockito.doNothing().when(source).delegateTask(any(), any());

        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());
        delegateTaskController.delegateTask();
    }


}
