package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.SystemTime;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class SystemTimeTest {

    private SystemTime systemTime;
    private LocalDateTime newTime;

    @Before
    public void setUp() throws Exception {
        systemTime = new SystemTime();
        newTime = LocalDateTime.now().plusHours(10);
    }

    @Test
    public void UpdateSystemTime_valid() throws Exception {
        systemTime.updateSystemTime(newTime);
        assertEquals(newTime, systemTime.getCurrentSystemTime());
    }

    @Test (expected = IllegalArgumentException.class)
     public void UpdateSystemTime_invalid_earlierTime() throws Exception {
        LocalDateTime newTime = systemTime.getCurrentSystemTime().minusHours(1);
        systemTime.updateSystemTime(newTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void UpdateSystemTime_invalid_null() throws Exception {
        systemTime.updateSystemTime(null);
    }

    @Test
    public void GetCurrentSystemTime_check() throws Exception {
        assertTrue(systemTime.getCurrentSystemTime().isBefore(newTime));
    }
}