package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.ResourceType;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourceTypeTest {

    private AResourceType type_24_7;
    private AResourceType type_10_16;

    @Before
    public void setUp() throws Exception {

        this.type_24_7 =  new ResourceType("24/7");
        this.type_10_16 = new ResourceType("10u - 16u ");

        type_10_16.setDailyAvailability(new DailyAvailability(LocalTime.of(10,00), LocalTime.of(16,00)));
    }

    @Test
    public void calculateEndTime_WithoutDailyAvailabilityTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2015,4,6,10,0);
        Duration duration = Duration.ofMinutes(350);
        LocalDateTime end = type_24_7.calculateEndTime(start,duration);
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

        assertEquals(expectedEnd1,type_10_16.calculateEndTime(start1, duration));
        assertEquals(expectedEnd2,type_10_16.calculateEndTime(start2, duration));
        assertEquals(expectedEnd3,type_10_16.calculateEndTime(start3, duration));
    }

}