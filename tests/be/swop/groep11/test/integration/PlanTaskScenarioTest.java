package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.PlanningController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by Arne De Brabandere_3 on 30/04/2015.
 */
public class PlanTaskScenarioTest {

    private LocalDateTime now;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private PlanningController planningController;
    private BranchOffice branchOffice;
    private Task taskB;
    private Task taskA;
    private Developer devB;
    private Developer devA;
    private Task taskC;
    private ResourcePlanner resourcePlanner ;
    private LogonController logonController;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.of(2015, 1, 1, 11, 0);
        this.mockedUI = mock(UserInterface.class);

        systemTime = new SystemTime(now);

        addTempDomainObjects();

        logonController = mock(LogonController.class);
        when(logonController.hasIdentifiedProjectManager()).thenReturn(true);
        when(logonController.getBranchOffice()).thenReturn(branchOffice);
        when(logonController.getProjectManager()).thenReturn(new ProjectManager("PM"));

        this.planningController = new PlanningController(logonController,systemTime,mockedUI);
    }


    private void addTempDomainObjects() {
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();

        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
        resourcePlanner = spy(new ResourcePlanner(resourceRepository, systemTime));
        branchOffice = new BranchOffice("Branch Office 1", "Leuven", projectRepository, resourcePlanner);

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

        RequirementListBuilder requirementListBuilderC = new RequirementListBuilder(resourceRepository);
        requirementListBuilderC.addNewRequirement(autoType, 3);
        requirementListBuilderC.addNewRequirement(devType, 1);

        projectRepository.addNewProject("Proj1", "Omschrijving1", now, now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("Taak 1", 1, Duration.ofMinutes(120), requirementListBuilderA.getRequirements());
        taskA = projectRepository.getProjects().get(0).getLastAddedTask();
        projectRepository.getProjects().get(0).addNewTask("Taak 2", 1, Duration.ofMinutes(120), requirementListBuilderB.getRequirements());
        taskB = projectRepository.getProjects().get(0).getLastAddedTask();
        projectRepository.getProjects().get(0).addNewTask("Taak 3", 1, Duration.ofMinutes(60), requirementListBuilderC.getRequirements());
        taskC = projectRepository.getProjects().get(0).getLastAddedTask();
    }


    @Test
    public void PlanTask_NoConflictsTest() throws Exception {
        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskA).thenReturn(taskB);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString())).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        // kies als starttijd de eerste in de lijst:
        when(mockedUI.selectFromList(anyList(), any())).thenReturn(now.plusHours(1)).thenReturn(now.plusHours(1));


        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstances = new ArrayList<>();
        resourceInstances.add(devA);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any())).thenReturn(resourceInstances);

        planningController.planTask();
        assertTrue(branchOffice.isTaskPlanned(taskA));

        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstancesB = new ArrayList<>();
        resourceInstancesB.add(devB);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any())).thenReturn(resourceInstancesB);

        planningController.planTask();
        assertTrue(branchOffice.isTaskPlanned(taskB));
    }



    @Test
    public void PlanTask_WithConflictsTest() throws Exception {
        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskA);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString())).thenReturn(true).thenReturn(false);
        // kies als starttijd de eerste in de lijst:
        when(mockedUI.selectFromList(anyList(), any())).thenReturn(now.plusHours(1));

        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstances = new ArrayList<>();
        resourceInstances.add(devA);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any())).thenReturn(resourceInstances);
        planningController.planTask();
        verify(resourcePlanner).getNextPossibleStartTimes(any(IRequirementList.class), any(Duration.class), anyInt());
        verify(resourcePlanner,atLeastOnce()).addPlan(any());
        assertTrue(branchOffice.isTaskPlanned(taskA));


        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskC);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString()))
                .thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false)
                .thenReturn(true).thenReturn(false);
        // kies als starttijd de eerste in de lijst:
        when(mockedUI.selectFromList(anyList(), any())).thenReturn(now.plusHours(1)).thenReturn(now.plusDays(1)).thenReturn(now.plusHours(1));

        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstancesB = new ArrayList<>();
        resourceInstancesB.add(devB);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any()))
                .thenReturn(resourceInstancesB);
        planningController.planTask();
        verify(resourcePlanner,atLeastOnce()).getNextPossibleStartTimes(any(IRequirementList.class), any(Duration.class), anyInt());
        verify(resourcePlanner,atLeastOnce()).addPlan(any());

        assertTrue(branchOffice.isTaskPlanned(taskA));
        assertTrue(branchOffice.isTaskPlanned(taskC));
        Plan planC = branchOffice.getPlanForTask(taskC);
        Plan planA = branchOffice.getPlanForTask(taskA);
        assertTrue(planC.startsBefore(planA));

    }

    @Test
    public void PlanTask_CancelInResolveConflictTest() throws Exception {
        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskA);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString())).thenReturn(true).thenReturn(false);
        // kies als starttijd de eerste in de lijst:
        when(mockedUI.selectFromList(anyList(), any())).thenReturn(now.plusHours(1));

        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstances = new ArrayList<>();
        resourceInstances.add(devA);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any())).thenReturn(resourceInstances);
        planningController.planTask();
        verify(resourcePlanner).getNextPossibleStartTimes(any(IRequirementList.class), any(Duration.class), anyInt());
        verify(resourcePlanner,atLeastOnce()).addPlan(any());
        assertTrue(branchOffice.isTaskPlanned(taskA));

        Plan planAFirst = branchOffice.getPlanForTask(taskA);
        List<ResourceReservation> resourceReservationList = planAFirst.getReservations();
        TimeSpan planATS = planAFirst.getTimeSpan();
        Task planATask = planAFirst.getTask();

        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskC);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString()))
                .thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(true).thenThrow(new CancelException("Stop test"))
                .thenReturn(true).thenReturn(false);
        // kies als starttijd de eerste in de lijst:
        when(mockedUI.selectFromList(anyList(), any())).thenReturn(now.plusHours(1)).thenReturn(now.plusDays(1)).thenReturn(now.plusHours(1));

        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstancesB = new ArrayList<>();
        resourceInstancesB.add(devB);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any()))
                .thenReturn(resourceInstancesB);
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));
        boolean catched = false;
        try {
            planningController.planTask();
        } catch (StopTestException e) {
            catched = true;
        }



        assertTrue(catched);
        assertEquals(resourceReservationList, branchOffice.getPlanForTask(taskA).getReservations());
        assertEquals(planATask,branchOffice.getPlanForTask(taskA).getTask());
        assertEquals(planATS,branchOffice.getPlanForTask(taskA).getTimeSpan());

        verify(resourcePlanner,atLeastOnce()).getNextPossibleStartTimes(any(IRequirementList.class), any(Duration.class), anyInt());
        verify(resourcePlanner,atLeastOnce()).addPlan(any());

        assertTrue(branchOffice.isTaskPlanned(taskA));
        assertTrue(!branchOffice.isTaskPlanned(taskC));


    }

}
