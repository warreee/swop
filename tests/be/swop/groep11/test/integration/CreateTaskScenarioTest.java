package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.TaskController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 11/03/2015.
 */
public class CreateTaskScenarioTest {
    private LocalDateTime now;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private TaskController taskController;
    private BranchOffice branchOffice, branchOffice2;
    private ProjectRepository projectRepository;
    private List<Project> projects;
    private ResourcePlanner resourcePlanner;
    private ResourceRepository resourceRepository;
    private ResourceTypeRepository resourceTypeRepository;
    private Developer devA, devB;
    private Task taskA, taskB;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.of(2015, 1, 1, 11, 0);
        this.mockedUI = mock(UserInterface.class);

        systemTime = new SystemTime(now);

        addTempDomainObjects();

        LogonController logonController = mock(LogonController.class);
        when(logonController.hasIdentifiedProjectManager()).thenReturn(true);
        when(logonController.getBranchOffice()).thenReturn(branchOffice);
        when(logonController.getProjectManager()).thenReturn(new ProjectManager("PM"));

        this.taskController = new TaskController(mockedUI, logonController);
    }


    private void addTempDomainObjects() {
        resourceTypeRepository = new ResourceTypeRepository();

        projectRepository = new ProjectRepository(systemTime);
        resourceRepository = new ResourceRepository(resourceTypeRepository);
        resourcePlanner = new ResourcePlanner(resourceRepository, systemTime);
        branchOffice = new BranchOffice("Branch Office 1", "Leuven", projectRepository, resourcePlanner);
        branchOffice2 = new BranchOfficeProxy(new BranchOffice("Branch Office 2", "Mechelen", new ProjectRepository(systemTime), resourcePlanner));

        AResourceType devType = resourceTypeRepository.getDeveloperType();
        devA = new Developer("DevA", devType);
        devB = new Developer("DevB", devType);

        branchOffice.addEmployee(new Developer("DevA", devType));
        branchOffice.addEmployee(new Developer("DevB", devType));
        branchOffice.addEmployee(new ProjectManager("PM1"));

        resourceTypeRepository.addNewResourceType("Auto");
        AResourceType autoType = resourceTypeRepository.getResourceTypeByName("Auto");

        resourceRepository.addResourceInstance(new Resource("Aston Martin Rapide", autoType));
        resourceRepository.addResourceInstance(new Resource("Toyota Auris", autoType));
        resourceRepository.addResourceInstance(new Resource("Rolls Royce Phantom", autoType));

        resourceTypeRepository.addNewResourceType("CarWash", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        AResourceType carWashType = resourceTypeRepository.getResourceTypeByName("CarWash");

        resourceRepository.addResourceInstance(new Resource("CarWash A", carWashType));
        resourceRepository.addResourceInstance(new Resource("CarWash B", carWashType));
        resourceRepository.addResourceInstance(new Resource("CarWash C", carWashType));


        RequirementListBuilder requirementListBuilderA = new RequirementListBuilder(resourceRepository);
        requirementListBuilderA.addNewRequirement(autoType, 2);
        requirementListBuilderA.addNewRequirement(carWashType, 1);
        requirementListBuilderA.addNewRequirement(devType, 1);

        RequirementListBuilder requirementListBuilderB = new RequirementListBuilder(resourceRepository);
        requirementListBuilderB.addNewRequirement(carWashType, 2);
        requirementListBuilderB.addNewRequirement(devType, 1);

        projectRepository.addNewProject("Project 11", "Omschrijving1", now, now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("Taak 1", 1, Duration.ofMinutes(120), requirementListBuilderA.getRequirements());
        taskA = projectRepository.getProjects().get(0).getLastAddedTask();
        projectRepository.getProjects().get(0).addNewTask("Taak 2", 1, Duration.ofMinutes(120), requirementListBuilderB.getRequirements());
        taskB = projectRepository.getProjects().get(0).getLastAddedTask();
        projects = projectRepository.getProjects();

        PlanBuilder planBuilder = new PlanBuilder(branchOffice, taskB, now);
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devA);
        Plan plan = planBuilder.getPlan();
        resourcePlanner.addPlan(plan);

        systemTime.updateSystemTime(systemTime.getCurrentSystemTime().plusMinutes(1));

        taskB.execute(systemTime.getCurrentSystemTime());
        taskB.fail(systemTime.getCurrentSystemTime().plusMinutes(1));
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

        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getDescription().equals("beschrijving Test"));
        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getEstimatedDuration().equals(Duration.ofMinutes(1)));
        assertTrue(new Double(projectRepository.getProjects().get(0).getLastAddedTask().getAcceptableDeviation()).equals(new Double(0.5)));
    }

    @Test
    public void createTask_selectResourceTypesTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(1);
        when(mockedUI.requestNumberBetween(anyString(), anyInt(), anyInt())).thenReturn(1);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        when(mockedUI.requestBoolean(contains("resource types"))).thenReturn(true);
        List<Object> chosenTypes = new ArrayList<>();
        chosenTypes.add(resourceTypeRepository.getResourceTypes().get(0));
        when(mockedUI.selectMultipleFromList(contains("types"), any(), any(), any())).thenReturn(chosenTypes);

        taskController.createTask();

        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getDescription().equals("beschrijving Test"));
        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getEstimatedDuration().equals(Duration.ofMinutes(1)));
        assertTrue(new Double(projectRepository.getProjects().get(0).getLastAddedTask().getAcceptableDeviation()).equals(new Double(0.5)));
        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getRequirementList().getRequirements().size() == 1);
    }

    @Test
    public void createTask_selectDependentTasksTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(1);
        when(mockedUI.requestNumberBetween(anyString(), anyInt(), anyInt())).thenReturn(1);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));

        when(mockedUI.requestBoolean(contains("afhankelijkheid"))).thenReturn(true);
        List<Object> chosenTasks = new ArrayList<>();
        chosenTasks.add(taskA);
        when(mockedUI.selectMultipleFromList(contains("afhankelijkheden"), any(), any(), any())).thenReturn(chosenTasks);

        taskController.createTask();

        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getDescription().equals("beschrijving Test"));
        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getEstimatedDuration().equals(Duration.ofMinutes(1)));
        assertTrue(new Double(projectRepository.getProjects().get(0).getLastAddedTask().getAcceptableDeviation()).equals(new Double(0.5)));

        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getDependingOnTasks().contains(taskA));
        assertTrue(projectRepository.getProjects().get(0).getLastAddedTask().getDependingOnTasks().size() == 1);
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
    public void createTask_invalidDescriptionTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createTask_invalidDeviationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(-50));
        when(mockedUI.requestNumber(anyString())).thenReturn(8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

    @Test (expected = StopTestException.class)
    public void createTask_invalidDurationTest() throws Exception {
        //stubbing
        when(mockedUI.requestString(anyString())).thenReturn("beschrijving Test");
        when(mockedUI.requestDouble(anyString())).thenReturn(new Double(50));
        when(mockedUI.requestNumber(anyString())).thenReturn(-8);
        when(mockedUI.selectProjectFromList(projects)).thenReturn(projects.get(0));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any());

        taskController.createTask();
    }

   // private void addTempDomainObjects() {

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

   // }

}
