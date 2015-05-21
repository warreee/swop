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
import java.time.temporal.ChronoUnit;
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

        projectRepository.addNewProject("Proj1", "Omschrijving1", now, now.plusDays(10));
        projectRepository.getProjects().get(0).addNewTask("Taak 1", 1, Duration.ofMinutes(120), requirementListBuilderA.getRequirements());
        taskA = projectRepository.getProjects().get(0).getLastAddedTask();
        projectRepository.getProjects().get(0).addNewTask("Taak 2", 1, Duration.ofMinutes(120), requirementListBuilderB.getRequirements());
        taskB = projectRepository.getProjects().get(0).getLastAddedTask();

    }





    @Test
    public void PlanTask_NoConflictsTest() throws Exception {
        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(taskA);
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(anyString())).thenReturn(true);
        // kies als starttijd de eerste in de lijst:
        when(mockedUI.selectFromList(anyList(), any())).thenReturn(now.plusHours(1).truncatedTo(ChronoUnit.HOURS));


        //Niet zelf selecteren van resources
        when(mockedUI.requestBoolean(anyString())).thenReturn(false);
        ArrayList<ResourceInstance> resourceInstances = new ArrayList<>();
        resourceInstances.add(devA);
        //selectDevelopers
        when(mockedUI.selectMultipleFromList(anyString(), anyList(), anyList(), any(), anyBoolean(), any())).thenReturn(resourceInstances);



        planningController.planTask();

        assertTrue(branchOffice.isTaskPlanned(taskA));
    }
//
//    @Test (expected = IllegalStateException.class)
//    public void PlanTask_ConflictTest() throws Exception {
//        OldPlan plan = resourceManager.getNextPlans(1, tasks.get(1), now).get(0);
////        tasks.get(1).plan(plan); TODO
//
//        // selecteer de juiste taak:
//        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
//        // starttijd zelf kiezen:
//        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(false);
//        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
//        // reserveer de voorgestelde instanties:
//        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
//        // kies developers
//        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
//            @Override
//            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
//                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
//                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
//                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
//                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
//                gekozenDevelopers.add(dev1);
//                gekozenDevelopers.add(dev2);
//                gekozenDevelopers.add(dev3);
//                return gekozenDevelopers;
//            }
//        });
//
//        planningController.planTask();
//    }
//
//    @Test
//    public void PlanTask_ResolveConflict_MoveCurrentTaskTest() throws Exception {
//        OldPlan plan = resourceManager.getNextPlans(1, tasks.get(1), now).get(0);
//        List<ResourceInstance> developers = new ArrayList<>();
//        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(3));
//        plan.addReservations(developers);
////        tasks.get(1).plan(plan); TODO
//
//        // selecteer de juiste taak:
//        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
//        // starttijd zelf kiezen: maar de 2e keer niet zelf kiezen (bij resolve conflicts)
//        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(false).thenReturn(true);
//        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
//        // kies als starttijd de eerste in de lijst (2de keer):
//        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
//            @Override
//            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                return ((List<LocalDateTime>) args[0]).get(0);
//            }
//        });
//        // reserveer de voorgestelde instanties:
//        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
//        // kies developers
//        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
//            @Override
//            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
//                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
//                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
//                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
//                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
//                gekozenDevelopers.add(dev1);
//                gekozenDevelopers.add(dev2);
//                gekozenDevelopers.add(dev3);
//                return gekozenDevelopers;
//            }
//        });
//
//        // resolve conflict
//        when(mockedUI.requestBoolean(Matchers.contains("conflicterende"))).thenReturn(false);
//
//        planningController.planTask();
//    }
//
//    @Test
//    public void PlanTask_ResolveConflict_MoveOtherTasksTest() throws Exception {
//        OldPlan plan = resourceManager.getNextPlans(1, tasks.get(1), now).get(0);
//        List<ResourceInstance> developers = new ArrayList<>();
//        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(3));
//        plan.addReservations(developers);
////        tasks.get(1).plan(plan); TODO
//
//        // selecteer de juiste taak:
//        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
//        // starttijd zelf kiezen: maar de 2e keer niet zelf kiezen (bij resolve conflicts)
//        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(false).thenReturn(true);
//        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
//        // kies als starttijd de eerste in de lijst (2de keer):
//        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
//            @Override
//            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                return ((List<LocalDateTime>) args[0]).get(0);
//            }
//        });
//        // reserveer de voorgestelde instanties:
//        when(mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
//        // kies developers
//        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
//            @Override
//            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
//                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
//                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
//                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
//                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
//                gekozenDevelopers.add(dev1);
//                gekozenDevelopers.add(dev2);
//                gekozenDevelopers.add(dev3);
//                return gekozenDevelopers;
//            }
//        });
//
//        // resolve conflict
//        when(mockedUI.requestBoolean(Matchers.contains("conflicterende"))).thenReturn(true);
//
//        planningController.planTask();
//    }
//
//    @Test
//    public void PlanTask_ResolveConflict_SelectResoourcesTest() throws Exception {
//        // selecteer de juiste taak:
//        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
//        // starttijd zelf kiezen:
//        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(true);
//        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
//        // kies als starttijd de eerste in de lijst:
//        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
//            @Override
//            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                return ((List<LocalDateTime>) args[0]).get(0);
//            }
//        });
//        // reserveer zelf de resource instanties:
//        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(false);
//        // kies resoruces
//        when(mockedUI.selectMultipleFromList(Matchers.contains("instanties"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
//            @Override
//            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
//                List<ResourceInstance> gekozenInstanties = new ArrayList<ResourceInstance>();
//                ResourceInstance inst1 = resourceManager.getResourceTypeByName("Auto").getResourceInstances().get(0);
//                ResourceInstance inst2 = resourceManager.getResourceTypeByName("Auto").getResourceInstances().get(1);
//                gekozenInstanties.add(inst1);
//                gekozenInstanties.add(inst2);
//                return gekozenInstanties;
//            }
//        }).thenAnswer(new Answer<List<ResourceInstance>>() {
//            @Override
//            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
//                List<ResourceInstance> gekozenInstanties = new ArrayList<ResourceInstance>();
//                ResourceInstance inst1 = resourceManager.getResourceTypeByName("Koets").getResourceInstances().get(0);
//                gekozenInstanties.add(inst1);
//                return gekozenInstanties;
//            }
//        });
//        // kies developers
//        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
//            @Override
//            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
//                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
//                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
//                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
//                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
//                gekozenDevelopers.add(dev1);
//                gekozenDevelopers.add(dev2);
//                gekozenDevelopers.add(dev3);
//                return gekozenDevelopers;
//            }
//        });
//
//        // resolve conflict
//        when(mockedUI.requestBoolean(Matchers.contains("conflicterende"))).thenReturn(false);
//
//        planningController.planTask();
//    }


}
