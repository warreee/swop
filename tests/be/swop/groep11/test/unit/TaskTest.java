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
        project = new Project(1,"Test project", "Test beschrijving",
                    LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015,3,4,16,0),
                    new User("Alfred J. Kwak"));
        project.addNewTask("Test taak", 0.1, Duration.ofHours(8));
        project.addNewTask("Test taak 1", 0, Duration.ofMinutes(30));
        project.addNewTask("Test taak 2", 0.2, Duration.ofHours(16));
        task1 = project.getTaskByID(0);
        task2 = project.getTaskByID(1);
        task3 = project.getTaskByID(2);
    }

    /*
        Aanmaken van taak: geldige input controleren
     */

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidTaskID_Negative() throws Exception {
        Task invalidTask = new Task(-1,"Test taak", Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Null() throws Exception {
        Task invalidTask = new Task(0,null, Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Empty() throws Exception {
        Task invalidTask = new Task(0,"", Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDuration_Negative() throws Exception {
        Task invalidTask = new Task(0,"Test taak", Duration.ofHours(-1), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidAcceptableDeviation_Negative() throws Exception {
        Task invalidTask = new Task(0,"Test taak", Duration.ofHours(8), -0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidProject_Null() throws Exception {
        Task invalidTask = new Task(0,"Test taak", Duration.ofHours(8), 0.1, null);
    }

    @Test (expected = Exception.class)
    public void newTask_InvalidProject_Finished() throws Exception {
        task1.setStatus2(TaskStatus.FINISHED);
        task2.setStatus2(TaskStatus.FINISHED);
        task3.setStatus2(TaskStatus.FINISHED);
        project.finish();
        Task invalidTask = new Task(0,"Test taak", Duration.ofHours(8), 0.1, project);
    }


    /*
        Tests voor alternatieve taken
     */

    @Test
    public void SetAlternativeTask_valid() throws Exception {
        task1.setStatus(TaskStatus.FAILED);
        task1.setAlternativeTask(task2);
    }
    @Test (expected = Exception.class)
    public void SetAlternativeTask_notFailed() throws Exception {
        task1.setAlternativeTask(task2);
    }
    @Test (expected = Exception.class)
    public void SetAlternativeTask_sameTask() throws Exception {
        task1.setStatus(TaskStatus.FAILED);
        task1.setAlternativeTask(task1);
    }

    /*
        Tests voor status van geëindigde taak
     */
    @Test
    public void FinishedStatus_notFinished() throws Exception {
        assertTrue(task1.getFinishedStatus() == -2);
    }
    @Test
    public void FinishedStatus_early() throws Exception {
        task1.setStatus(TaskStatus.FINISHED);
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.setEndTime(LocalDateTime.of(2015,3,8,12,0));
        assertTrue(task1.getFinishedStatus() == -1);
    }
    @Test
    public void FinishedStatus_onTime() throws Exception {
        task1.setStatus(TaskStatus.FINISHED);
        task1.setStartTime(LocalDateTime.of(2015,3,8,8,32));
        task1.setEndTime(LocalDateTime.of(2015,3,8,16,35));
        assertTrue(task1.getFinishedStatus() == 0);
    }
    @Test
    public void FinishedStatus_late() throws Exception {
        task1.setStatus(TaskStatus.FINISHED);
        task1.setStartTime(LocalDateTime.of(2015,3,8,8,32));
        task1.setEndTime(LocalDateTime.of(2015,3,9,12,38));
        assertTrue(task1.getFinishedStatus() == 1);
    }

    /*
        Delay en duration
     */
    @Test
    public void Delay_EarlyFinishedTask() {
        task1.setStatus(TaskStatus.FINISHED);
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.setEndTime(LocalDateTime.of(2015,3,8,12,0));
        assertTrue(task1.getDelay().equals(Duration.ofDays(0)));
    }
    @Test
    public void Delay_FinishedAfterEstimatedDuration() {
        task1.setStatus(TaskStatus.FINISHED);
        task1.setStartTime(LocalDateTime.of(2015, 3, 8, 8, 32));
        task1.setEndTime(LocalDateTime.of(2015, 3, 8, 16, 35));
        assertTrue(task1.getDelay().equals(Duration.ofMinutes(3)));
    }
}