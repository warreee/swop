package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.Developer;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class DeveloperTypeTest {

    /**
     * Developer "Jos" van het resource type "Developer"
     * en 4 allocaties: * wo 08/04/2015 08:00 - vr 10/04/2015 14:00 => middagpauze inbegrepen
     *                  * ma 13/04/2015 08:00 - ma 13/04/2015 12:30 => middagpauze inbegrepen
     *                  * di 14/04/2015 13:00 - di 14/04/2015 13:30 => middagpauze niet inbegrepen
     *                  * wo 15/04/2015 12:00 - wo 15/04/2015 13:00 => middagpauze inbegrepen
     */
    private Developer developer;
    private Task taskA;
    private AResourceType developerType;

    @Before
    public void setUp() throws Exception {
        ResourceManager repo = new ResourceManager();
        developerType = repo.getDeveloperType();
/*        developer = new Developer("Jos", developerType);

        String description = "description";
        Duration duration = Duration.ofDays(1);
        SystemTime systemTime = new SystemTime();
        Double deviation = 0.2;
        DependencyGraph dependencyGraph = new DependencyGraph();

        taskA = new Task(description, duration, deviation, systemTime, dependencyGraph);


        ResourceReservation reservation1 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,8,8,0), LocalDateTime.of(2015,4,10,14,0)),false);
        ResourceReservation reservation2 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,13,8,0), LocalDateTime.of(2015,4,13,12,30)),false);
        ResourceReservation reservation3 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,14,13,0), LocalDateTime.of(2015,4,14,13,30)),false);
        ResourceReservation reservation4 = new ResourceReservation(taskA,developer, new TimeSpan(LocalDateTime.of(2015,4,15,12,0), LocalDateTime.of(2015,4,15,13,0)),false);*/
    }

    @Test
     public void developerType_calculateEndTime_EndsOnSameDayTest() {
        LocalDateTime startTime = LocalDateTime.of(2015,4,14,10,30);
        Duration duration = Duration.ofMinutes(90);
        LocalDateTime expectedEndTime = LocalDateTime.of(2015,4,14,13,30);
        assertEquals(expectedEndTime, developerType.calculateEndTime(startTime, duration));
    }

    @Test
    public void developerType_calculateEndTime_EndsOnDifferentDay_AddBreakTest() {
        LocalDateTime startTime = LocalDateTime.of(2015,4,14,12,0);
        Duration duration = Duration.ofHours(6);
        LocalDateTime expectedEndTime = LocalDateTime.of(2015,4,15,10,0);
        assertEquals(expectedEndTime, developerType.calculateEndTime(startTime, duration));
    }

    @Test
    public void developerType_calculateEndTime_addBreakTest() {
        LocalDateTime startTime = LocalDateTime.of(2015,4, 14, 11, 0);
        Duration duration = Duration.ofMinutes(60);
        LocalDateTime expectedEndTime = LocalDateTime.of(2015,4,14,13,0);
        assertEquals(expectedEndTime, developerType.calculateEndTime(startTime, duration));
    }


}
