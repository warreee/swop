package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlanBuilderTest {

    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BranchOffice branchOffice;
    private ResourcePlanner resourcePlanner;
    private AResourceType type1, type2;
    private ResourceInstance instance1a, instance1b, instance2a, instance2b;

    private PlanBuilder planBuilder;
    private ResourceRepository resourceRepository;

    @Before
    public void setUp() {
        // starttijd
        startTime = LocalDateTime.of(2015,1,15,9,0);
        // duur
        Duration duration = Duration.ofHours(1);
        // eindtijd van resource types wanneer ze vanaf starttijd voor duration worden gereserveerd
        endTime = startTime.plus(duration);

        // branch office en resource planner
        branchOffice = mock(BranchOffice.class);
        resourceRepository = mock(ResourceRepository.class);

        initResources();
        when(branchOffice.getResourcePlanner()).thenReturn(resourcePlanner);
        when(branchOffice.getResourceRepository()).thenReturn(resourceRepository);

        // taak met requirements (2x type1 + 1x type2), duur=1u, niet gedelegeerd en niet gepland
        task = mock(Task.class);
        RequirementListBuilder builder = new RequirementListBuilder(resourceRepository);
        builder.addNewRequirement(type1, 2);
        builder.addNewRequirement(type2, 1);
        when(task.getRequirementList()).thenReturn(builder.getRequirements());
        when(task.getEstimatedDuration()).thenReturn(duration);
        when(task.isDelegated()).thenReturn(false);
        when(task.isPlanned()).thenReturn(false);
        when(branchOffice.containsTask(eq(task))).thenReturn(true);
        when(task.getDelegatedTo()).thenReturn(branchOffice);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(branchOffice.getUnplannedTasks()).thenReturn(tasks);

        planBuilder = new PlanBuilder(branchOffice, task, startTime);
    }

    private void initResources() {
        resourcePlanner = mock(ResourcePlanner.class);
        when(resourcePlanner.isAvailable(any(ResourceInstance.class), any(TimeSpan.class))).thenReturn(true);

        type1 = mock(ResourceType.class);
        type2 = mock(ResourceType.class);
        when(type1.getDailyAvailability()).thenReturn(new DailyAvailability(LocalTime.MIN, LocalTime.MAX));
        when(type2.getDailyAvailability()).thenReturn(new DailyAvailability(LocalTime.MIN, LocalTime.MAX));
        when(type1.calculateEndTime(anyObject(), anyObject())).thenReturn(endTime);
        when(type2.calculateEndTime(anyObject(), anyObject())).thenReturn(endTime);
        instance1a = mock(ResourceInstance.class);
        when(instance1a.getResourceType()).thenReturn(type1);
        instance1b = mock(ResourceInstance.class);
        when(instance1b.getResourceType()).thenReturn(type1);
        List<ResourceInstance> instances1 = new ArrayList<>();
        instances1.add(instance1a);
        instances1.add(instance1b);

        instance2a = mock(ResourceInstance.class);
        when(instance2a.getResourceType()).thenReturn(type2);
        instance2b = mock(ResourceInstance.class);
        when(instance2b.getResourceType()).thenReturn(type2);
        List<ResourceInstance> instances2 = new ArrayList<>();
        instances2.add(instance2a);
        instances2.add(instance2b);

        when(resourcePlanner.getInstances(eq(type1), any(TimeSpan.class))).thenReturn(instances1);
        when(resourcePlanner.getInstances(eq(type2), any(TimeSpan.class))).thenReturn(instances2);
        when(resourceRepository.getResources(eq(type1))).thenReturn(instances1);
        when(resourceRepository.getResources(eq(type2))).thenReturn(instances2);
        when(resourceRepository.amountOfResourceInstances(eq(type1))).thenReturn(instances1.size());
        when(resourceRepository.amountOfResourceInstances(eq(type2))).thenReturn(instances2.size());
    }

    @Test
    public void NewPlanBuilder_validTest() {
        PlanBuilder planBuilderTest = new PlanBuilder(branchOffice, task, startTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewPlanBuilder_planNotInBranchOfficeTest() {
        when(branchOffice.containsTask(eq(task))).thenReturn(false);
        PlanBuilder planBuilderTest = new PlanBuilder(branchOffice, task, startTime);
    }

    @Test
    public void proposeReservationsTest() {
        planBuilder.proposeResources();

        assertEquals(planBuilder.getSelectedInstances(type1).size(), 2);
        assertTrue(planBuilder.getSelectedInstances(type1).contains(instance1a));
        assertTrue(planBuilder.getSelectedInstances(type1).contains(instance1b));

        assertEquals(planBuilder.getSelectedInstances(type2).size(), 1);
        assertTrue(planBuilder.getSelectedInstances(type2).contains(instance2a)
                || planBuilder.getSelectedInstances(type2).contains(instance2b));
    }

    @Test
    public void addResourceInstance_ValidTest() {
        planBuilder.addResourceInstance(instance1a);
        planBuilder.addResourceInstance(instance1b);
        planBuilder.addResourceInstance(instance2b);

        assertTrue(planBuilder.getSelectedInstances(type1).contains(instance1a));
        assertTrue(planBuilder.getSelectedInstances(type1).contains(instance1b));
        assertTrue(planBuilder.getSelectedInstances(type2).contains(instance2b));
        assertEquals(planBuilder.getSelectedInstances(type2).size(), 1);
        assertEquals(planBuilder.getSelectedInstances(type1).size(), 2);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addResourceInstance_TooManyInstancesTest() {
        planBuilder.addResourceInstance(instance2a);
        planBuilder.addResourceInstance(instance2b);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addResourceInstance_NoRequirementsTest() {
        RequirementListBuilder requirementListBuilder = new RequirementListBuilder(resourceRepository);
        when(task.getRequirementList()).thenReturn(requirementListBuilder.getRequirements());
        planBuilder.addResourceInstance(instance1a);
    }

    @Test
     public void hasConflictingReservations_NoConflictsTest() {
        planBuilder.addResourceInstance(instance1a);
        assertFalse(planBuilder.hasConflictingReservations());
    }

    @Test
    public void hasConflictingReservations_ConflictsTest() {
        when(resourcePlanner.isAvailable(any(ResourceInstance.class), any(TimeSpan.class))).thenReturn(false);
        planBuilder.addResourceInstance(instance1a);
        assertTrue(planBuilder.hasConflictingReservations());
    }

    @Test
    public void isSatisfied_SatisfiedTest() {
        planBuilder.addResourceInstance(instance1a);
        planBuilder.addResourceInstance(instance1b);
        planBuilder.addResourceInstance(instance2a);
        assertTrue(planBuilder.isSatisfied());
    }

    @Test
    public void isSatisfied_NotSatisfiedTest() {
        planBuilder.addResourceInstance(instance1a);
        planBuilder.addResourceInstance(instance1b);
        assertFalse(planBuilder.isSatisfied());
    }

    @Test
    public void getPlan_ValidTest() {
        planBuilder.addResourceInstance(instance1a);
        planBuilder.addResourceInstance(instance1b);
        planBuilder.addResourceInstance(instance2a);
        Plan plan = planBuilder.getPlan();
        assertTrue(plan.getTask() == task);
        assertTrue(plan.getTimeSpan().equals(new TimeSpan(startTime, endTime)));
        assertTrue(plan.getReservations(type1).size() == 2);
        assertFalse(plan.getReservations(type1).stream().filter(r -> r.getResourceInstance() == instance1a).collect(Collectors.toList()).isEmpty());
        assertFalse(plan.getReservations(type1).stream().filter(r -> r.getResourceInstance() == instance1b).collect(Collectors.toList()).isEmpty());
        assertTrue(plan.getReservations(type2).size() == 1);
        assertFalse(plan.getReservations(type2).stream().filter(r -> r.getResourceInstance() == instance2a).collect(Collectors.toList()).isEmpty());
    }

    @Test (expected = IllegalArgumentException.class)
    public void getPlan_NotSatisfiedTest() {
        planBuilder.addResourceInstance(instance1a);
        planBuilder.addResourceInstance(instance1b);
        Plan plan = planBuilder.getPlan();
    }

    @Test (expected = IllegalArgumentException.class)
    public void getPlan_ConflictsTest() {
        when(resourcePlanner.isAvailable(any(ResourceInstance.class), any(TimeSpan.class))).thenReturn(false);
        planBuilder.addResourceInstance(instance1a);
        planBuilder.addResourceInstance(instance1b);
        planBuilder.addResourceInstance(instance2a);
        Plan plan = planBuilder.getPlan();
    }

}
