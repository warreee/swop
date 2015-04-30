package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.junit.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by Arne De Brabandere_3 on 30/04/2015.
 */
public class PlanTest {

    private Plan plan;
    private ResourceManager resourceManager;
    private LocalDateTime now;

    @Before
    public void setUp() {
        now = LocalDateTime.of(2015,1,1,11,0);
        resourceManager = new ResourceManager();
        addTempDomainObjects();
        RequirementListBuilder builder = new RequirementListBuilder();
        builder.addNewRequirement(resourceManager.getDeveloperType(), 2);
        builder.addNewRequirement(resourceManager.getResourceTypeByName("Auto"), 1);
        Task task = new Task("test taak", Duration.ofHours(7), 0.1, new SystemTime(), new DependencyGraph(), builder.getRequirements());
        plan = new Plan(task, now, resourceManager);
    }

    @Test
    public void isValid_ValidTest() {
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(0));
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(1));
        plan.addReservations(developers);
        assertTrue(plan.isValid());
    }

    @Test
    public void isValid_InvalidTest() {
        assertFalse(plan.isValid());
    }

    @Test
    public void isValidWithoutDevelopers_ValidTest() {
        assertTrue(plan.isValidWithoutDevelopers());
    }

    @Test
    public void isValidWithoutDevelopers_InvalidTest() {
        plan.changeReservations(new ArrayList<>());
        assertFalse(plan.isValidWithoutDevelopers());
    }

    @Test
    public void getEndTimeTest() {
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(0));
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(1));
        plan.addReservations(developers);
        assertEquals(LocalDateTime.of(2015, 1, 2, 10, 0), plan.getEndTime());
    }

    @Test
    public void conflictingTasks_ConflictsTest() {
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(0));
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(1));
        plan.addReservations(developers);
        Task otherTask = mock(Task.class);
        resourceManager.makeReservation(otherTask, resourceManager.getDeveloperType().getResourceInstances().get(0),
                new TimeSpan(now, now.plusHours(1)), false);
        assertTrue(plan.getConflictingTasks().contains(otherTask));
        assertTrue(plan.getConflictingTasks().size() == 1);
    }

    @Test
    public void conflictingTasks_NoConflictsTest() {
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(0));
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(1));
        plan.addReservations(developers);
        assertTrue(plan.getConflictingTasks().isEmpty());
    }

    @Test
    public void applyPlan_ValidTest() {
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(0));
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(1));
        plan.addReservations(developers);
        plan.apply();
        assertTrue(resourceManager.getReservations(plan.getTask()).size() == 3);
        assertTrue(reservationsListContainsInstance(resourceManager.getReservations(plan.getTask()),
                resourceManager.getDeveloperType().getResourceInstances().get(0)));
        assertTrue(reservationsListContainsInstance(resourceManager.getReservations(plan.getTask()),
                resourceManager.getDeveloperType().getResourceInstances().get(1)));
        assertTrue(reservationsListContainsInstance(resourceManager.getReservations(plan.getTask()),
                resourceManager.getResourceTypeByName("Auto").getResourceInstances().get(0)));
    }

    @Test
    public void addReservationsTest() {
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(0));
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(1));
        plan.addReservations(developers);
        plan.apply();
        assertTrue(reservationsListContainsInstance(plan.getReservations(),
                resourceManager.getDeveloperType().getResourceInstances().get(0)));
        assertTrue(reservationsListContainsInstance(plan.getReservations(),
                resourceManager.getDeveloperType().getResourceInstances().get(1)));
    }

    private boolean reservationsListContainsInstance(List<ResourceReservation> reservations, ResourceInstance resourceInstance) {
        for (ResourceReservation reservation : reservations) {
            if (reservation.getResourceInstance() == resourceInstance) {
                return true;
            }
        }
        return false;
    }


    private void addTempDomainObjects() {

        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter SWOP");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ward");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ronald");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Robin");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Arne");

        resourceManager.addNewResourceType("Auto");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Aston Martin Rapide");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Toyota Auris");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Rolls Royce Phantom");

        resourceManager.addNewResourceType("Koets", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 1");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 2");

    }

}
