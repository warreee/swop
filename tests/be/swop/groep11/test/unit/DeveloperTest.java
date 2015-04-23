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

import static org.junit.Assert.fail;

public class DeveloperTest {

    /**
     * Developer "Jos" van het resource type "Developer"
     * en 4 allocaties: * wo 08/04/2015 08:00 - vr 10/04/2015 14:00 => middagpauze inbegrepen
     *                  * ma 13/04/2015 08:00 - ma 13/04/2015 12:30 => middagpauze inbegrepen
     *                  * di 14/04/2015 13:00 - di 14/04/2015 13:30 => middagpauze niet inbegrepen
     *                  * wo 15/04/2015 12:00 - wo 15/04/2015 13:00 => middagpauze inbegrepen
     */
    private Developer developer;
    private Task taskA;

    @Before
    public void setUp() throws Exception {
        ResourceManager repo = new ResourceManager();
        repo.addNewResourceType("dev",new DailyAvailability(LocalTime.of(8,0),LocalTime.of(17,0)));
        IResourceType developerType = repo.getResourceTypeByName("dev");
        developer = new Developer("Jos", developerType);

        String description = "description";
        Duration duration = Duration.ofDays(1);
        SystemTime systemTime = new SystemTime();
        Double deviation = 0.2;
        DependencyGraph dependencyGraph = new DependencyGraph();

        taskA = new Task(description, duration, deviation, systemTime, dependencyGraph);


        ResourceReservation reservation1 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,8,8,0), LocalDateTime.of(2015,4,10,14,0)),false);
        ResourceReservation reservation2 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,13,8,0), LocalDateTime.of(2015,4,13,12,30)),false);
        ResourceReservation reservation3 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,14,13,0), LocalDateTime.of(2015,4,14,13,30)),false);
        ResourceReservation reservation4 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,15,12,0), LocalDateTime.of(2015,4,15,13,0)),false);
    }

    @Test
    public void isAvailable_UnAvailableTest() {
        fail("Not implemented");
//        assertFalse(developer.isAvailable(new TimeSpan(LocalDateTime.of(2015,4,9,8,0), LocalDateTime.of(2015,4,9,16,0))));
    }

    @Test
    public void isAvailable_AvailableTest() {
        fail("Not implemented");
//        assertTrue(developer.isAvailable(new TimeSpan(LocalDateTime.of(2015,4,6,8,0), LocalDateTime.of(2015,4,7,16,0))));
    }

    @Test
    public void getNextAvailableTimeSpan_EndsOnOtherDayTest() {
        fail("Not implemented");

/*        LocalDateTime startTime = LocalDateTime.of(2015,4,9,10,0);
        Duration duration = Duration.ofHours(5);
        TimeSpan expectedTimeSpan = new TimeSpan(LocalDateTime.of(2015,4,13,13,00), LocalDateTime.of(2015,4,14,9,0));
        TimeSpan timeSpan = developer.getNextAvailableTimeSpan(startTime, duration);
        assertEquals(expectedTimeSpan.getStartTime(), timeSpan.getStartTime());
        assertEquals(expectedTimeSpan.getEndTime(), timeSpan.getEndTime());*/
    }

    @Test
    public void getNextAvailableTimeSpan_EndsOnSameDayTest() {
        fail("Not implemented");

/*        LocalDateTime startTime = LocalDateTime.of(2015,4,14,10,30);
        Duration duration = Duration.ofMinutes(90);
        TimeSpan expectedTimeSpan = new TimeSpan(LocalDateTime.of(2015,4,14,14,0), LocalDateTime.of(2015,4,14,15,30));
        TimeSpan timeSpan = developer.getNextAvailableTimeSpan(startTime, duration);
        assertEquals(expectedTimeSpan.getStartTime(), timeSpan.getStartTime());
        assertEquals(expectedTimeSpan.getEndTime(), timeSpan.getEndTime());*/
    }

    @Test
    public void getNextAvailableTimeSpan_AddBreakTest() {
        fail("Not implemented");

/*        LocalDateTime startTime = LocalDateTime.of(2015,4,14,11,0);
        Duration duration = Duration.ofMinutes(60);
        TimeSpan expectedTimeSpan = new TimeSpan(LocalDateTime.of(2015,4,14,11,00), LocalDateTime.of(2015,4,14,13,0));
        TimeSpan timeSpan = developer.getNextAvailableTimeSpan(startTime, duration);
        assertEquals(expectedTimeSpan.getStartTime(), timeSpan.getStartTime());
        assertEquals(expectedTimeSpan.getEndTime(), timeSpan.getEndTime());*/
    }

    @Test
    public void addAllocation_ValidTest() {
        fail("Not implemented");
        /*
        ResourceReservation allocationA = mock(ResourceReservation.class);
        when(allocationA.getResourceInstance()).thenReturn(developer);
        when(allocationA.getTimeSpan()).thenReturn(new TimeSpan(LocalDateTime.of(2015,4,1,8,0), LocalDateTime.of(2015,4,2,20,0)));

        ResourceReservation allocationB = mock(ResourceReservation.class);
        when(allocationB.getResourceInstance()).thenReturn(developer);
        when(allocationB.getTimeSpan()).thenReturn(new TimeSpan(LocalDateTime.of(2015, 4, 3, 8, 0), LocalDateTime.of(2015, 4, 8, 8, 0)));
        developer.addUtilization(allocationA);
        developer.addUtilization(allocationB);*/

    }

    @Test (expected=IllegalArgumentException.class)
    public void addAllocation_InValidTest1() {
        fail("Not implemented");

   /*     ResourceReservation allocationA = mock(ResourceReservation.class);
        when(allocationA.getResourceInstance()).thenReturn(developer);
        when(allocationA.getTimeSpan()).thenReturn(new TimeSpan(LocalDateTime.of(2015,4,1,8,0), LocalDateTime.of(2015,4,28,20,0)));

        developer.addUtilization(allocationA);*/
    }

    @Test (expected=IllegalArgumentException.class)
    public void addAllocation_InValidTest2() {
        fail("Not implemented");
        /*
        ResourceReservation allocationB = mock(ResourceReservation.class);
        when(allocationB.getResourceInstance()).thenReturn(developer);
        when(allocationB.getTimeSpan()).thenReturn(new TimeSpan(LocalDateTime.of(2015,4,1,8,0), LocalDateTime.of(2015,4,8,9,0)));

        developer.addUtilization(allocationB);
        */
    }

}
