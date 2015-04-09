package be.swop.groep11.test.unit;

import be.swop.groep11.main.TimeSpan;
import org.junit.*;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TimeSpanTest {

    private TimeSpan timeSpan1, timeSpan2, timeSpan3, timeSpan4,
                     timeSpan5, timeSpan6, timeSpan7, timeSpan8;

    @Before
    public void setUp() {
        timeSpan1 = new TimeSpan(LocalDateTime.of(2015,4,9,9,0), LocalDateTime.of(2015,4,9,12,0));
        timeSpan2 = new TimeSpan(LocalDateTime.of(2015,4,9,14,0), LocalDateTime.of(2015,4,9,17,0));
        timeSpan3 = new TimeSpan(LocalDateTime.of(2015,4,9,10,0), LocalDateTime.of(2015,4,9,11,0));
        timeSpan4 = new TimeSpan(LocalDateTime.of(2015,4,9,15,0), LocalDateTime.of(2015,4,9,16,0));
        timeSpan5 = new TimeSpan(LocalDateTime.of(2015,4,9,8,0), LocalDateTime.of(2015,4,9,10,0));
        timeSpan5 = new TimeSpan(LocalDateTime.of(2015,4,9,10,0), LocalDateTime.of(2015,4,9,13,0));
        timeSpan5 = new TimeSpan(LocalDateTime.of(2015,4,9,13,0), LocalDateTime.of(2015,4,9,15,0));
        timeSpan5 = new TimeSpan(LocalDateTime.of(2015,4,9,15,0), LocalDateTime.of(2015,4,9,18,0));
    }

    @Test
    public void overlapsWith_NoOverlapTest() {
        assertFalse(timeSpan1.overlapsWith(timeSpan2));
        assertFalse(timeSpan2.overlapsWith(timeSpan1));
        assertFalse(timeSpan5.overlapsWith(timeSpan6));
        assertFalse(timeSpan6.overlapsWith(timeSpan5));
    }

    @Test
    public void overlapsWith_OverlapTest() {
        assertTrue(timeSpan1.overlapsWith(timeSpan1));
        assertTrue(timeSpan5.overlapsWith(timeSpan1));
        assertTrue(timeSpan1.overlapsWith(timeSpan6));
        assertTrue(timeSpan1.overlapsWith(timeSpan3));
        assertTrue(timeSpan4.overlapsWith(timeSpan2));
    }

}
