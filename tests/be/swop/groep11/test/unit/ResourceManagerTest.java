package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.ResourceInstance;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by robin on 23/04/15.
 */
public class ResourceManagerTest {

    private ResourceManager resourceManager;

    @Before
    public void setUp() throws Exception{
        resourceManager = new ResourceManager();
    }

    @Test (expected = NoSuchElementException.class)
    public void emptyResourceManagerTest() throws Exception {
        // Een lege ResourceManager zou geen types mogen bevatten. Ook niet het Developer type.
        assertTrue(resourceManager.getResourceTypes().isEmpty());
        assertFalse(resourceManager.containsType("Developer"));
        resourceManager.getResourceTypeByName("Developer");
    }

    /**
     * Test of alle methodes om een ResourceType toe te voegen.
     * @throws Exception
     */
    @Test
    public void addResourceTypeToResourceManagerTest() throws Exception {
        // TODO: conflict met zichzelf
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "");
    }

    /**
     * Voegt een ResourceInstantie toe met een 'null' naam. Dit zou een IllegalArgumentException moeten gooien.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void addNullNameInstanceTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
        Task mockedTask = mock(Task.class);
        LocalDateTime start1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2015, 3, 10, 13, 0);
        LocalDateTime end1 = LocalDateTime.of(2015, 3, 10, 16, 0);
        LocalDateTime end2 = LocalDateTime.of(2015, 3, 10, 14, 0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(start1, end1), false);
        resourceManager.makeReservation(mockedTask, type1.getResourceInstances().get(0), new TimeSpan(start2, end2), false);
    }

    /**
     * Test de methode getNextAvailableTimeSpan.
     * @throws Exception
     */
    @Test
    public void nextAvailableTimeSpanTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
    public void getAvailableInstancesTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
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
    public void getNextPlansTest() throws Exception {
        addResourceTypeNameOnly("Test Resource 1");
        IResourceType type1 = resourceManager.getResourceTypes().get(0);
        resourceManager.addResourceInstance(type1, "Instance 1");
        resourceManager.addResourceInstance(type1, "Instance 2");
        Task mockedTask1 = mock(Task.class);
        when(mockedTask1.getEstimatedDuration()).thenReturn(Duration.ofHours(1));
        LocalDateTime time1 = LocalDateTime.of(2015, 3, 10, 12, 0);
        LocalDateTime time2 = LocalDateTime.of(2015, 3, 10, 13, 0);
        // TODO: Wachten tot Task.getRequirementsList Compleet is
        fail("Nog niet klaar jongeuh!!");

    }

    private void addResourceTypeNameOnly(String name){
        resourceManager.addNewResourceType(name);
    }

    private void addResourceTypeNameDailyAvailability(String name, LocalTime start, LocalTime end){
        resourceManager.addNewResourceType(name, new DailyAvailability(start, end));
    }

    private void addResourceTypeNameDailyAvailabilityConflictRequires(String name, LocalTime start, LocalTime end, List<Integer> req, List<Integer> con){
        ArrayList<IResourceType> required = new ArrayList<>();
        ArrayList<IResourceType> conflicting = new ArrayList<>();
        req.stream().forEach(x -> required.add(resourceManager.getResourceTypes().get(x)));
        con.stream().forEach(x -> conflicting.add(resourceManager.getResourceTypes().get(x)));
        resourceManager.addNewResourceType(name, new DailyAvailability(start, end), required, conflicting);
    }

    private void addResourceTypeNameConflictRequires(String name, List<Integer> req, List<Integer> con){
        ArrayList<IResourceType> required = new ArrayList<>();
        ArrayList<IResourceType> conflicting = new ArrayList<>();
        req.stream().forEach(x -> required.add(resourceManager.getResourceTypes().get(x)));
        con.stream().forEach(x -> conflicting.add(resourceManager.getResourceTypes().get(x)));
        resourceManager.addNewResourceType(name, required, conflicting);
    }
}