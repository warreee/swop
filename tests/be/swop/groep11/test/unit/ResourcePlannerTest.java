package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by robin on 15/05/15.
 */
public class ResourcePlannerTest {

    private Company company;
    private BranchOffice branchOffice1;
    private BranchOffice branchOffice2;
    private BranchOffice branchOffice3;
    private ResourcePlanner planner1;
    private ResourcePlanner planner2;
    private ResourcePlanner planner3;
    private ResourceTypeRepository typeRepository;
    private ResourceRepository repository1;
    private ResourceRepository repository2;
    private ResourceRepository repository3;
    private SystemTime systemTime;
    private Task task1;
    private Task task2;
    private Task task3;
    private Project project1;
    private Project project2;
    private Project project3;


    @Before
    public void setUp() throws Exception {
        systemTime = new SystemTime(LocalDateTime.of(2015, 5, 20, 8, 0));

        initTypeRepo();
        company = new Company("test company", typeRepository, systemTime);

        initRepo1();
        initRepo2();
        initRepo3();

        planner1 = new ResourcePlanner(repository1, systemTime);
        planner2 = new ResourcePlanner(repository2, systemTime);
        planner3 = new ResourcePlanner(repository3, systemTime);

        initBranchOffices();
        initProjects();

        initTasks();
        initPlans();
    }

    private void initTypeRepo(){
        typeRepository = new ResourceTypeRepository();
        typeRepository.addNewResourceType("type a");
        typeRepository.addNewResourceType("type b");
        typeRepository.addNewResourceType("type c");
        typeRepository.addNewResourceType("type d", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(14,0)));
        typeRepository.addNewResourceType("type e");
    }

    private void initRepo1(){
        repository1 = new ResourceRepository(typeRepository);
        repository1.addResourceInstance(new Resource("type a 1", typeRepository.getResourceTypeByName("type a")));
        repository1.addResourceInstance(new Resource("type a 2", typeRepository.getResourceTypeByName("type a")));
        repository1.addResourceInstance(new Resource("type a 3", typeRepository.getResourceTypeByName("type a")));
        repository1.addResourceInstance(new Resource("type b 1", typeRepository.getResourceTypeByName("type b")));
        repository1.addResourceInstance(new Resource("type b 2", typeRepository.getResourceTypeByName("type b")));
        repository1.addResourceInstance(new Resource("type c 1", typeRepository.getResourceTypeByName("type c")));
        repository1.addResourceInstance(new Resource("type d 1", typeRepository.getResourceTypeByName("type d")));
        repository1.addResourceInstance(new Resource("type d 2", typeRepository.getResourceTypeByName("type d")));
        repository1.addResourceInstance(new Resource("type d 3", typeRepository.getResourceTypeByName("type d")));
    }

    private void initRepo2(){
        repository2 = new ResourceRepository(typeRepository);
        repository2.addResourceInstance(new Resource("type a 1", typeRepository.getResourceTypeByName("type a")));
        repository2.addResourceInstance(new Resource("type a 2", typeRepository.getResourceTypeByName("type a")));
        repository2.addResourceInstance(new Resource("type b 1", typeRepository.getResourceTypeByName("type b")));
        repository2.addResourceInstance(new Resource("type c 1", typeRepository.getResourceTypeByName("type c")));
        repository2.addResourceInstance(new Resource("type c 2", typeRepository.getResourceTypeByName("type c")));
    }

    private void initRepo3(){
        repository3 = new ResourceRepository(typeRepository);
        repository3.addResourceInstance(new Resource("type a 1", typeRepository.getResourceTypeByName("type a")));
        repository3.addResourceInstance(new Resource("type a 2", typeRepository.getResourceTypeByName("type a")));
        repository3.addResourceInstance(new Resource("type a 3", typeRepository.getResourceTypeByName("type a")));
        repository3.addResourceInstance(new Resource("type c 1", typeRepository.getResourceTypeByName("type c")));
        repository3.addResourceInstance(new Resource("type c 2", typeRepository.getResourceTypeByName("type c")));
        repository3.addResourceInstance(new Resource("type c 3", typeRepository.getResourceTypeByName("type c")));
        repository3.addResourceInstance(new Resource("type d 1", typeRepository.getResourceTypeByName("type d")));
        repository3.addResourceInstance(new Resource("type d 2", typeRepository.getResourceTypeByName("type d")));
        repository3.addResourceInstance(new Resource("type d 3", typeRepository.getResourceTypeByName("type d")));
    }

    private void initTasks(){
        RequirementListBuilder builder1 = new RequirementListBuilder(repository1);
        builder1.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 3);
        project1.addNewTask("task1", .5, Duration.ofHours(10), builder1.getRequirements());
        task1 = project1.getTasks().get(0);

        RequirementListBuilder builder2 = new RequirementListBuilder(repository2);
        builder2.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 1);
        builder2.addNewRequirement(typeRepository.getResourceTypeByName("type b"), 1);
        builder2.addNewRequirement(typeRepository.getResourceTypeByName("type c"), 1);
        project2.addNewTask("task2", .2, Duration.ofHours(20), builder2.getRequirements());
        task2 = project2.getTasks().get(0);

        RequirementListBuilder builder3 = new RequirementListBuilder(repository3);
        builder3.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 2);
        builder3.addNewRequirement(typeRepository.getResourceTypeByName("type c"), 1);
        builder3.addNewRequirement(typeRepository.getResourceTypeByName("type d"), 1);
        project3.addNewTask("task3", .2, Duration.ofHours(3), builder3.getRequirements());
        task3 = project3.getTasks().get(0);
    }

    private void initPlans(){
        PlanBuilder builder1 = new PlanBuilder(branchOffice1, task1, LocalDateTime.of(2015, 5, 20, 10, 0));
        builder1.addResourceInstance(repository1.getResources(typeRepository.getResourceTypeByName("type a")).get(0));
        builder1.addResourceInstance(repository1.getResources(typeRepository.getResourceTypeByName("type a")).get(1));
        builder1.addResourceInstance(repository1.getResources(typeRepository.getResourceTypeByName("type a")).get(2));
        branchOffice1.getResourcePlanner().addPlan(builder1.getPlan());

        PlanBuilder builder2 = new PlanBuilder(branchOffice2, task2, LocalDateTime.of(2015, 5, 20, 14, 0));
        builder2.addResourceInstance(repository2.getResources(typeRepository.getResourceTypeByName("type a")).get(0));
        builder2.addResourceInstance(repository2.getResources(typeRepository.getResourceTypeByName("type b")).get(0));
        builder2.addResourceInstance(repository2.getResources(typeRepository.getResourceTypeByName("type c")).get(1));
        branchOffice2.getResourcePlanner().addPlan(builder2.getPlan());
    }

    private void initBranchOffices(){
        branchOffice1 = new BranchOffice("branch1", "Leuven1", new ProjectRepository(systemTime), planner1);
        branchOffice2 = new BranchOffice("branch2", "Leuven2", new ProjectRepository(systemTime), planner2);
        branchOffice3 = new BranchOffice("branch3", "Leuven3", new ProjectRepository(systemTime), planner3);
        company.addBranchOffice(branchOffice1);
        company.addBranchOffice(branchOffice2);
        company.addBranchOffice(branchOffice3);
        branchOffice1.getProjectRepository().addNewProject("test1", "test", LocalDateTime.of(2015, 5, 20, 12, 0), LocalDateTime.of(2015, 5, 22, 9, 0));
        branchOffice2.getProjectRepository().addNewProject("test2", "test", LocalDateTime.of(2015, 5, 20, 12, 0), LocalDateTime.of(2015, 5, 23, 9, 0));
        branchOffice3.getProjectRepository().addNewProject("test3", "test", LocalDateTime.of(2015, 5, 20, 12, 0), LocalDateTime.of(2015, 5, 24, 9, 0));
    }

    private void initProjects(){
        branchOffice1.getProjectRepository().addNewProject("test1", "test", LocalDateTime.of(2015, 5, 20, 12, 0), LocalDateTime.of(2015, 5, 22, 9, 0));
        branchOffice2.getProjectRepository().addNewProject("test2", "test", LocalDateTime.of(2015, 5, 20, 12, 0), LocalDateTime.of(2015, 5, 23, 9, 0));
        branchOffice3.getProjectRepository().addNewProject("test3", "test", LocalDateTime.of(2015, 5, 20, 12, 0), LocalDateTime.of(2015, 5, 24, 9, 0));
        project1 = branchOffice1.getProjectRepository().getProjects().get(0);
        project2 = branchOffice2.getProjectRepository().getProjects().get(0);
        project3 = branchOffice3.getProjectRepository().getProjects().get(0);
    }

    @Test
    public void testHasEnoughResourcesToPlan() throws Exception {
        assertTrue(planner1.hasEnoughResourcesToPlan(task1));
        assertFalse(planner2.hasEnoughResourcesToPlan(task1));
        assertTrue(planner3.hasEnoughResourcesToPlan(task1));

        assertTrue(planner1.hasEnoughResourcesToPlan(task2));
        assertTrue(planner2.hasEnoughResourcesToPlan(task2));
        assertFalse(planner3.hasEnoughResourcesToPlan(task2));
    }

    @Test
    public void testIsAvailableResourceTypeTimeSpan() throws Exception {
        assertFalse(planner1.isAvailable(typeRepository.getResourceTypeByName("type a"), new TimeSpan(LocalDateTime.of(2015, 5, 20, 10, 0), LocalDateTime.of(2015, 5, 20, 12, 0))));
        assertTrue(planner1.isAvailable(typeRepository.getResourceTypeByName("type a"), new TimeSpan(LocalDateTime.of(2015, 5, 20, 8, 0), LocalDateTime.of(2015, 5, 20, 10, 0))));
    }

    @Test
    public void testIsAvailableResourceTypeTimeSpanAmount() throws Exception {
        assertFalse(planner1.isAvailable(typeRepository.getResourceTypeByName("type a"), new TimeSpan(LocalDateTime.of(2015, 5, 20, 10, 0), LocalDateTime.of(2015, 5, 20, 12, 0)), 2));
        assertTrue(planner1.isAvailable(typeRepository.getResourceTypeByName("type a"), new TimeSpan(LocalDateTime.of(2015, 5, 20, 8, 0), LocalDateTime.of(2015, 5, 20, 10, 0)), 2));
        assertFalse(planner1.isAvailable(typeRepository.getResourceTypeByName("type a"), new TimeSpan(LocalDateTime.of(2015, 5, 20, 8, 0), LocalDateTime.of(2015, 5, 20, 10, 0)), 4));
        assertFalse(planner1.isAvailable(typeRepository.getResourceTypeByName("type a"), new TimeSpan(LocalDateTime.of(2015, 5, 20, 10, 0), LocalDateTime.of(2015, 5, 20, 12, 0)), 4));
    }

    @Test
    public void testIsAvailableResourceInstanceTimeSpan() throws Exception {
        assertTrue(planner2.isAvailable(repository2.getResources(typeRepository.getResourceTypeByName("type a")).get(1), new TimeSpan(LocalDateTime.of(2015, 5, 20, 14, 0), LocalDateTime.of(2015, 5, 20, 17, 0))));
        assertFalse(planner2.isAvailable(repository2.getResources(typeRepository.getResourceTypeByName("type a")).get(0), new TimeSpan(LocalDateTime.of(2015, 5, 20, 14, 0), LocalDateTime.of(2015, 5, 20, 17, 0))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullPlan() throws Exception {
        planner1.addPlan(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOverlappingPlans() throws Exception{
        PlanBuilder builder1 = new PlanBuilder(branchOffice3, task3, LocalDateTime.of(2015, 5, 20, 10, 0));
        PlanBuilder builder2 = new PlanBuilder(branchOffice3, task3, LocalDateTime.of(2015, 5, 20, 11, 0));
        builder1.proposeResources();
        builder2.proposeResources();
        Plan plan1 = builder1.getPlan();
        Plan plan2 = builder2.getPlan();
        branchOffice3.getResourcePlanner().addPlan(plan1);
        branchOffice3.getResourcePlanner().addPlan(plan2);
    }

    @Test
    public void testAddPlan() throws Exception{
        PlanBuilder builder1 = new PlanBuilder(branchOffice3, task3, LocalDateTime.of(2015, 5, 20, 10, 0));
        builder1.proposeResources();
        Plan plan1 = builder1.getPlan();
        branchOffice3.getResourcePlanner().addPlan(plan1);
    }


    @Test
    public void testGetNextPossibleTimeSpans() throws Exception {
        RequirementListBuilder builder1 = new RequirementListBuilder(repository1);
        builder1.addNewRequirement(typeRepository.getResourceTypeByName("type a"), 1);
        LocalDateTime start = LocalDateTime.of(2015, 5, 20, 20, 0);
        LocalDateTime end = LocalDateTime.of(2015, 5, 20, 21, 0);
        for(TimeSpan next: planner1.getNextPossibleTimeSpans(builder1.getRequirements(), LocalDateTime.of(2015, 5, 20, 11, 0), Duration.ofHours(1), 3)){
            assertTrue(start.equals(next.getStartTime()));
            assertTrue(end.equals(next.getEndTime()));
            start = start.plusHours(1);
            end = end.plusHours(1);
        }
    }

    @Test
    public void testGetNextPossibleTimeSpansDailyAvailability() throws Exception {
        RequirementListBuilder builder1 = new RequirementListBuilder(repository1);
        builder1.addNewRequirement(typeRepository.getResourceTypeByName("type d"), 1);
        LocalDateTime start = LocalDateTime.of(2015, 5, 20, 13, 0);
        LocalDateTime end = LocalDateTime.of(2015, 5, 20, 14, 0);
        List<TimeSpan> timeSpans =  planner1.getNextPossibleTimeSpans(builder1.getRequirements(), LocalDateTime.of(2015, 5, 20, 13, 0), Duration.ofHours(1), 3);

        assertTrue(start.equals(timeSpans.get(0).getStartTime()));
        assertTrue(end.equals(timeSpans.get(0).getEndTime()));
        start = start.plusHours(1);
        end = end.plusHours(19);

        assertTrue(start.equals(timeSpans.get(1).getStartTime()));
        assertTrue(end.equals(timeSpans.get(1).getEndTime()));
        start = start.plusHours(1);

        assertTrue(start.equals(timeSpans.get(2).getStartTime()));
        assertTrue(end.equals(timeSpans.get(2).getEndTime()));

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
        ResourceInstance instance = repository1.getResources(typeRepository.getResourceTypes().get(0)).get(0);
        TimeSpan timeSpan = new TimeSpan(LocalDateTime.of(2015,5,19,8,0), LocalDateTime.of(2015,5,19,10,0));
        IResourcePlannerMemento memento = planner1.createMemento();

        // instance == beschikbaar
        assertTrue(planner1.isAvailable(instance, timeSpan));

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
        planner1.addPlan(plan);

        // instance != beschikbaar
        assertFalse(planner1.isAvailable(instance, timeSpan));

        planner1.setMemento(memento);

        // instance == beschikbaar
        assertTrue(planner1.isAvailable(instance, timeSpan));
    }
}