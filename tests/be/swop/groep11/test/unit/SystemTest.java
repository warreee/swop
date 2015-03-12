package be.swop.groep11.test.unit;

import be.swop.groep11.main.System;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class SystemTest {

    private System system;
    private LocalDateTime newTime;

    @Before
    public void setUp() throws Exception {
        system = new System();
        newTime = LocalDateTime.now().plusSeconds(10);

    }

    @Test
    public void GetProjectRepository_check() throws Exception {
        assertNotNull(system.getProjectRepository());
    }

    @Test
    public void UpdateSystemTime_valid() throws Exception {
        system.updateSystemTime(newTime);
        assertEquals(newTime, system.getCurrentSystemTime());
    }

    @Test (expected = IllegalArgumentException.class)
     public void UpdateSystemTime_invalid_earlierTime() throws Exception {
        LocalDateTime newTime = system.getCurrentSystemTime().minusSeconds(10);
        system.updateSystemTime(newTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void UpdateSystemTime_invalid_null() throws Exception {
        system.updateSystemTime(null);
    }

    @Test
    public void GetCurrentSystemTime_check() throws Exception {
        assertTrue(system.getCurrentSystemTime().isBefore(newTime));
    }
}