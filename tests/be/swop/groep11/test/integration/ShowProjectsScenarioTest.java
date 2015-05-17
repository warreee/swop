package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.ProjectController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class ShowProjectsScenarioTest {

    private ProjectRepository projectRepository;
    private ImmutableList<Project> projects;
    private ImmutableList<Task> tasks;
    private UserInterface mockedUI;
    private ProjectController projectController;
    private BranchOffice branchOffice;

    @Before
    public void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        SystemTime systemTime = new SystemTime(now);
        this.mockedUI = mock(UserInterface.class);
        this.branchOffice = mock(BranchOffice.class);

        projectRepository = new ProjectRepository(systemTime);

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(),now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("TestTaak", 0.5, Duration.ofHours(8));

        this.projectController = new ProjectController(projectRepository, mockedUI);
        this.projects = projectRepository.getProjects();
        this.tasks = projects.get(0).getTasks();
    }

    @Test
    public void showProjects_validTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));

        projectController.showProjects();
    }

    @Test(expected = StopTestException.class)
    public void showProjects_CancelSelectProjectTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenReturn(tasks.get(0));
        when(mockedUI.selectProjectFromList(projects)).thenThrow(new CancelException("Cancel in Test"));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        projectController.showProjects();
    }

    @Test(expected = StopTestException.class)
    public void showProjects_CancelSelectTaskTest() throws Exception {
        //stubbing
        when(mockedUI.selectTaskFromList(tasks)).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        projectController.showProjects();
    }
}
