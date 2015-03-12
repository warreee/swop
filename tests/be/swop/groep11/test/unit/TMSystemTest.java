package be.swop.groep11.test.unit;

import be.swop.groep11.main.TMSystem;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TMSystemTest {

    private TMSystem TMSystem;
    private LocalDateTime newTime;

    @Before
    public void setUp() throws Exception {
        TMSystem = new TMSystem();
        newTime = LocalDateTime.now().plusSeconds(10);

    }

    @Test
    public void GetProjectRepository_check() throws Exception {
        assertNotNull(TMSystem.getProjectRepository());
    }

    @Test
    public void UpdateSystemTime_valid() throws Exception {
        TMSystem.updateSystemTime(newTime);
        assertEquals(newTime, TMSystem.getCurrentSystemTime());
    }

    @Test (expected = IllegalArgumentException.class)
     public void UpdateSystemTime_invalid_earlierTime() throws Exception {
        LocalDateTime newTime = TMSystem.getCurrentSystemTime().minusSeconds(10);
        TMSystem.updateSystemTime(newTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void UpdateSystemTime_invalid_null() throws Exception {
        TMSystem.updateSystemTime(null);
    }

    @Test
    public void GetCurrentSystemTime_check() throws Exception {
        assertTrue(TMSystem.getCurrentSystemTime().isBefore(newTime));
    }
}