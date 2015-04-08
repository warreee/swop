package be.swop.groep11.test.unit;

import be.swop.groep11.main.Developer;
import be.swop.groep11.main.ResourceAllocation;
import be.swop.groep11.main.ResourceType;
import be.swop.groep11.main.TimeSpan;
import org.junit.*;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class DeveloperTest {

    /**
     * Developer "Jos" van het resource type "Developer"
     * en 4 allocaties: * wo 08/04/2015 08:00 - vr 10/04/2015 14:00
     *                  * ma 13/04/2015 08:00 - ma 13/04/2015 12:30
     *                  * di 14/04/2015 13:00 - di 14/04/2015 13:30
     *                  * wo 15/04/2015 12:00 - wo 15/04/2015 13:00
     */
    private Developer developer;

    @Before
    public void setUp() throws Exception {
        ResourceType developerType = new ResourceType("Developer");
        developer = new Developer("Jos", developerType);
        ResourceAllocation allocation1 = new ResourceAllocation(developer, new TimeSpan(LocalDateTime.of(2015,4,8,8,0), LocalDateTime.of(2015,4,10,14,0)));
        ResourceAllocation allocation2 = new ResourceAllocation(developer, new TimeSpan(LocalDateTime.of(2015,4,13,8,0), LocalDateTime.of(2015,4,13,12,30)));
        ResourceAllocation allocation3 = new ResourceAllocation(developer, new TimeSpan(LocalDateTime.of(2015,4,14,13,0), LocalDateTime.of(2015,4,14,13,30)));
        ResourceAllocation allocation4 = new ResourceAllocation(developer, new TimeSpan(LocalDateTime.of(2015,4,15,12,0), LocalDateTime.of(2015,4,15,13,0)));
    }

    @Test
    public void isAvailable_UnAvailableTest() {
        assertFalse(developer.isAvailable(new TimeSpan(LocalDateTime.of(2015,4,9,8,0), LocalDateTime.of(2015,4,9,16,0))));
    }

    @Test
    public void isAvailable_AvailableTest() {
        assertTrue(developer.isAvailable(new TimeSpan(LocalDateTime.of(2015,4,12,8,0), LocalDateTime.of(2015,4,15,16,0))));
    }

    @Test
    public void getNextAvailableTimeSpan_Test1() {
        LocalDateTime startTime = LocalDateTime.of(2015,4,9,10,0);
        Duration duration = Duration.ofHours(4);
        TimeSpan expectedTimeSpan = new TimeSpan(LocalDateTime.of(2015,4,13,12,30), LocalDateTime.of(2015,4,14,8,30));
        TimeSpan timeSpan = developer.getNextAvailableTimeSpan(startTime, duration);
    }

    @Test
    public void getNextAvailableTimeSpan_Test2() {
        LocalDateTime startTime = LocalDateTime.of(2015,4,14,10,30);
        Duration duration = Duration.ofMinutes(90);
        TimeSpan expectedTimeSpan = new TimeSpan(LocalDateTime.of(2015,4,14,13,30), LocalDateTime.of(2015,4,14,15,0));
        TimeSpan timeSpan = developer.getNextAvailableTimeSpan(startTime, duration);
    }

}
