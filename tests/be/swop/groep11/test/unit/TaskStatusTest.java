package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.task.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class TaskStatusTest {

    Project project;
    Task task1, task2, task3;

    private SystemTime systemTime;
    private LocalDateTime now;
    private Method TUmethodMakeUnAvailable;
    private Method TUmethodMakeAvailable;
    private Method TAmethodMakeAvailable;
    private Method TAmethodMakeUnavailable;
    private Method TEmethodmakeAvailable;
    private Method TEmethodmakeUnavailable;
    private Method FmethodmakeAvailable;
    private Method FmethodmakeUnavailable;
    private Method FamethodmakeAvailable;
    private Method FamethodmakeUnavailable;

    @Before
    public void setUp() throws NoSuchMethodException {
        now = LocalDateTime.of(2015, 3, 12, 8, 0);
        systemTime = new SystemTime(now);
        project = new Project("Test project", "Mijn eerste project", now, now.plusHours(6), systemTime, mock(BranchOffice.class));

        project.addNewTask("Taak1", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak2", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak3", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak4", 0.1, Duration.ofHours(1));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
        task3 = project.getTasks().get(2);

        //Unavailable
        TUmethodMakeUnAvailable = TaskUnavailable.class.getDeclaredMethod("makeUnavailable", Task.class);
        TUmethodMakeAvailable = TaskUnavailable.class.getDeclaredMethod("makeAvailable", Task.class);
        TUmethodMakeAvailable.setAccessible(true);
        TUmethodMakeUnAvailable.setAccessible(true);

        //Available
        TAmethodMakeAvailable = TaskAvailable.class.getDeclaredMethod("makeAvailable", Task.class);
        TAmethodMakeUnavailable = TaskAvailable.class.getDeclaredMethod("makeUnavailable", Task.class);
        TAmethodMakeAvailable.setAccessible(true);
        TAmethodMakeUnavailable.setAccessible(true);

        //Executing
        TEmethodmakeAvailable = TaskExecuting.class.getDeclaredMethod("makeAvailable", Task.class);
        TEmethodmakeUnavailable = TaskExecuting.class.getDeclaredMethod("makeUnavailable", Task.class);
        TEmethodmakeAvailable.setAccessible(true);
        TEmethodmakeUnavailable.setAccessible(true);

        //Finished
        FmethodmakeAvailable = TaskFinished.class.getDeclaredMethod("makeAvailable", Task.class);
        FmethodmakeUnavailable = TaskFinished.class.getDeclaredMethod("makeUnavailable", Task.class);
        FmethodmakeAvailable.setAccessible(true);
        FmethodmakeUnavailable.setAccessible(true);

        //Failed
        FamethodmakeAvailable = TaskFailed.class.getDeclaredMethod("makeAvailable", Task.class);
        FamethodmakeUnavailable = TaskFailed.class.getDeclaredMethod("makeUnavailable", Task.class);
        FamethodmakeAvailable.setAccessible(true);
        FamethodmakeUnavailable.setAccessible(true);
    }

/////////////////////////// UNAVAILABLE___TO____ //////////////////////

    // TODO: alle testen waar er gebruik gemaakt wordt van de string aanpassen

    @Test
    public void unavailableToAvailable() {
        task2.addNewDependencyConstraint(task1);
        assertTrue(task1.isAvailable());
        assertTrue(task2.isUnavailable());
        task1.execute(LocalDateTime.now());
        task1.finish(LocalDateTime.now());
        assertTrue(task1.isFinished());
        assertTrue(task2.isAvailable());
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void unavailableToExecuting() {
        task2.addNewDependencyConstraint(task1);
        assertTrue(task1.isAvailable());
        assertTrue(task2.isUnavailable());
        task2.execute(LocalDateTime.now());
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void unavailableToFailed() {
        task2.addNewDependencyConstraint(task1);
        assertTrue(task1.isAvailable());
        assertTrue(task2.isUnavailable());
        task2.fail(LocalDateTime.now());
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void unavailableToFinished() {
        task2.addNewDependencyConstraint(task1);
        assertTrue(task1.isAvailable());
        assertTrue(task2.isUnavailable());
        task2.finish(LocalDateTime.now());
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void unavailableToUnavailable() throws Throwable {
        task2.addNewDependencyConstraint(task1);
        assertTrue(task1.isAvailable());
        assertTrue(task2.isUnavailable());
        TaskUnavailable test = (TaskUnavailable) task2.getStatus();
        try {
            TUmethodMakeUnAvailable.invoke(test, task2);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        assertTrue(task2.isUnavailable());
    }




//////////////////////// AVAILABLE___TO____ //////////////////////

    @Test
    public void availableToUnavailable() {
        task2.addNewDependencyConstraint(task1);
        assertTrue(task1.isAvailable());
        assertTrue(task2.isUnavailable());
    }

    @Test
    public void availableToExecuting() throws Exception {
        assertTrue(task1.isAvailable());
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        assertTrue(task1.isExecuting());
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void availableToFailedTest() throws Exception {
        assertTrue(task1.isAvailable());
        task1.fail(now);
    }


    @Test(expected = IllegalStateTransitionException.class)
    public void availableToFinishedTest() throws Exception {
        assertTrue(task1.isAvailable());
        task1.finish(now);
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void availableToAvailable() throws Throwable {

        TaskAvailable test = (TaskAvailable) task1.getStatus();
        try {
            TAmethodMakeAvailable.invoke(test, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
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
        assertTrue(task1.isFailed());
        assertEquals(LocalDateTime.of(2015, 3, 12, 10, 0), task1.getEndTime());
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void executingToExecuting() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute(LocalDateTime.of(2015, 3, 12, 18, 0));
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void executingToAvailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        TaskExecuting state = (TaskExecuting) task1.getStatus();
        try {
            TEmethodmakeAvailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void executingToUnavailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        TaskExecuting state = (TaskExecuting) task1.getStatus();
        try {
            TEmethodmakeUnavailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }



    //////////////////////// FINISHED___TO____ //////////////////////

    @Test(expected = IllegalStateTransitionException.class)
    public void finishedToExecuting() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 12, 8, 0).plusHours(2));
        assertTrue(task1.isFinished());
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0).plusHours(4));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void finishedToFailed() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.isFinished());
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void finishedToFinished() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.isFinished());
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void finishedToAvailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        task1.finish(LocalDateTime.now());
        TaskFinished state = (TaskFinished) task1.getStatus();
        try {
            FmethodmakeAvailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void finishedToUnavailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        task1.finish(LocalDateTime.now());
        TaskFinished state = (TaskFinished) task1.getStatus();
        try {
            FmethodmakeUnavailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    //////////////////////// FAILED___TO____ //////////////////////

    @Test(expected = IllegalStateTransitionException.class)
    public void failedToExecuting() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.isFailed());
        task1.execute(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void failedToFinished() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.isFailed());
        task1.finish(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test(expected = IllegalStateTransitionException.class)
    public void failedToFailed() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
        assertTrue(task1.isFailed());
        task1.fail(LocalDateTime.of(2015, 3, 12, 10, 0));
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void failedToAvailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        task1.fail(LocalDateTime.now());
        TaskFailed state = (TaskFailed) task1.getStatus();
        try {
            FamethodmakeAvailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void failedToUnavailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        task1.fail(LocalDateTime.now());
        TaskFailed state = (TaskFailed) task1.getStatus();
        try {
            FamethodmakeUnavailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}