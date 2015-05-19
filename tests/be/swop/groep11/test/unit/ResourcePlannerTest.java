package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by robin on 15/05/15.
 */
public class ResourcePlannerTest {

    BranchOffice branchOffice;
    ResourcePlanner planner;
    ResourceTypeRepository typeRepository;
    ResourceRepository repository;
    private SystemTime systemTime;

    @Before
    public void setUp() throws Exception {
        systemTime = new SystemTime();


        // TODO: al wat plannen met reservaties toevoegen.
        typeRepository = new ResourceTypeRepository();
        typeRepository.addNewResourceType("type a");
        typeRepository.addNewResourceType("type b");
        typeRepository.addNewResourceType("type c");
        typeRepository.addNewResourceType("type d", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(14,0)));
        typeRepository.addNewResourceType("type e");
        repository = new ResourceRepository(typeRepository);
        repository.addResourceInstance(new Resource("type a 1", typeRepository.getResourceTypeByName("type a")));
        repository.addResourceInstance(new Resource("type a 2", typeRepository.getResourceTypeByName("type a")));
        repository.addResourceInstance(new Resource("type a 3", typeRepository.getResourceTypeByName("type a")));
        repository.addResourceInstance(new Resource("type b 1", typeRepository.getResourceTypeByName("type b")));
        repository.addResourceInstance(new Resource("type b 2", typeRepository.getResourceTypeByName("type b")));
        repository.addResourceInstance(new Resource("type c 1", typeRepository.getResourceTypeByName("type c")));
        repository.addResourceInstance(new Resource("type d 1", typeRepository.getResourceTypeByName("type d")));
        repository.addResourceInstance(new Resource("type d 2", typeRepository.getResourceTypeByName("type d")));
        repository.addResourceInstance(new Resource("type d 3", typeRepository.getResourceTypeByName("type d")));
        planner = new ResourcePlanner(repository,systemTime);

        branchOffice = mock(BranchOffice.class);
        Task task = mock(Task.class);
        when(branchOffice.getUnplannedTasks()).thenReturn(Arrays.<Task>asList(task));
        RequirementListBuilder builder = new RequirementListBuilder(repository);
        builder.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 2);
        when(task.getRequirementList()).thenReturn(builder.getRequirements());
        when(task.getEstimatedDuration()).thenReturn(Duration.ofHours(3));
        PlanBuilder planBuilder = new PlanBuilder(branchOffice, task, LocalDateTime.of(2015, 3, 1, 10 ,0));

        planner = new ResourcePlanner(repository, systemTime);
    }

    @Test
    public void testCanPlan() throws Exception {
        Task task1 = mock(Task.class);
        RequirementListBuilder builder = new RequirementListBuilder(repository);
        builder.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 2);
        when(task1.getRequirementList()).thenReturn(builder.getRequirements());
        assertTrue(planner.canPlan(task1));
        // TODO: planner met meerdere branchOffices enz
    }

    @Test
    public void testIsAvailable() throws Exception {
        fail();
    }

    @Test
    public void testIsAvailable1() throws Exception {
        fail();
    }

    @Test
    public void testIsAvailable2() throws Exception {
        fail();
    }

    @Test
    public void testAddPlan() throws Exception {
        fail();
    }

    @Test
    public void testGetNextPossibleTimeSpans() throws Exception {
        fail();
    }

    @Test
    public void testGetNextPossibleStartTimes() throws Exception {
        fail();
    }

    @Test
    public void testGetAvailableInstances() throws Exception {
        fail();
    }

    @Test
    public void MementoTest() throws Exception {
        ResourceInstance instance = repository.getResources(typeRepository.getResourceTypes().get(0)).get(0);
        TimeSpan timeSpan = new TimeSpan(LocalDateTime.of(2015,5,19,8,0), LocalDateTime.of(2015,5,19,10,0));
        IResourcePlannerMemento memento = planner.createMemento();

        // instance == beschikbaar
        assertTrue(planner.isAvailable(instance, timeSpan));

        // setup plan
        Task task = mock(Task.class);
        List<ResourceReservation> reservations = new ArrayList<>();
        reservations.add(new ResourceReservation(task, instance, timeSpan, true));
        Plan plan = mock(Plan.class);
        when(plan.getTask()).thenReturn(task);
        when(plan.getReservations()).thenReturn(ImmutableList.copyOf(reservations));
        when(plan.getReservations(anyObject())).thenReturn(ImmutableList.copyOf(reservations));
        when(plan.getTimeSpan()).thenReturn(timeSpan);
        when(task.getResourcePlannerObserver()).thenReturn(resourcePlanner -> { });

        // add plan to resource planner
        planner.addPlan(plan);

        // instance != beschikbaar
        assertFalse(planner.isAvailable(instance, timeSpan));

        planner.setMemento(memento);

        // instance == beschikbaar
        assertTrue(planner.isAvailable(instance, timeSpan));
    }
}