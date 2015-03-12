package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.User;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by robin on 12/03/15.
 */
public class TaskStatusTest {

    Project project;
    Task task1, task2;

    @Before
    public void setUp() {
        LocalDateTime date = LocalDateTime.of(2015, 3, 12, 8, 0);
        project = new Project("Test project", "Mijn eerste project", date, date.plusHours(6), new User("Ik"));
        project.addNewTask("Taak1", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak2", 0.1, Duration.ofHours(1));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
    }

    @Test
    public void availableToOtherTest() throws Exception {
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));
    }

    @Test
    public void availableToFinishedTest() throws Exception {
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));
    }

    @Test
    public void availableToFailedTest() throws Exception {
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
    }

    @Test
    public void availableToUnavailableTest() throws Exception {
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
        task1.addNewDependencyConstraint(task2);
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
    }
}
