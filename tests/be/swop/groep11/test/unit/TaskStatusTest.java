package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


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
    public void availableToAvailableTest() throws Exception {
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
        task1.addNewDependencyConstraint(task2); // Dit zou ervoor moeten zorgen dat taak1 op UNVAILABLE komt.
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
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

    @Test
    public void unavailableToAvailaleTest() throws Exception {
        task1.addNewDependencyConstraint(task2); // Dit zet de status van taak1 op Unavailable.
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
        task2.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task2.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
        task2.setNewStatus(TaskStatus.FINISHED);
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));

    }

    @Test
    public void unavailableToUnavailaleTest() throws Exception {
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
        task1.addNewDependencyConstraint(task2); // Dit zet de status van taak1 op Unavailable.
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
        task2.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task2.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task2.setNewStatus(TaskStatus.FINISHED);
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
    }

    @Test
    public void unavailableToFinishedTest() throws Exception {
        // UNAVAILABLE -> FINISHED mag nooit.
        // Correct: UNAVAILABLE -> AVAILABLE -> FINISHED
        task1.addNewDependencyConstraint(task2); // Dit zet de status van taak1 op Unavailable.
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));
        task2.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task2.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task2.setNewStatus(TaskStatus.FINISHED);
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 11, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 12, 0));
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));

    }

    @Test
    public void unavailableToFailedTest() throws Exception {
        // UNAVAILABLE -> FAILED mag nooit.
        // Correct: UNAVAILABLE -> AVAILABLE -> FAILED
        task1.addNewDependencyConstraint(task2); // Dit zet de status van taak1 op Unavailable.
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        task2.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task2.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task2.setNewStatus(TaskStatus.FINISHED);
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 11, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 12, 0));
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
    }
}
