package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by robin on 23/04/15.
 */
public class ResourceManagerTest {

    private AResourceType typeA;
    private AResourceType typeB;

    private ResourceManager resourceManager;

    @Before
    public void setUp() throws Exception{
        resourceManager = new ResourceManager();
        resourceManager.addNewResourceType("A");
        resourceManager.addNewResourceType("B");
        typeA = resourceManager.getResourceTypeByName("A");
        typeB = resourceManager.getResourceTypeByName("B");
    }

    @Test (expected = NoSuchElementException.class)
    public void emptyResourceManagerTest() throws Exception {
        // Een lege ResourceManager zou geen types mogen bevatten. Ook niet het Developer type.
        ResourceManager resourceManager = new ResourceManager();

        assertTrue(resourceManager.getResourceTypes().isEmpty());
        assertFalse(resourceManager.containsType("Developer"));
        resourceManager.getResourceTypeByName("Developer");
    }

    @Test
    public void testBasicResourceType() throws Exception {
        ResourceType type = new ResourceType("A");
        assertEquals("A", type.getName());
        assertEquals(0, type.amountOfInstances());
        assertEquals(0, type.amountOfConstraints());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testName_Invalid() throws Exception {
        new ResourceType("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithDailyAvailability_invalid() throws Exception {
        resourceManager.withDailyAvailability(typeA,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithRequirementConstraint_invaid() throws Exception {
        resourceManager.withRequirementConstraint(typeA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithConflictConstraint_invalid() throws Exception {
        resourceManager.withConflictConstraint(typeA, null);
    }

    @Test
    public void testWithDailyAvailability() throws Exception {
        LocalTime start = LocalTime.of(10, 10), end = LocalTime.of(15, 10);
        DailyAvailability availability = new DailyAvailability(start,end);

        resourceManager.withDailyAvailability(typeA, availability);

        assertEquals(end,typeA.getDailyAvailability().getEndTime());
        assertEquals(start, typeA.getDailyAvailability().getStartTime());
    }

    @Test
    public void testWithRequirementConstraints_valid() throws Exception {
        resourceManager.withRequirementConstraint(typeA, typeB);
        assertTrue(typeA.hasConstraintFor(typeB));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithConstraints_invalid() throws Exception {
        resourceManager.withRequirementConstraint(typeA, typeB);
        resourceManager.withConflictConstraint(typeB, typeA);
    }

    @Test
    public void testSelfConflictingConstraint() throws Exception {
        resourceManager.withConflictConstraint(typeA, typeA);
        assertTrue(typeA.hasConstraintFor(typeA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelfRequireConstraint_invalid() throws Exception {
        resourceManager.withRequirementConstraint(typeA, typeA);
    }


    /**
     * Test of alle methodes om een ResourceType toe te voegen.
     * @throws Exception
     */
    @Test
    public void addResourceTypeToResourceManagerTest() throws Exception {
        // TODO: conflict met zichzelf
        this.resourceManager = new ResourceManager();
        addResourceTypeNameOnly("Test Resource 1");
        assertEquals(1, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource 1"); // Mag geen exception gooien
        assertTrue(resourceManager.containsType("Test Resource 1"));

        addResourceTypeNameDailyAvailability("Test Resource 2", LocalTime.of(8, 0), LocalTime.of(16, 0));
        assertEquals(2, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource 2"); // Mag geen exception gooien
        assertTrue(resourceManager.containsType("Test Resource 2"));

        addResourceTypeNameConflictRequires("Test Resource 3", new ArrayList<Integer>(Arrays.asList(0)), new ArrayList<Integer>(Arrays.asList(1)));
        assertEquals(3, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource 3"); // Mag geen exception gooien
        assertTrue(resourceManager.containsType("Test Resource 3"));

        addResourceTypeNameDailyAvailabilityConflictRequires("Test Resource 4", LocalTime.of(8, 0), LocalTime.of(16, 0), new ArrayList<Integer>(Arrays.asList(0)), new ArrayList<Integer>(Arrays.asList(1)));
        assertEquals(4, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource 4"); // Mag geen exception gooien
        assertTrue(resourceManager.containsType("Test Resource 4"));
    }

    /**
     * Voegt een ResourceInstantie toe via de ResourceManager.
     * @throws Exception
     */
    @Test
    public void addResourceInstanceTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        assertTrue(type1.getResourceInstances().get(0).getName().equals("Instance 1"));
        assertEquals(1, type1.getResourceInstances().size());
    }

    /**
     * Voegt een ResourceInstantie toe met een lege naam. Dit zou een IllegalArgumentException moeten gooien.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void addEmptyNameInstanceTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "");
    }

    /**
     * Voegt een ResourceInstantie toe met een 'null' naam. Dit zou een IllegalArgumentException moeten gooien.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void addNullNameInstanceTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doubleNameInstanceTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        addResourceTypeNameOnly("Test Resource 1");
    }

    @Test
    public void containsTypeTest() throws Exception {
        assertFalse(resourceManager.containsType("Car"));
        addResourceTypeNameOnly("Car");
        assertTrue(resourceManager.containsType("Car"));
    }

    /**
     * Test de methode om reservaties toe te voegen via de ResourceManager.
     * @throws Exception
     */
    @Test
    public void makeReservationTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        Task mockedTask = mock(Task.class);
        LocalDateTime start = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime end = LocalDateTime.of(2015, 3, 10, 16, 0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(start, end), false);
        assertFalse(resourceManager.isAvailable(type1.getResourceInstances().get(0), new TimeSpan(start, end)));
        assertTrue(resourceManager.isAvailable(type1.getResourceInstances().get(0), new TimeSpan(end, LocalDateTime.of(2015, 3, 10, 17, 0))));
    }

    /**
     * Test of er een IllegalArgumentException wordt gegooid indien er een dubbele reservatie wordt gedaan.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void makeDoubleReservationTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        Task mockedTask = mock(Task.class);
        LocalDateTime start1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2015, 3, 10, 13, 0);
        LocalDateTime end1 = LocalDateTime.of(2015, 3, 10, 16, 0);
        LocalDateTime end2 = LocalDateTime.of(2015, 3, 10, 14, 0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(start1, end1), false);
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(start2, end2), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeNullReservationTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        LocalDateTime start = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime end = LocalDateTime.of(2015, 3, 10, 16, 0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.makeReservation(null, type1.getResourceInstances().get(0), new TimeSpan(start, end), false);
    }

    /**
     * Test de methode getNextAvailableTimeSpan.
     * @throws Exception
     */
    @Test
    public void nextAvailableTimeSpan_ReservationsTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        Task mockedTask = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        TimeSpan timeSpan = resourceManager.getNextAvailableTimeSpan(type1.getResourceInstances().get(0), time1, Duration.ofHours(1));
        assertEquals(time1, timeSpan.getStartTime());
        assertEquals(time2, timeSpan.getEndTime());

        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(time1, time2), false);
        timeSpan = resourceManager.getNextAvailableTimeSpan(type1.getResourceInstances().get(0), time1, Duration.ofHours(1));
        assertEquals(time1.plusHours(1), timeSpan.getStartTime());
        assertEquals(time2.plusHours(1), timeSpan.getEndTime());
    }

    @Test
    public void nextAvailableTimeSpan_DailyAvailabilitiesTest() throws Exception {
        addResourceTypeNameDailyAvailability("Test Resource 1", LocalTime.of(12, 0), LocalTime.of(13, 0));
        AResourceType type1 = resourceManager.getResourceTypeByName("Test Resource 1");
        resourceManager.addResourceInstance(type1, "Instance 1");
        Task mockedTask = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 11, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        TimeSpan timeSpan = resourceManager.getNextAvailableTimeSpan(type1.getResourceInstances().get(0), time1, Duration.ofHours(1));
        assertEquals(time1, timeSpan.getStartTime());
        assertEquals(time2, timeSpan.getEndTime());
    }

    @Test
    public void getAvailableInstancesTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        Task mockedTask = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        // Er is 1 instantie die nog niet gereserveerd is.
        List<ResourceInstance> resourceInstanceList = resourceManager.getAvailableInstances(type1, time1, Duration.ofHours(1));
        assertEquals(type1.getResourceInstances().get(0), resourceInstanceList.get(0));
        assertEquals(1, resourceInstanceList.size());

        // Maak een reservatie zodat niks meer beschikbaar is.
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(time1, time2), false);
        resourceInstanceList = resourceManager.getAvailableInstances(type1, time1, Duration.ofHours(1));
        assertTrue(resourceInstanceList.isEmpty());

        // Controleer dat na de reservatie de instantie terug beschikbaar is.
        resourceInstanceList = resourceManager.getAvailableInstances(type1, time2, Duration.ofHours(1));
        assertEquals(type1.getResourceInstances().get(0), resourceInstanceList.get(0));
        assertEquals(1, resourceInstanceList.size());
    }


    @Test
    public void getAvailableInstancesResourceTypeTimeSpanTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        Task mockedTask = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        // Er is 1 instantie die nog niet gereserveerd is.
        List<ResourceInstance> resourceInstanceList = resourceManager.getAvailableInstances(type1, new TimeSpan(time1, time2));
        assertEquals(type1.getResourceInstances().get(0), resourceInstanceList.get(0));
        assertEquals(1, resourceInstanceList.size());

        // Maak een reservatie zodat niks meer beschikbaar is.
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(time1, time2), false);
        resourceInstanceList = resourceManager.getAvailableInstances(type1, new TimeSpan(time1, time2));
        assertTrue(resourceInstanceList.isEmpty());

        // TimeSpan van 0 minuten tijdens een moment dat alles gereserveerd is.
        resourceInstanceList = resourceManager.getAvailableInstances(type1, new TimeSpan(time1, time1.plusMinutes(1)));
        assertTrue(resourceInstanceList.isEmpty());
    }

    @Test
    public void getReservationsTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        Task mockedTask1 = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(0), new TimeSpan(time1, time2), false);
        assertEquals(1, resourceManager.getReservations().size());
        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(1), new TimeSpan(time1, time2), false);
        assertEquals(2, resourceManager.getReservations().size());
    }

    @Test
    public void getTaskReservationsTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        Task mockedTask1 = mock(Task.class);
        Task mockedTask2 = mock(Task.class);
        Task mockedTask3 = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        // Voer wat reservaties uit en kijk of alle lijsten lang genoeg zijn.
        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(0), new TimeSpan(time1, time2), false);
        resourceManager.makeReservation(mockedTask2, type1.getResourceInstances().get(0), new TimeSpan(time1.plusHours(2), time2.plusHours(3)), false);
        assertEquals(1, resourceManager.getReservations(mockedTask1).size());
        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(1), new TimeSpan(time1, time2), false);
        assertEquals(2, resourceManager.getReservations(mockedTask1).size());

        // Een onbekende taak moet een lege lijst terug geven.
        assertTrue(resourceManager.getReservations(mockedTask3).isEmpty());
    }

    @Test
    public void getResourceInstanceReservationsTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        Task mockedTask1 = mock(Task.class);
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);

        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(0), new TimeSpan(time1, time2), false);
        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(1), new TimeSpan(time1, time2), false);
        assertEquals(1, resourceManager.getReservations(type1.getResourceInstances().get(0)).size());
        resourceManager.makeReservation(mockedTask1, type1.getResourceInstances().get(0), new TimeSpan(time1.plusHours(2), time2.plusHours(3)), false);
        assertEquals(2, resourceManager.getReservations(type1.getResourceInstances().get(0)).size());
    }

    @Test
    public void getNextPlans_NoConflictingReservationsTest() throws Exception {

        LocalDateTime t1 = LocalDateTime.of(2015,4,20, 8,0);
        LocalDateTime t2 = LocalDateTime.of(2015,4,22,17,0);
        LocalDateTime t3 = LocalDateTime.of(2015,4,23,8,0);
        LocalDateTime t4 = LocalDateTime.of(2015,4,23,11,0);

        // resource manager
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypeByName("Test Resource 1");
        addResourceTypeNameDailyAvailability("Test Resource 2", t4.toLocalTime(), t4.toLocalTime().plusHours(3));
        AResourceType type2 = resourceManager.getResourceTypeByName("Test Resource 2");
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        resourceManager.makeReservation(mock(Task.class), type1.getResourceInstances().get(0),
                new TimeSpan(t1, t2), false);
        resourceManager.addResourceInstance(type2, "Instance 3");
        resourceManager.makeReservation(mock(Task.class), type2.getResourceInstances().get(0),
                new TimeSpan(t1, t2), false);

        // task die gepland moet worden
        RequirementListBuilder requirementListBuilder = new RequirementListBuilder();
        requirementListBuilder.addNewRequirement(type1, 2);
        requirementListBuilder.addNewRequirement(type2, 1);
        IRequirementList requirementList = requirementListBuilder.getRequirements();
        Task task = new Task("description",Duration.ofHours(1), 0.1, new SystemTime(), new DependencyGraph(), requirementList);

        // plannen maken vanaf t3
        List<IPlan> plans_t3 = resourceManager.getNextPlans(3, task, t3);
        assertEquals(t3, plans_t3.get(0).getStartTime());
        assertEquals(t4.plus(task.getEstimatedDuration()), plans_t3.get(0).getEndTime());
        assertEquals(t3.plusHours(1), plans_t3.get(1).getStartTime());
        assertEquals(t4.plus(task.getEstimatedDuration()), plans_t3.get(1).getEndTime());
        assertEquals(t3.plusHours(2), plans_t3.get(2).getStartTime());
        assertEquals(t4.plus(task.getEstimatedDuration()), plans_t3.get(2).getEndTime());

        // plannen maken vanaf t4
        List<IPlan> plans_t4 = resourceManager.getNextPlans(4, task, t4);
        assertEquals(t4, plans_t4.get(0).getStartTime());
        assertEquals(t4.plus(task.getEstimatedDuration()), plans_t4.get(0).getEndTime());
        assertEquals(t4.plusHours(1), plans_t4.get(1).getStartTime());
        assertEquals(t4.plus(task.getEstimatedDuration().plusHours(1)), plans_t4.get(1).getEndTime());
        assertEquals(t4.plusHours(2), plans_t4.get(2).getStartTime());
        assertEquals(t4.plus(task.getEstimatedDuration().plusHours(2)), plans_t4.get(2).getEndTime());
        assertEquals(t4.plusHours(3), plans_t4.get(3).getStartTime());
        assertEquals( t4.plus(task.getEstimatedDuration().plusDays(1)), plans_t4.get(3).getEndTime());
    }

    @Test (expected = IllegalArgumentException.class)
    public void makeReservationsForPlan_InvalidPlanTest() {
        LocalDateTime t1 = LocalDateTime.of(2015,4,20, 8,0);
        LocalDateTime t2 = LocalDateTime.of(2015,4,22,17,0);
        LocalDateTime t3 = LocalDateTime.of(2015,4,23,8,0);
        LocalDateTime t4 = LocalDateTime.of(2015, 4, 23, 11, 0);

        // resource manager
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypeByName("Test Resource 1");
        addResourceTypeNameDailyAvailability("Test Resource 2", t4.toLocalTime(), t4.toLocalTime().plusHours(3));
        AResourceType type2 = resourceManager.getResourceTypeByName("Test Resource 2");
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        resourceManager.makeReservation(mock(Task.class), type1.getResourceInstances().get(0),
                new TimeSpan(t1, t2), false);
        resourceManager.addResourceInstance(type2, "Instance 3");
        resourceManager.makeReservation(mock(Task.class), type2.getResourceInstances().get(0),
                new TimeSpan(t1, t2), false);

        // task die gepland moet worden
        RequirementListBuilder requirementListBuilder = new RequirementListBuilder();
        requirementListBuilder.addNewRequirement(type1, 2);
        requirementListBuilder.addNewRequirement(type2, 1);
        IRequirementList requirementList = requirementListBuilder.getRequirements();
        Task task = new Task("description",Duration.ofHours(1), 0.1, new SystemTime(), new DependencyGraph(), requirementList);

        // plan maken vanaf t1
        IPlan plan = resourceManager.getNextPlans(1, task, t1).get(0);

        List<ResourceInstance> newReservations = new ArrayList<>();
        newReservations.add(type2.getResourceInstances().get(0));
        plan.changeReservations(newReservations);

        resourceManager.makeReservationsForPlan(plan);
    }

    @Test
    public void makeReservationsForPlan_ValidPlanTest() throws Exception {

        LocalDateTime t1 = LocalDateTime.of(2015, 4, 20, 8, 0);
        LocalDateTime t2 = LocalDateTime.of(2015, 4, 22, 17, 0);
        LocalDateTime t3 = LocalDateTime.of(2015, 4, 23, 8, 0);
        LocalDateTime t4 = LocalDateTime.of(2015, 4, 23, 11, 0);

        // resource manager
        addResourceTypeNameOnly("Test Resource 1");
        AResourceType type1 = resourceManager.getResourceTypeByName("Test Resource 1");
        addResourceTypeNameDailyAvailability("Test Resource 2", t4.toLocalTime(), t4.toLocalTime().plusHours(3));
        AResourceType type2 = resourceManager.getResourceTypeByName("Test Resource 2");
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        resourceManager.makeReservation(mock(Task.class), type1.getResourceInstances().get(0),
                new TimeSpan(t1, t2), false);
        resourceManager.addResourceInstance(type2, "Instance 3");
        resourceManager.makeReservation(mock(Task.class), type2.getResourceInstances().get(0),
                new TimeSpan(t1, t2), false);

        // task die gepland moet worden
        RequirementListBuilder requirementListBuilder = new RequirementListBuilder();
        requirementListBuilder.addNewRequirement(type1, 2);
        requirementListBuilder.addNewRequirement(type2, 1);
        IRequirementList requirementList = requirementListBuilder.getRequirements();
        Task task = new Task("description", Duration.ofHours(1), 0.1, new SystemTime(), new DependencyGraph(), requirementList);

        // plan maken vanaf t4
        IPlan plan_t4 = resourceManager.getNextPlans(4, task, t4).get(0);

        resourceManager.makeReservationsForPlan(plan_t4);

        assertTrue(reservationForResourceInstanceExists(task, type1.getResourceInstances().get(0)));
        assertTrue(reservationForResourceInstanceExists(task, type1.getResourceInstances().get(1)));
        assertTrue(reservationForResourceInstanceExists(task, type2.getResourceInstances().get(0)));
    }

    private void addResourceTypeNameOnly(String name){
        resourceManager.addNewResourceType(name);
    }

    private void addResourceTypeNameDailyAvailability(String name, LocalTime start, LocalTime end){
        resourceManager.addNewResourceType(name, new DailyAvailability(start, end));
    }

    private void addResourceTypeNameDailyAvailabilityConflictRequires(String name, LocalTime start, LocalTime end, List<Integer> req, List<Integer> con){
        ArrayList<AResourceType> required = new ArrayList<>();
        ArrayList<AResourceType> conflicting = new ArrayList<>();
        req.stream().forEach(x -> required.add(resourceManager.getResourceTypes().get(x)));
        con.stream().forEach(x -> conflicting.add(resourceManager.getResourceTypes().get(x)));
        resourceManager.addNewResourceType(name, new DailyAvailability(start, end), required, conflicting);
    }

    private void addResourceTypeNameConflictRequires(String name, List<Integer> req, List<Integer> con){
        ArrayList<AResourceType> required = new ArrayList<>();
        ArrayList<AResourceType> conflicting = new ArrayList<>();
        req.stream().forEach(x -> required.add(resourceManager.getResourceTypes().get(x)));
        con.stream().forEach(x -> conflicting.add(resourceManager.getResourceTypes().get(x)));
        resourceManager.addNewResourceType(name, required, conflicting);
    }

    private boolean reservationForResourceInstanceExists(Task task, ResourceInstance resourceInstance) {
        for (ResourceReservation reservation : resourceManager.getReservations(task)) {
            if (reservation.getResourceInstance() == resourceInstance) {
                return true;
            }
        }
        return false;
    }
}