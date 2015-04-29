package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.DailyAvailability;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class DailyAvailabilityTest {

    private DailyAvailability availability1, availability2, availability3;

    @Before
    public void setUp() throws Exception {
        availability1 = new DailyAvailability(LocalTime.of(8,0), LocalTime.of(12,0));
        availability2 = new DailyAvailability(LocalTime.of(13,0), LocalTime.of(18,0));
        availability3 = new DailyAvailability(LocalTime.of(10,0), LocalTime.of(14,0));
    }

    @Test
    public void containsDateTimeTest() throws Exception {
        assertTrue(availability1.containsDateTime(LocalDateTime.of(2015,4,29,8,0)));
        assertTrue(availability1.containsDateTime(LocalDateTime.of(2015,4,29,10,0)));
        assertTrue(availability1.containsDateTime(LocalDateTime.of(2015,4,29,12,0)));
        assertFalse(availability1.containsDateTime(LocalDateTime.of(2015, 4, 29, 12, 1)));
        assertFalse(availability1.containsDateTime(LocalDateTime.of(2015, 4, 29, 7, 59)));

        assertFalse(availability1.containsDateTime(LocalDateTime.of(2015, 5, 2, 10, 0)));
        assertFalse(availability1.containsDateTime(LocalDateTime.of(2015, 5, 3, 10, 0)));
    }

    @Test
    public void overlapsWithTest() throws Exception {
        LinkedList<DailyAvailability> list1 = new LinkedList<>();
        list1.add(availability2);
        assertFalse(availability1.overlapsWith(list1));
        list1.add(availability3);
        assertFalse(availability1.overlapsWith(list1));

        LinkedList<DailyAvailability> list2 = new LinkedList<>();
        list2.add(availability3);
        assertTrue(availability1.overlapsWith(list2));
    }

    @Test
    public void isValidStartTimeEndTimeTest() throws Exception {
        assertFalse(DailyAvailability.isValidStartTimeEndTime(null, LocalTime.of(18, 0)));
        assertFalse(DailyAvailability.isValidStartTimeEndTime(LocalTime.of(18, 0), null));
        assertFalse(DailyAvailability.isValidStartTimeEndTime(null, null));
        assertFalse(DailyAvailability.isValidStartTimeEndTime(LocalTime.of(10, 0), LocalTime.of(10, 59)));
        assertFalse(DailyAvailability.isValidStartTimeEndTime(LocalTime.of(10, 0), LocalTime.of(9, 59)));
        assertFalse(DailyAvailability.isValidStartTimeEndTime(LocalTime.of(10, 0), LocalTime.of(10, 0)));
        assertTrue(DailyAvailability.isValidStartTimeEndTime(LocalTime.of(10, 0), LocalTime.of(11, 0)));
    }

    @Test
     public void getNextTimeTest() throws Exception {
        assertEquals(LocalDateTime.of(2015, 4, 29, 8, 0), availability1.getNextTime(LocalDateTime.of(2015, 4, 29, 7, 0)));
        assertEquals(LocalDateTime.of(2015, 4, 29, 8, 0), availability1.getNextTime(LocalDateTime.of(2015, 4, 29, 8, 0)));
        assertEquals(LocalDateTime.of(2015, 4, 29, 9, 0), availability1.getNextTime(LocalDateTime.of(2015, 4, 29, 9, 0)));
        assertEquals(LocalDateTime.of(2015, 5, 4, 8, 0), availability1.getNextTime(LocalDateTime.of(2015, 5, 1, 12, 1)));
        assertEquals(LocalDateTime.of(2015, 5, 4, 8, 0), availability1.getNextTime(LocalDateTime.of(2015, 5, 2, 12, 1)));
        assertEquals(LocalDateTime.of(2015, 5, 4, 8, 0), availability1.getNextTime(LocalDateTime.of(2015, 5, 3, 12, 1)));
    }

    @Test
    public void getNextStartTimeTest() throws Exception {
        assertEquals(LocalDateTime.of(2015,4,29,8,0), availability1.getNextStartTime(LocalDateTime.of(2015, 4, 29, 7, 0)));
        assertEquals(LocalDateTime.of(2015,4,29,8,0), availability1.getNextStartTime(LocalDateTime.of(2015, 4, 29, 8, 0)));
        assertEquals(LocalDateTime.of(2015,4,30,8,0), availability1.getNextStartTime(LocalDateTime.of(2015, 4, 29, 9, 0)));
        assertEquals(LocalDateTime.of(2015,5,4,8,0), availability1.getNextStartTime(LocalDateTime.of(2015, 5, 1, 12, 1)));
        assertEquals(LocalDateTime.of(2015,5,4,8,0), availability1.getNextStartTime(LocalDateTime.of(2015, 5, 2, 12, 1)));
        assertEquals(LocalDateTime.of(2015,5,4,8,0), availability1.getNextStartTime(LocalDateTime.of(2015, 5, 3, 12, 1)));
    }

    @Test
    public void getNextEndTimeTest() throws Exception {
        assertEquals(LocalDateTime.of(2015,4,29,12,0), availability1.getNextEndTime(LocalDateTime.of(2015, 4, 29, 7, 0)));
        assertEquals(LocalDateTime.of(2015,4,29,12,0), availability1.getNextEndTime(LocalDateTime.of(2015, 4, 29, 8, 0)));
        assertEquals(LocalDateTime.of(2015,4,29,12,0), availability1.getNextEndTime(LocalDateTime.of(2015, 4, 29, 9, 0)));
        assertEquals(LocalDateTime.of(2015,5,4,12,0), availability1.getNextEndTime(LocalDateTime.of(2015, 5, 1, 12, 1)));
        assertEquals(LocalDateTime.of(2015,5,4,12,0), availability1.getNextEndTime(LocalDateTime.of(2015, 5, 2, 12, 1)));
        assertEquals(LocalDateTime.of(2015,5,4,12,0), availability1.getNextEndTime(LocalDateTime.of(2015, 5, 3, 12, 1)));
    }


}