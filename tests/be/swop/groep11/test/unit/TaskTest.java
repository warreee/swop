package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TaskTest {

    private Task task1, task2, task3;
    private Project project;
    private SystemTime systemTime;
    private DependencyGraph dependencyGraph;
    private LocalDateTime now;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        project = new Project("Test project", "Test beschrijving",
                LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015, 3, 4, 16, 0),
                systemTime, branchOffice);

        project.addNewTask("Test taak 1", 0.1, Duration.ofHours(8));
        project.addNewTask("Test taak 2", 0, Duration.ofMinutes(120));
        project.addNewTask("Test taak 3", 0.2, Duration.ofHours(16));

        dependencyGraph = new DependencyGraph();

        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
        task3 = project.getTasks().get(2);
    }
    //TODO review testen, wordt alles getest dat getest moet worden?
    /*
        Aanmaken van taak: geldige input controleren
     */
    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Null() throws Exception {
        Task invalidTask = new Task(null, Duration.ofHours(8), 0.1, systemTime,dependencyGraph, mock(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Empty() throws Exception {
        Task invalidTask = new Task("", Duration.ofHours(8), 0.1, systemTime,dependencyGraph, mock(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDuration_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(-1), 0.1, systemTime,dependencyGraph, mock(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidAcceptableDeviation_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), -0.1, systemTime,dependencyGraph, mock(Project.class));
    }

    /*
        Tests voor alternatieve taken
     */
    @Test
    public void SetAlternativeTask_valid() {
        task1.execute(now);
        task1.fail(now.plusDays(1));
        task1.setAlternativeTask(task2);
        assertTrue(task1.getAlternativeTask() == task2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void SetAlternativeTask_notFailed() throws Exception {
        task1.setAlternativeTask(task2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void SetAlternativeTask_sameTask() throws Exception {
        task1.execute(now);
        task1.fail(now.plusHours(1));
        task1.setAlternativeTask(task1);
    }

    @Test
    public void SetAlternativeTask_withDepedencyConstraints() throws Exception {
        task3.addNewDependencyConstraint(task1);
        task1.execute(now);
        task1.fail(now.plusDays(1));
        task1.setAlternativeTask(task2);
        assertTrue(task3.getDependingOnTasks().contains(task2));
        assertFalse(task3.getDependingOnTasks().contains(task1));
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
        task1.execute(now);
        task1.finish(now.plusHours(2));
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.EARLY);
    }

    @Test
    public void FinishedStatus_onTime() throws Exception {
        task1.execute(now);
        task1.finish(now.plusHours(8));
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.ONTIME);
    }

    @Test
    public void FinishedStatus_late() throws Exception {
        task1.execute(now);
        task1.finish(now.plusHours(18));
        assertTrue(task1.getFinishedStatus() == Task.FinishedStatus.OVERDUE);
    }

    /*
        Delay en duration
     */
    @Test
    public void Delay_EarlyFinishedTask() {
        task1.execute(now);
        task1.finish(now.plusHours(1));
        assertTrue(task1.getDelay().equals(Duration.ofDays(0)));
    }

    @Test
    public void Delay_FinishedAfterEstimatedDuration() {
        task1.execute(LocalDateTime.of(2015, 3, 8, 8, 32));
        task1.finish(LocalDateTime.of(2015, 3, 8, 16, 35));
        assertTrue(task1.getDelay().equals(Duration.ofSeconds(180)));
    }

    /**
     * getAlternativeStatus
     */
    @Test
    public void AlternativeStatus_NoAlternativeTask() {
        assertFalse(task1.getAlternativeFinished());
        task1.execute(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.finish(LocalDateTime.of(2015, 3, 8, 12, 0));
        assertTrue(task1.getAlternativeFinished());
    }

    @Test
    public void AlternativeStatus_AlternativeTask() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 8, 10, 30));
        task1.fail(LocalDateTime.of(2015, 3, 8, 12, 0));
        task1.setAlternativeTask(task2);
        assertFalse(task1.getAlternativeFinished());
        task2.execute(LocalDateTime.of(2015, 3, 8, 10, 30));
        task2.finish(LocalDateTime.of(2015, 3, 8, 12, 0));
        assertTrue(task1.getAlternativeFinished());
    }

    @Test
    public void getOverTimePercentageTest() throws Exception {
        task1.execute(LocalDateTime.of(2015, 7, 8, 8, 0));
        assertEquals(task1.getOverTimePercentage(), 0.0, 1E-14);

        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 13, 0));
        assertEquals(task1.getOverTimePercentage(), 0.0, 1E-14);

        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 17, 0));
        assertEquals(task1.getOverTimePercentage(), 0.125, 1E-14);
    }

    @Test
    public void isOverTimeTest() throws Exception {
        task1.execute(LocalDateTime.of(2015, 7, 8, 8, 0));
        assertFalse(task1.isOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 16, 0));
        assertFalse(task1.isOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 18, 0));
        assertTrue(task1.isOverTime());
    }

    @Test
    public void isUnacceptablyOverTime() {
        task1.execute(LocalDateTime.of(2015, 7, 8, 8, 0));
        assertFalse(task1.isUnacceptablyOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 16, 0));
        assertFalse(task1.isUnacceptablyOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 19, 0));
        assertTrue(task1.isUnacceptablyOverTime());
    }

}