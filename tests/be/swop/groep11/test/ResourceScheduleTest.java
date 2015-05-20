package be.swop.groep11.test;

import be.swop.groep11.main.util.ResourceSchedule;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.UnavailableReservationException;
import be.swop.groep11.main.resource.ResourceInstance;
import be.swop.groep11.main.resource.ResourceReservation;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by Ronald on 12/05/2015.
 */
public class ResourceScheduleTest {

    private ResourceSchedule schedule;
    private ResourceInstance resource;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task task5;


    @Before
    public void setUp() throws Exception {
        this.resource = mock(ResourceInstance.class);
        this.schedule = new ResourceSchedule(resource);

        this.task1 = mock(Task.class);
        this.task2 = mock(Task.class);
        this.task3 = mock(Task.class);
        this.task4 = mock(Task.class);
        this.task5 = mock(Task.class);

    }

    @Test
    public void constructor_valid() throws Exception {
        ResourceInstance resource = mock(ResourceInstance.class);
        ResourceSchedule schedule = new ResourceSchedule(resource);
        assertEquals(resource, schedule.getResource());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_invalid() throws Exception {
        new ResourceSchedule(null);
    }

    @Test
    public void canHaveAsReservation_null_invalid() throws Exception {
        assertFalse(schedule.canHaveAsReservation(null));
    }

    @Test
    public void canHaveAsReservation_wrongResource_invalid() throws Exception {
        TimeSpan timeSpan1 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 9, 0), LocalDateTime.of(2015, 4, 9, 13, 0));
        ResourceReservation reservation = new ResourceReservation(task1, mock(ResourceInstance.class), timeSpan1, false);
        assertFalse(schedule.canHaveAsReservation(reservation));
    }

    @Test
    public void getOverlappingReservations_null_emptyList() throws Exception {
        assertTrue(schedule.getOverlappingReservations(null).isEmpty());

    }

    @Test
    public void addReservation_valid() throws Exception {
        TimeSpan ts = mock(TimeSpan.class);
        Task task = mock(Task.class);

        ResourceReservation reservation = new ResourceReservation(task, resource, ts, false);
        schedule.addReservation(reservation);

        assertEquals(reservation, schedule.getReservation(task));
    }

    @Test
    public void addMultipleReservation_valid() throws Exception {

        TimeSpan timeSpan1 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 9, 0), LocalDateTime.of(2015, 4, 9, 13, 0));
        TimeSpan timeSpan2 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 13, 0), LocalDateTime.of(2015, 4, 9, 15, 0));
        TimeSpan timeSpan3 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 15, 0), LocalDateTime.of(2015, 4, 9, 18, 0));

        ResourceReservation reservation1 = new ResourceReservation(task1, resource, timeSpan1, false);
        ResourceReservation reservation2 = new ResourceReservation(task2, resource, timeSpan2, false);
        ResourceReservation reservation3 = new ResourceReservation(task3, resource, timeSpan3, false);

        schedule.addReservation(reservation1);
        schedule.addReservation(reservation2);
        schedule.addReservation(reservation3);

        assertEquals(reservation1, schedule.getReservation(task1));
        assertEquals(reservation2, schedule.getReservation(task2));
        assertEquals(reservation3, schedule.getReservation(task3));
    }

    @Test(expected = UnavailableReservationException.class)
    public void addReservation_NotFree() throws Exception {

        TimeSpan timeSpan1 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 9, 0), LocalDateTime.of(2015, 4, 9, 12, 0));
        TimeSpan timeSpan2 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 11, 0), LocalDateTime.of(2015, 4, 9, 12, 0));

        ResourceReservation reservation1 = new ResourceReservation(task1, resource, timeSpan1, false);
        ResourceReservation reservation2 = new ResourceReservation(task2, resource, timeSpan2, false);

        schedule.addReservation(reservation1);
        schedule.addReservation(reservation2);
    }

    @Test
    public void addNewReservationBetweenExisting_valid() throws Exception {

        TimeSpan timeSpan2 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 13, 0), LocalDateTime.of(2015, 4, 9, 15, 0));
        TimeSpan timeSpan3 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 17, 0), LocalDateTime.of(2015, 4, 9, 18, 0));
        TimeSpan timeSpan5 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 15, 30), LocalDateTime.of(2015, 4, 9, 16, 30));

        ResourceReservation reservation2 = new ResourceReservation(task2, resource, timeSpan2, false);
        ResourceReservation reservation3 = new ResourceReservation(task3, resource, timeSpan3, false);
        ResourceReservation reservation5 = new ResourceReservation(task5, resource, timeSpan5, false);

        schedule.addReservation(reservation3);
        schedule.addReservation(reservation2);

        assertTrue(schedule.canHaveAsReservation(reservation5));
    }

    @Test
    public void getAvailableTime_NoReservations() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2015, 4, 9, 11, 0);
        List<TimeSpan> freeTime = schedule.getAvailableTime(dateTime);
        assertEquals(1, freeTime.size());
        assertEquals(new TimeSpan(dateTime, LocalDateTime.MAX), freeTime.get(0));
    }

    @Test
    public void getAvailableTime_NonContiguous() throws Exception {
        LocalDateTime start = LocalDateTime.of(2015, 4, 9, 8, 0);
        TimeSpan timeSpan1 = new TimeSpan(start.plusHours(1), start.plusHours(3));
        TimeSpan timeSpan2 = new TimeSpan(start.plusHours(5), start.plusHours(6));

        ResourceReservation reservation1 = new ResourceReservation(task1, resource, timeSpan1, false);
        ResourceReservation reservation2 = new ResourceReservation(task2, resource, timeSpan2, false);

        schedule.addReservation(reservation1);
        schedule.addReservation(reservation2);

        assertEquals(reservation1, schedule.getReservation(task1));
        assertEquals(reservation2, schedule.getReservation(task2));

        List<TimeSpan> freeTime = schedule.getAvailableTime(start);
        assertEquals(3, freeTime.size());
        assertEquals(new TimeSpan(start, start.plusHours(1)), freeTime.get(0));
        assertEquals(new TimeSpan(start.plusHours(3), start.plusHours(5)), freeTime.get(1));
        assertEquals(new TimeSpan(start.plusHours(6), LocalDateTime.MAX), freeTime.get(2));

    }

    @Test(expected = IllegalArgumentException.class)
    public void getAvailableTime_invalid() throws Exception {
        schedule.getAvailableTime(null);
    }

/*
    @Test
    public void test() throws Exception {
        TimeSpan timeSpan1 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 9, 0), LocalDateTime.of(2015, 4, 9, 11, 0));
        TimeSpan timeSpan2 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 13, 0), LocalDateTime.of(2015, 4, 9, 14, 0));
        TimeSpan timeSpan3 = new TimeSpan(LocalDateTime.of(2015, 4, 9, 16, 0), LocalDateTime.of(2015, 4, 9, 18, 0));

        ResourceReservation reservation1 = new ResourceReservation(task1, resource, timeSpan1, false);
        ResourceReservation reservation2 = new ResourceReservation(task2, resource, timeSpan2, false);
        ResourceReservation reservation3 = new ResourceReservation(task3, resource, timeSpan3, false);

        schedule.addReservation(reservation1);
        schedule.addReservation(reservation2);
        schedule.addReservation(reservation3);

        assertEquals(reservation1, schedule.getReservation(task1));
        assertEquals(reservation2, schedule.getReservation(task2));
        assertEquals(reservation3, schedule.getReservation(task3));
        System.out.println(schedule.getAvailableTime(LocalDateTime.of(2015, 4, 9, 11, 15)));


    }*/


}
