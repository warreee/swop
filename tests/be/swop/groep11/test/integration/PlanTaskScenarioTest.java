package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.PlanningController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        this.planningController = new PlanningController(logonController,systemTime,mockedUI);
    }


    private void addTempDomainObjects() {
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();

        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
        ResourcePlanner resourcePlanner1 = new ResourcePlanner(resourceRepository, systemTime);
        branchOffice = new BranchOffice("Branch Office 1", "Leuven", projectRepository, resourcePlanner1);

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
    public void testName() throws Exception {



        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskA).thenReturn(taskC);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString())).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        // kies als starttijd de eerste in de lijst:

        when(mockedUI.selectFromList(anyListOf(LocalDateTime.class), any()))
                .thenReturn(now.plusHours(1)).thenReturn(now.plusHours(1));


        //Niet zelf selecteren van resources
        ArrayList<ResourceInstance> resourceInstances = new ArrayList<>();
        resourceInstances.add(devA);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any())).thenReturn(resourceInstances);

        planningController.planTask();
        assertTrue(branchOffice.isTaskPlanned(taskA));


        //Niet zelf selecteren van resources
        when(mockedUI.requestBoolean(anyString())).thenReturn(true).thenReturn(false);
        when(mockedUI.selectFromList(anyListOf(LocalDateTime.class), any()))
                .thenReturn(now.plusHours(1)).thenReturn(now.plusHours(1));
        ArrayList<ResourceInstance> resourceInstancesB = new ArrayList<>();
        resourceInstancesB.add(devB);
        //selectDevelopers

        //resolve Conflict move TaskA && plan TaskC again
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), anyInt(), anyBoolean(), any())).thenReturn(new ArrayList<>()).thenReturn(resourceInstancesB);

        planningController.planTask();
        assertTrue(branchOffice.isTaskPlanned(taskC));



    }


}
