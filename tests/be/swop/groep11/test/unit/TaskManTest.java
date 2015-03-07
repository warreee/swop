package be.swop.groep11.test.unit;

import be.swop.groep11.main.TaskMan;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TaskManTest {

    private TaskMan taskMan;
    private LocalDateTime newTime;

    @Before
    public void setUp() throws Exception {
        taskMan = new TaskMan();
        newTime = LocalDateTime.now().plusSeconds(10);

    }

    @Test
    public void GetProjectRepository_check() throws Exception {
        assertNotNull(taskMan.getProjectRepository());
    }

    @Test
    public void UpdateSystemTime_valid() throws Exception {
        taskMan.updateSystemTime(newTime);
        assertEquals(newTime,taskMan.getCurrentSystemTime());
    }

    @Test (expected = IllegalArgumentException.class)
     public void UpdateSystemTime_invalid_earlierTime() throws Exception {
        LocalDateTime newTime = taskMan.getCurrentSystemTime().minusSeconds(10);
        taskMan.updateSystemTime(newTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void UpdateSystemTime_invalid_null() throws Exception {
        taskMan.updateSystemTime(null);
    }

    @Test
    public void GetCurrentSystemTime_check() throws Exception {
        assertTrue(taskMan.getCurrentSystemTime().isBefore(newTime));
    }
}