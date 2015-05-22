package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


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

    private Constructor<TaskUnavailable> taskUnavailableConstructor;
    private Constructor<TaskAvailable> taskAvailableConstructor;
    private Constructor<TaskExecuting> taskExecutingConstructor;
    private Constructor<TaskFinished> taskFinishedConstructor;
    private Constructor<TaskFailed> taskFailedConstructor;



    @Before
    public void setUp() throws NoSuchMethodException {
        now = LocalDateTime.of(2015, 3, 12, 8, 0);
        systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        project = new Project("Test project", "Mijn eerste project", now, now.plusHours(6), systemTime, branchOffice);

        project.addNewTask("Taak1", 0.1, Duration.ofHours(1), mock(IRequirementList.class));
        project.addNewTask("Taak2", 0.1, Duration.ofHours(1), mock(IRequirementList.class));
        project.addNewTask("Taak3", 0.1, Duration.ofHours(1), mock(IRequirementList.class));
        project.addNewTask("Taak4", 0.1, Duration.ofHours(1), mock(IRequirementList.class));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
        task3 = project.getTasks().get(2);
        when(branchOffice.containsTask(any())).thenReturn(true);

        // Plan zetten
        // zorg dat elke taak een plan heeft en available wordt
        Plan testPlan = mock(Plan.class);
        when(branchOffice.isTaskPlanned(eq(task1))).thenReturn(true);
        when(branchOffice.isTaskPlanned(eq(task2))).thenReturn(true);
        when(branchOffice.isTaskPlanned(eq(task3))).thenReturn(true);
        when(branchOffice.getPlanForTask(eq(task1))).thenReturn(testPlan);
        when(branchOffice.getPlanForTask(eq(task2))).thenReturn(testPlan);
        when(branchOffice.getPlanForTask(eq(task3))).thenReturn(testPlan);
        when(testPlan.hasEquivalentPlan()).thenReturn(true);
        when(testPlan.isWithinPlanTimeSpan(any())).thenReturn(true);

        LocalDateTime startTime = LocalDateTime.of(2015,1,1,0,0);
        TimeSpan timeSpan = new TimeSpan(startTime, startTime.plusHours(1));
        when(testPlan.getTimeSpan()).thenReturn(timeSpan);

        systemTime.addObserver(task1.getSystemTimeObserver());
        systemTime.addObserver(task2.getSystemTimeObserver());
        systemTime.addObserver(task3.getSystemTimeObserver());
        systemTime.updateSystemTime(systemTime.getCurrentSystemTime().plusDays(1));

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

        //Constructors
        taskUnavailableConstructor = (Constructor<TaskUnavailable>) TaskUnavailable.class.getDeclaredConstructors()[0];
        taskUnavailableConstructor.setAccessible(true);

        taskAvailableConstructor = (Constructor<TaskAvailable>) TaskAvailable.class.getDeclaredConstructors()[0];
        taskAvailableConstructor.setAccessible(true);

        taskExecutingConstructor = (Constructor<TaskExecuting>) TaskExecuting.class.getDeclaredConstructors()[0];
        taskExecutingConstructor.setAccessible(true);

        taskFinishedConstructor = (Constructor<TaskFinished>) TaskFinished.class.getDeclaredConstructors()[0];
        taskFinishedConstructor.setAccessible(true);

        taskFailedConstructor = (Constructor<TaskFailed>) TaskFailed.class.getDeclaredConstructors()[0];
        taskFailedConstructor.setAccessible(true);

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
        TaskUnavailable test = taskUnavailableConstructor.newInstance();
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

        TaskAvailable test = taskAvailableConstructor.newInstance();
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
        TaskExecuting state = taskExecutingConstructor.newInstance();
        try {
            TEmethodmakeAvailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Test (expected = IllegalStateTransitionException.class)
    public void executingToUnavailable() throws Throwable {
        task1.execute(LocalDateTime.now());
        TaskExecuting state = taskExecutingConstructor.newInstance();
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
        TaskFinished state = taskFinishedConstructor.newInstance();
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
        TaskFinished state = taskFinishedConstructor.newInstance();
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
        TaskFailed state = taskFailedConstructor.newInstance();
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
        TaskFailed state = taskFailedConstructor.newInstance();
        try {
            FamethodmakeUnavailable.invoke(state, task1);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}