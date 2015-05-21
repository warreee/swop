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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testklasse voor de klasse Plan.
 */
public class PlanTest {

    private Task task;
    private ResourceRepository resourceRepository;
    private ResourcePlanner resourcePlanner;
    private AResourceType type1, type2;
    private ResourceInstance instance1a, instance1b, instance2a, instance2b;
    LocalDateTime startTime, endTime;

    private Plan plan;

    @Before
    public void setUp() {
        // starttijd
        startTime = LocalDateTime.of(2015,1,15,9,0);
        // duur
        Duration duration = Duration.ofHours(1);
        // eindtijd van resource types wanneer ze vanaf starttijd voor duration worden gereserveerd
        endTime = startTime.plus(duration);

        // branch office en resource planner
        BranchOffice branchOffice = mock(BranchOffice.class);
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
        when(task.getDelegatedTo()).thenReturn(branchOffice);
        when(task.isPlanned()).thenReturn(false);
        when(branchOffice.containsTask(eq(task))).thenReturn(true);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(branchOffice.getUnplannedTasks()).thenReturn(tasks);

        PlanBuilder planBuilder = new PlanBuilder(branchOffice, task, startTime);
        planBuilder.proposeResources();
        plan = planBuilder.getPlan();
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
    public void clearTest() {
        verify(resourcePlanner).removePlan(plan);


        plan.clear();
        assertTrue(plan.getTask().equals(null));
        assertTrue(plan.getReservations().isEmpty());
    }

    @Test
    public void clearFutureReservations_ValidTest() {
        plan.clearFutureReservations(startTime.plusMinutes(30));
        for (ResourceReservation reservation : plan.getReservations()) {
            assertTrue(reservation.getTimeSpan().getEndTime().equals(startTime.plusMinutes(30)));
        }
        assertTrue(plan.getTimeSpan().getEndTime().equals(startTime.plusMinutes(30)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void clearFutureReservations_InValidTest() {
        plan.clearFutureReservations(endTime.plusMinutes(1));
    }

    /**
     * Test die nagaat of er bij niet-overlappende resources een equivalent plan bestaat
     * Dit moet zo zijn.
     */
    @Test
    public void hasEquivalentPlan_TrueTest() {
        when(resourcePlanner.isAvailable(any(ResourceInstance.class), eq(new TimeSpan(startTime.plusDays(1), endTime.plusDays(1))))).thenReturn(true);
        when(resourcePlanner.hasEquivalentPlan(any(), any())).thenCallRealMethod();
        assertTrue(plan.hasEquivalentPlan(startTime.plusDays(1)));
    }

    /**
     * Test die nagaat of er bij overlappende resources een equivalent plan bestaat.
     * Dit kan niet zo zijn want de resources zijn al nodig voor de andere taak.
     */
    @Test
    public void hasEquivalentPlan_FalseTest() {
        when(resourcePlanner.isAvailable(any(ResourceInstance.class), eq(new TimeSpan(startTime.plusDays(1),endTime.plusDays(1))))).thenReturn(false);
        when(resourcePlanner.hasEquivalentPlan(any(), any())).thenCallRealMethod();
        assertFalse(plan.hasEquivalentPlan(startTime.plusDays(1)));
    }

}
