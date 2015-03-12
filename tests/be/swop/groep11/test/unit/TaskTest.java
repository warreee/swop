package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskStatus;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTest {

    private Task task1, task2, task3;
    private Project project;

    @Before
    public void setUp() throws Exception {
        project = new Project("Test project", "Test beschrijving",
                LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015, 3, 4, 16, 0),
                new User("Alfred J. Kwak"));
        project.addNewTask("Test taak", 0.1, Duration.ofHours(8));
        project.addNewTask("Test taak 1", 0, Duration.ofMinutes(30));
        project.addNewTask("Test taak 2", 0.2, Duration.ofHours(16));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
        task3 = project.getTasks().get(2);
    }

    /*
        Aanmaken van taak: geldige input controleren
     */


    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Null() throws Exception {
        Task invalidTask = new Task(null, Duration.ofHours(8), 0.1, project);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Empty() throws Exception {
        Task invalidTask = new Task("", Duration.ofHours(8), 0.1, project);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDuration_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(-1), 0.1, project);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidAcceptableDeviation_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), -0.1, project);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidProject_Null() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), 0.1, null);
    }

   /* @Test(expected = Exception.class)
    public void newTask_InvalidProject_Finished() throws Exception {
        task1.setNewStatus(TaskStatus.FINISHED);
        task2.setNewStatus(TaskStatus.FINISHED);
        task3.setNewStatus(TaskStatus.FINISHED);
        project.finish();
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), 0.1, project);
    }*/


    /*
        Tests voor alternatieve taken
     */
    @Test
    public void SetAlternativeTask_valid() throws Exception {
        task1.setStartTime(LocalDateTime.now());
        task1.setEndTime(LocalDateTime.now().plusDays(1));
        task1.setNewStatus(TaskStatus.FAILED);
        task1.setAlternativeTask(task2);
    }

    @Test(expected = Exception.class)
    public void SetAlternativeTask_notFailed() throws Exception {
        task1.setAlternativeTask(task2);
    }

    @Test(expected = Exception.class)
    public void SetAlternativeTask_sameTask() throws Exception {
        task1.setNewStatus(TaskStatus.FAILED);
        task1.setAlternativeTask(task1);
    }

    /*
        Tests voor status van geëindigde taak
     */
    @Test
    public void FinishedStatus_notFinished() throws Exception {
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.NOTFINISHED);
    }

    @Test
    public void FinishedStatus_early() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 12, 0));
        task1.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.EARLY);
    }

    @Test
    public void FinishedStatus_onTime() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 8, 32));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 16, 35));
        task1.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.ONTIME);
    }

    @Test
    public void FinishedStatus_late() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 8, 32));
        task1.setEndTime(LocalDateTime.of(2015, 3, 9, 12, 38));
        task1.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.OVERDUE);
    }

    /*
        Delay en duration
     */
    @Test
    public void Delay_EarlyFinishedTask() {
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 12, 0));
        task1.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getDelay().equals(Duration.ofDays(0)));
    }

    @Test
    public void Delay_FinishedAfterEstimatedDuration() {
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 8, 32));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 16, 35));
        task1.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getDelay().equals(Duration.ofSeconds(180)));
    }

    /**
     * HasTask
     */

    @Test
    public void hasTaskTest() {
        assertTrue(project.hasTask(task2));
        // Deze task heeft een referentie naar project, maar is niet aan project toegevoegd.
        Task t = new Task("beschrijving", Duration.ofHours(8), 0.1, project);
        assertFalse(project.hasTask(t));

    }

    /**
     * getAlternativeStatus
     */

    @Test
    public void AlternativeStatus_NoAlternativeTask() {
        assertFalse(task1.getAlternativeFinished());
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 12, 0));
        task1.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getAlternativeFinished());
    }

    @Test
    public void AlternativeStatus_AlternativeTask_() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 12, 0));
        task1.setNewStatus(TaskStatus.FAILED);
        task1.setAlternativeTask(task2);
        assertFalse(task1.getAlternativeFinished());
        task2.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task2.setEndTime(LocalDateTime.of(2015, 3, 8, 12, 0));
        task2.setNewStatus(TaskStatus.FINISHED);
        assertTrue(task1.getAlternativeFinished());
    }

}