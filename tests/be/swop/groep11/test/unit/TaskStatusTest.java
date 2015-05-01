package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskStatus;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;


public class TaskStatusTest {

    Project project;
    Task task1, task2, task3;
    Method methodMakeAvailable, methodMakeUnAvailable;
    private SystemTime systemTime;
    private LocalDateTime now;

    @Before
    public void setUp() throws NoSuchMethodException {
        now = LocalDateTime.of(2015, 3, 12, 8, 0);
        systemTime = new SystemTime(now);
        project = new Project("Test project", "Mijn eerste project", now, now.plusHours(6), systemTime);

        project.addNewTask("Taak1", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak2", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak3", 0.1, Duration.ofHours(1));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
        task3 = project.getTasks().get(2);
        methodMakeAvailable = TaskStatus.class.getDeclaredMethod("makeAvailable");
        methodMakeUnAvailable = TaskStatus.class.getDeclaredMethod("makeUnAvailable");
        methodMakeAvailable.setAccessible(true);
        methodMakeUnAvailable.setAccessible(true);

    }


/*    *//**
     * Taken zijn standaard geïnitialiseerd op available!
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     *//*
    @Test(expected = IllegalStateTransition.class)
    public void availableToAvailableTest() throws Exception {

        methodMakeAvailable.invoke(task1);

    }

    */

    /**
     * Maak een taak unavailable, daarna terug available en daarna nog is available TODO: huh?
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     *//*
    @Test(expected = IllegalStateTransition.class)
    public void availableToAvailableTest2() throws InvocationTargetException, IllegalAccessException {
        methodMakeUnAvailable.invoke(task1);
        methodMakeAvailable.invoke(task1);
        methodMakeAvailable.invoke(task1);
    }

    @Test (expected = IllegalStateTransition.class)
    public void availableToUnavailableTest() throws Exception {
        methodMakeUnAvailable.invoke(task1);
        task1.addNewDependencyConstraint(task2);
        methodMakeAvailable.invoke(task1);
    } */
/////////////////////////// UNAVAILABLE___TO____ //////////////////////

    @Test
    public void unavailableToAvailable() {
        task2.addNewDependencyConstraint(task1);
        assertEquals(task1.getStatusString(), "AVAILABLE");
        assertEquals(task2.getStatusString(), "UNAVAILABLE");
        task1.execute(LocalDateTime.now());
        task1.finish(LocalDateTime.now());
        assertEquals(task1.getStatusString(), "FINISHED");
        assertEquals(task2.getStatusString(), "AVAILABLE");
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void unavailableToExecuting() {
        task2.addNewDependencyConstraint(task1);
        assertEquals(task1.getStatusString(), "AVAILABLE");
        assertEquals(task2.getStatusString(), "UNAVAILABLE");
        task2.execute(LocalDateTime.now());
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void unavailableToFailed() {
        task2.addNewDependencyConstraint(task1);
        assertEquals(task1.getStatusString(), "AVAILABLE");
        assertEquals(task2.getStatusString(), "UNAVAILABLE");
        task2.fail(LocalDateTime.now());
    }

    @Test
    public void unavailableToUnavailable() {
        task2.addNewDependencyConstraint(task1);
        assertEquals(task1.getStatusString(), "AVAILABLE");
        assertEquals(task2.getStatusString(), "UNAVAILABLE");
        try {
            methodMakeUnAvailable.invoke(task2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        assertEquals(task2.getStatusString(), "UNAVAILABLE");
    }




//////////////////////// AVAILABLE___TO____ //////////////////////

    @Test
    public void availableToUnavailable() {
        task2.addNewDependencyConstraint(task1);
        assertEquals(task1.getStatusString(), "AVAILABLE");
        assertEquals(task2.getStatusString(), "UNAVAILABLE");
    }

    @Test
    public void availableToExecuting() throws Exception {
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        assertTrue(task1.getStatusString().equals("EXECUTING"));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void availableToFailedTest() throws Exception {
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.fail(now);
    }


    @Test(expected = IllegalStateTransitionException.class)
    public void availableToFinishedTest() throws Exception {
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.finish(now);
    }

    //////////////////////// EXECUTING___TO____ //////////////////////

    @Test
    public void executingToFinishedTest() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        assertTrue(task1.getStatusString().equals("EXECUTING"));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FINISHED"));
    }

    @Test
    public void executingToFailed() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FAILED"));
        assertEquals(LocalDateTime.of(2015, 3, 12, 10, 0), task1.getEndTime());
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void executingToExecuting() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute(LocalDateTime.of(2015, 3, 12, 18, 0));
    }

    //////////////////////// FINISHED___TO____ //////////////////////

    @Test(expected = IllegalStateTransitionException.class)
    public void finishedToExecuting() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 12, 8, 0).plusHours(2));
        assertTrue(task1.getStatusString().equals("FINISHED"));
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0).plusHours(4));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void finishedToFailed() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FINISHED"));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void finishedToFinished() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FINISHED"));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    //////////////////////// FAILED___TO____ //////////////////////

    @Test(expected = IllegalStateTransitionException.class)
    public void failedToExecuting() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FAILED"));
        task1.execute(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void failedToFinished() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FAILED"));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void failedToFailed() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.getStatusString().equals("FAILED"));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
    }
}