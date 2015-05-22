package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.DelegateTaskController;
import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.SimulationController;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.InterruptedAProcedureException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test om de usecase delegate task scenario na te gaan.
 */
public class DelegateTaskScenarioTest {

    private LocalDateTime now;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private DelegateTaskController delegateTaskController;
    private Company company;
    private BranchOffice branchOffice, branchOffice2, branchOffice3;
    private ProjectRepository projectRepository;
    private ResourcePlanner resourcePlanner;
    private Developer devA, devB;
    private Task taskA, taskB;
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

        this.delegateTaskController = new DelegateTaskController(mockedUI,company,logonController);
    }


    private void addTempDomainObjects() {
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();
        company = new Company("bedrijf", resourceTypeRepository, systemTime);

        projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
        resourcePlanner = new ResourcePlanner(resourceRepository, systemTime);
        branchOffice = new BranchOffice("Branch Office 1", "Leuven", projectRepository, resourcePlanner);
        branchOffice2 = new BranchOfficeProxy(new BranchOffice("Branch Office 2", "Mechelen", new ProjectRepository(systemTime), resourcePlanner));
        branchOffice3 = new BranchOfficeProxy(new BranchOffice("Branch Office 3", "China", new ProjectRepository(systemTime), new ResourcePlanner(new ResourceRepository(resourceTypeRepository), systemTime)));
        company.addBranchOffice(branchOffice);
        company.addBranchOffice(new BranchOfficeProxy(branchOffice2));
        company.addBranchOffice(new BranchOfficeProxy(branchOffice3));

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
    }

    /**
     * Test voor het tonen van de ongeplande taken in de ingelogde branchoffice.
     * Hierna wordt er 1 taak geselecteerd.
     * Test voor het tonen van de branchoffices. Hierna wordt er 1 geselecteerd.
     */
    @Test
    public void delegateTaskTest() {
        when(logonController.getBranchOffice()).thenReturn(branchOffice);

        when(mockedUI.selectTaskFromList(any())).thenReturn(taskA);
        when(mockedUI.selectBranchOfficeFromList(any())).thenReturn(branchOffice2);

        delegateTaskController.delegateTask();

        assertTrue(branchOffice2.getUnplannedTasks().contains(taskA));
        assertTrue(taskA.getDelegatedTo().equals(branchOffice2));
    }

    /**
     * Test voor het tonen van de branch offices en daarna cancel
     */

    @Test
    public void delegateTaskShowBranchOfficesCancelTest() {
        when(logonController.getBranchOffice()).thenReturn(branchOffice);

        when(mockedUI.selectTaskFromList(any())).thenReturn(taskA);
        when(mockedUI.selectBranchOfficeFromList(any())).thenThrow(new CancelException("Cancel in Test"));

        try {
            delegateTaskController.delegateTask();
        } catch (InterruptedAProcedureException e) {
            // ok
        }

        assertTrue(branchOffice.getUnplannedTasks().contains(taskA));
        assertTrue(taskA.getDelegatedTo().equals(branchOffice));
        verify(mockedUI).printException(any(CancelException.class));
    }

    /**
     * Test voor het tonen van de taken en daarna cancel
     */
    @Test
    public void delegateTaskShowUnplannedTasksCancelTest() {
        when(logonController.getBranchOffice()).thenReturn(branchOffice);

        when(mockedUI.selectTaskFromList(any())).thenThrow(new CancelException("Cancel in Test"));
        when(mockedUI.selectBranchOfficeFromList(any())).thenReturn(branchOffice2);

        try {
            delegateTaskController.delegateTask();
        } catch (InterruptedAProcedureException e) {
            // ok
        }

        assertTrue(branchOffice.getUnplannedTasks().contains(taskA));
        assertTrue(taskA.getDelegatedTo().equals(branchOffice));
        verify(mockedUI).printException(any(CancelException.class));
    }

    /**
     * Delegatie naar branch office 3, die geen resource instanties heeft en dus de taak niet kan plannen.
     */
    @Test
    public void delegateTask_InvalidBranchOfficeTest() {
        when(logonController.getBranchOffice()).thenReturn(branchOffice);

        when(mockedUI.selectTaskFromList(any())).thenReturn(taskA);
        when(mockedUI.selectBranchOfficeFromList(any())).thenReturn(branchOffice3);

        try {
            delegateTaskController.delegateTask();
        } catch (InterruptedAProcedureException e) {
            // ok
        }

        assertTrue(branchOffice.getUnplannedTasks().contains(taskA));
        assertTrue(taskA.getDelegatedTo().equals(branchOffice));
        assertFalse(branchOffice2.getUnplannedTasks().contains(taskA));
        assertFalse(taskA.getDelegatedTo().equals(branchOffice2));
        verify(mockedUI).printException(any(IllegalArgumentException.class));
    }




}
