package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * Created by robin on 15/05/15.
 */
public class ResourcePlannerTest {

    ResourcePlanner planner;
    ResourceTypeRepository typeRepository;
    ResourceRepository repository;

    @Before
    public void setUp() throws Exception {
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
        planner = new ResourcePlanner(repository);
    }

    @Test
    public void testCanPlan() throws Exception {
        Task task1 = mock(Task.class);
        RequirementListBuilder builder = new RequirementListBuilder();
        builder.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 2);
        when(task1.getRequirementList()).thenReturn(builder.getRequirements());
        assertTrue(planner.canPlan(task1));
        builder.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 4);
        assertFalse(planner.canPlan(task1));
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
}