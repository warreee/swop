package be.swop.groep11.test.unit;

import be.swop.groep11.main.Resource;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ResourceTest {

    private Resource resource_24_7, resource_10_16;

    @Before
    public void setUp() throws Exception {
        resource_24_7 = new Resource("Resource 24/7");
        resource_10_16 = new Resource("Resource 10u - 16u", LocalTime.of(10,00), LocalTime.of(16,00));
    }

    @Test
    public void calculateEndTime_WithoutDailyAvailabilityTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2015,4,6,10,24);
        Duration duration = Duration.ofMinutes(350);
        LocalDateTime end = resource_24_7.calculateEndTime(start,duration);
        assertTrue(end.equals(start.plus(duration)));
    }

    @Test
    public void calculateEndTime_WithDailyAvailabilityTest() throws Exception {
        LocalDateTime start1 = LocalDateTime.of(2015, 4, 6, 8, 0);
        LocalDateTime start2 = LocalDateTime.of(2015, 4, 6, 10, 0);
        LocalDateTime start3 = LocalDateTime.of(2015, 4, 11, 12, 0);
        Duration duration = Duration.ofHours(25);
        LocalDateTime expectedEnd1 = LocalDateTime.of(2015, 4, 10, 11, 0);
        LocalDateTime expectedEnd2 = expectedEnd1;
        LocalDateTime expectedEnd3 = LocalDateTime.of(2015, 4, 17, 11, 0);
        assertTrue(expectedEnd1.equals(resource_10_16.calculateEndTime(start1, duration)));
        assertTrue(expectedEnd2.equals(resource_10_16.calculateEndTime(start2, duration)));
        assertTrue(expectedEnd3.equals(resource_10_16.calculateEndTime(start3, duration)));
    }

}