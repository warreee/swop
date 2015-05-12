package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.TimeSpan;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeSpanTest {

    private TimeSpan TimeSpan1, TimeSpan2, TimeSpan3, TimeSpan4,
            TimeSpan5, TimeSpan6, TimeSpan7, TimeSpan8;

    @Before
    public void setUp() {
        TimeSpan1 = new TimeSpan(LocalDateTime.of(2015,4,9,9,0), LocalDateTime.of(2015,4,9,12,0));
        TimeSpan2 = new TimeSpan(LocalDateTime.of(2015,4,9,14,0), LocalDateTime.of(2015,4,9,17,0));
        TimeSpan3 = new TimeSpan(LocalDateTime.of(2015,4,9,10,0), LocalDateTime.of(2015,4,9,11,0));
        TimeSpan4 = new TimeSpan(LocalDateTime.of(2015,4,9,15,0), LocalDateTime.of(2015,4,9,16,0));
        TimeSpan5 = new TimeSpan(LocalDateTime.of(2015,4,9,8,0), LocalDateTime.of(2015,4,9,10,0));
        TimeSpan6 = new TimeSpan(LocalDateTime.of(2015,4,9,10,0), LocalDateTime.of(2015,4,9,13,0));
        TimeSpan7 = new TimeSpan(LocalDateTime.of(2015,4,9,13,0), LocalDateTime.of(2015,4,9,15,0));
        TimeSpan8 = new TimeSpan(LocalDateTime.of(2015,4,9,15,0), LocalDateTime.of(2015,4,9,18,0));
    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidTimeSpanTest() {
        TimeSpan invalidTimeSpan = new TimeSpan(LocalDateTime.of(2015,5,1,15,0), LocalDateTime.of(2015,4,1,15,0));
    }

    @Test
    public void overlapsWith_NoOverlapTest() {
        assertFalse(TimeSpan1.overlapsWith(TimeSpan2));
        assertFalse(TimeSpan2.overlapsWith(TimeSpan1));
        assertFalse(TimeSpan5.overlapsWith(TimeSpan6));
        assertFalse(TimeSpan6.overlapsWith(TimeSpan5));
        assertFalse(TimeSpan1.overlapsWith(null));
    }

    @Test
    public void overlapsWith_OverlapTest() {
        assertTrue(TimeSpan1.overlapsWith(TimeSpan1));
        assertTrue(TimeSpan5.overlapsWith(TimeSpan1));
        assertTrue(TimeSpan1.overlapsWith(TimeSpan6));
        assertTrue(TimeSpan1.overlapsWith(TimeSpan3));
        assertTrue(TimeSpan4.overlapsWith(TimeSpan2));
    }

    @Test
    public void equalsTest_() throws Exception {

        TimeSpan ts1 = new TimeSpan(LocalDateTime.of(2015,4,9,9,0), LocalDateTime.of(2015,4,9,12,0));
        TimeSpan ts2 = new TimeSpan(LocalDateTime.of(2015,4,9,9,0), LocalDateTime.of(2015,4,9,12,0));

        assertTrue(ts1.equals(ts2));
        assertFalse(ts1.equals(TimeSpan3));
    }

    @Test
    public void isBeforeTest() throws Exception {


        assertTrue(TimeSpan1.isBefore(TimeSpan2));
        assertFalse(TimeSpan2.isBefore(TimeSpan1));

        assertFalse(TimeSpan1.isBefore(TimeSpan1));

    }

    @Test
    public void isAfterTest() throws Exception {
        assertTrue(TimeSpan2.isAfter(TimeSpan1));
        assertFalse(TimeSpan1.isAfter(TimeSpan2));
        assertFalse(TimeSpan1.isAfter(TimeSpan1));
    }
}
