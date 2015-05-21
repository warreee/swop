package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.resource.DeveloperType;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.resource.ResourceRepository;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class CreateTaskScenarioTest {
    private LocalDateTime now;
    private ProjectRepository projectRepository;
    private TaskController taskController;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private ImmutableList<Project> projects;
    private List<Task> tasks;
    private LogonController logonController;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.mockedUI = mock(UserInterface.class);

        systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        projectRepository = new ProjectRepository(systemTime);
        projectRepository.setBranchOffice(branchOffice);
        addTempDomainObjects();

        projectRepository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10));

        this.logonController = mock(LogonController.class);
        when(logonController.hasIdentifiedProjectManager()).thenReturn(true);

        this.taskController = new TaskController(logonController,mockedUI);

        this.projects = projectRepository.getProjects();
        this.tasks = projects.get(0).getTasks();

        ResourceRepository resourceRepository = mock(ResourceRepository.class);

        when(logonController.getBranchOffice()).thenReturn(branchOffice);
        when(branchOffice.getProjectRepository()).thenReturn(projectRepository);
        ResourcePlanner resourcePlanner = new ResourcePlanner(resourceRepository, systemTime);
        when(branchOffice.getResourcePlanner()).thenReturn(resourcePlanner);
        when(branchOffice.getResourceRepository()).thenReturn(resourceRepository);
        when(resourceRepository.getPresentResourceTypes()).thenReturn(ImmutableList.copyOf(new ArrayList<>()));

        DeveloperType developerType = mock(DeveloperType.class);
        when(resourceRepository.getDeveloperType()).thenReturn(developerType);


        //when(developerType.getConstraintFor(developerType).isAcceptableAmount(any(), anyInt())).thenReturn(true);
    }

    @Test
    public void createTask_validTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(1);
        when(mockedUI.requestNumberBetween(anyString(), anyInt(), anyInt())).thenReturn(1);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));

        taskController.createTask();
    }

    @Test(expected = StopTestException.class)
    public void createTask_CancelBeschrijvingTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }

    @Test(expected = StopTestException.class)
    public void createTask_CancelDeviationTest() throws Exception {
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }

    @Test(expected = StopTestException.class)
    public void createTask_CancelDurationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenThrow(new CancelException("Cancel in test"));
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        //Cancel exception wordt opgevangen in de controller.
        taskController.createTask();
    }


    @Test (expected = StopTestException.class)
    public void createProject_invalidDescriptionTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDeviationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(-50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createProject_invalidDurationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(-8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    private void addTempDomainObjects() {

//        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter SWOP");
//        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ward");
//        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ronald");
//        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Robin");
//        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Arne");
//
//        resourceManager.addNewResourceType("Auto");
//        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Aston Martin Rapide");
//        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Toyota Auris");
//        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Rolls Royce Phantom");
//
//        resourceManager.addNewResourceType("Koets", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
//        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 1");
//        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 2");

    }

}
