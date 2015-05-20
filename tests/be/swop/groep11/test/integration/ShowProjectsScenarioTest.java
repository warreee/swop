package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.ShowProjectsController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class ShowProjectsScenarioTest {

    private ProjectRepository projectRepository;
    private ImmutableList<Project> projects;
    private List<Task> tasks;
    private UserInterface mockedUI;
    private ShowProjectsController showProjectsController;
    private BranchOffice branchOffice;
    private Company company;

    @Before
    public void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        this.mockedUI = mock(UserInterface.class);
        this.branchOffice = mock(BranchOffice.class);

        projectRepository = new ProjectRepository(systemTime);

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8),mock(IRequirementList.class));

        this.showProjectsController = new ShowProjectsController(company, mockedUI, systemTime );
        this.projects = projectRepository.getProjects();
        this.tasks = projects.get(0).getTasks();
    }

    @Test
    public void showProjects_validTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(ImmutableList.copyOf(tasks))).thenReturn(tasks.get(0));
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));

        showProjectsController.showProjects();
    }

    @Test(expected = StopTestException.class)
    public void showProjects_CancelSelectProjectTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(ImmutableList.copyOf(tasks))).thenReturn(tasks.get(0));
        when(mockedUI.selectProjectFromList(projects)).thenThrow(new CancelException("Cancel in Test"));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        showProjectsController.showProjects();
    }

    @Test(expected = StopTestException.class)
    public void showProjects_CancelSelectTaskTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(ImmutableList.copyOf(tasks))).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        showProjectsController.showProjects();
    }
}
