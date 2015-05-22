package be.swop.groep11.test.integration;


import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.PlanningController;
import be.swop.groep11.main.controllers.SimulationController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimulationScenarioTest {

    private LocalDateTime now;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private SimulationController simulationController;
    private BranchOffice branchOffice, branchOffice2;
    private ProjectRepository projectRepository;
    private ResourcePlanner resourcePlanner;
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

        this.simulationController = new SimulationController(logonController,mockedUI);
    }


    private void addTempDomainObjects() {
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();

        projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
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

        PlanBuilder planBuilder = new PlanBuilder(branchOffice, taskB, now);
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devA);
        Plan plan = planBuilder.getPlan();
        resourcePlanner.addPlan(plan);
    }

    @Test
    public void simulation_CreateTask_RealizeTest() {
        simulationController.startSimulation();

        projectRepository.getProjects().get(0).addNewTask("Taak 3", 5, Duration.ofHours(2), mock(IRequirementList.class));

        assertTrue(projectRepository.getProjects().get(0).getTasks().size() == 3);
        assertEquals("Taak 1", projectRepository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Taak 2", projectRepository.getProjects().get(0).getTasks().get(1).getDescription());
        assertEquals("Taak 3", projectRepository.getProjects().get(0).getTasks().get(2).getDescription());

        simulationController.realize();

        assertTrue(projectRepository.getProjects().get(0).getTasks().size() == 3);
        assertEquals("Taak 1", projectRepository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Taak 2", projectRepository.getProjects().get(0).getTasks().get(1).getDescription());
        assertEquals("Taak 3", projectRepository.getProjects().get(0).getTasks().get(2).getDescription());
    }

    @Test
    public void simulation_CreateTask_CancelTest() {
        simulationController.startSimulation();

        projectRepository.getProjects().get(0).addNewTask("Taak 3", 5, Duration.ofHours(2), mock(IRequirementList.class));

        assertTrue(projectRepository.getProjects().get(0).getTasks().size() == 3);
        assertEquals("Taak 1", projectRepository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Taak 2", projectRepository.getProjects().get(0).getTasks().get(1).getDescription());
        assertEquals("Taak 3", projectRepository.getProjects().get(0).getTasks().get(2).getDescription());

        simulationController.cancel();

        assertTrue(projectRepository.getProjects().get(0).getTasks().size() == 2);
        assertEquals("Taak 1", projectRepository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Taak 2", projectRepository.getProjects().get(0).getTasks().get(1).getDescription());
    }

    @Test
    public void simulation_PlanTask_RealizeTest() {
        simulationController.startSimulation();

        PlanBuilder planBuilder = new PlanBuilder(branchOffice, taskA, now.plusYears(1));
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devA);
        Plan plan = planBuilder.getPlan();
        resourcePlanner.addPlan(plan);

        assertTrue(taskA.isPlanned());

        simulationController.realize();

        assertTrue(taskA.isPlanned());
    }

    @Test
    public void simulation_PlanTask_CancelTest() {
        simulationController.startSimulation();

        PlanBuilder planBuilder = new PlanBuilder(branchOffice, taskA, now.plusYears(1));
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devA);
        Plan plan = planBuilder.getPlan();
        resourcePlanner.addPlan(plan);

        assertTrue(taskA.isPlanned());

        simulationController.cancel();

        assertFalse(taskA.isPlanned());
    }

    @Test
    public void simulation_ResolveConflictNewPlan_RealizeTest() {
        simulationController.startSimulation();

        // nieuw plan maken voor Taak 2
        resourcePlanner.removePlan(resourcePlanner.getPlanForTask(taskB));
        PlanBuilder planBuilder = new PlanBuilder(branchOffice, taskB, now);
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devB);
        Plan newPlan = planBuilder.getPlan();
        resourcePlanner.addPlan(newPlan);

        // nu Taak 1 inplannen, die niet meer zal conflicteren
        planBuilder = new PlanBuilder(branchOffice, taskA, now);
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devA);
        Plan plan = planBuilder.getPlan();
        resourcePlanner.addPlan(plan);

        assertTrue(taskA.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskA).getAssignedDevelopers().contains(devA));
        assertTrue(taskB.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskB).getAssignedDevelopers().contains(devB));

        simulationController.realize();

        assertTrue(taskA.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskA).getAssignedDevelopers().contains(devA));
        assertTrue(taskB.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskB).getAssignedDevelopers().contains(devB));
    }

    @Test
    public void simulation_ResolveConflictNewPlan_CancelTest() {
        simulationController.startSimulation();

        // nieuw plan maken voor Taak 2
        resourcePlanner.removePlan(resourcePlanner.getPlanForTask(taskB));
        PlanBuilder planBuilder = new PlanBuilder(branchOffice, taskB, now);
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devB);
        Plan newPlan = planBuilder.getPlan();
        resourcePlanner.addPlan(newPlan);

        // nu Taak 1 inplannen, die niet meer zal conflicteren
        planBuilder = new PlanBuilder(branchOffice, taskA, now);
        planBuilder.proposeResources();
        planBuilder.addResourceInstance(devA);
        Plan plan = planBuilder.getPlan();
        resourcePlanner.addPlan(plan);

        assertTrue(taskA.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskA).getAssignedDevelopers().contains(devA));
        assertTrue(taskB.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskB).getAssignedDevelopers().contains(devB));

        simulationController.cancel();

        assertFalse(taskA.isPlanned());
        assertTrue(taskB.isPlanned());
        assertTrue(resourcePlanner.getPlanForTask(taskB).getAssignedDevelopers().contains(devA));
    }

}