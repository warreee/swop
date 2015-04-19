package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.TMSystem;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.task.IllegalStateTransition;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class TaskStatusTest {

    Project project;
    Task task1, task2;
    Method methodMakeAvailable, methodMakeUnAvailable;

    @Before
    public void setUp() throws NoSuchMethodException {
        LocalDateTime date = LocalDateTime.of(2015, 3, 12, 8, 0);
        ProjectRepository projectRepository = new TMSystem().getProjectRepository();
        project = new Project("Test project", "Mijn eerste project", date, date.plusHours(6), new User("Ik"), projectRepository);
        project.addNewTask("Taak1", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak2", 0.1, Duration.ofHours(1));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);

        methodMakeAvailable = Task.class.getDeclaredMethod("makeAvailable");
        methodMakeUnAvailable = Task.class.getDeclaredMethod("makeUnAvailable");
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

//////////////////////// AVAILABLE___TO____ //////////////////////
    @Test(expected = IllegalStateTransition.class)
    public void availableToExecuting() throws Exception {
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.execute();
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        assertTrue(task1.getStatusString().equals("EXECUTING"));
    }

    @Test(expected = IllegalStateTransition.class)
    public void availableToFailedTest() throws Exception {
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.fail();
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
    }


    @Test(expected = IllegalStateTransition.class)
    public void availableToFinishedTest() throws Exception {
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
        task1.finish();
        assertTrue(task1.getStatusString().equals("AVAILABLE"));
    }

    //////////////////////// EXECUTING___TO____ //////////////////////

    @Test(expected = IllegalStateTransition.class)
    public void executingToFinishedTest() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        assertTrue(task1.getStatusString().equals("EXECUTING"));
        task1.finish();
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.finish();
        assertTrue(task1.getStatusString().equals("FINISHED"));
    }

    @Test(expected = IllegalStateTransition.class)
    public void executingToFailed() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        // TODO heeft een gefailde taak ook een eindtijd? zie ook githubissue
        task1.fail();
        assertTrue(task1.getStatusString().equals("FAILED"));
    }

    @Test(expected = IllegalStateTransition.class)
    public void executingToExecuting() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.execute();
    }

    //////////////////////// FINISHED___TO____ //////////////////////

    @Test(expected = IllegalStateTransition.class)
    public void finishedToExecuting() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.finish();
        assertTrue(task1.getStatusString().equals("FINISHED"));
        task1.execute();
    }

    @Test(expected = IllegalStateTransition.class)
    public void finishedToFailed() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.finish();
        assertTrue(task1.getStatusString().equals("FINISHED"));
        task1.fail();
    }

    @Test(expected = IllegalStateTransition.class)
    public void finishedToFinished() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.finish();
        assertTrue(task1.getStatusString().equals("FINISHED"));
        task1.finish();
    }

    //////////////////////// FAILED___TO____ //////////////////////

    // TODO: nog nakijken of er nu een starttijd vereist is voor de fAILed status

    @Test(expected = IllegalStateTransition.class)
    public void failedToExecuting() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.fail();
        assertTrue(task1.getStatusString().equals("FAILED"));
        task1.execute();
    }

    @Test(expected = IllegalStateTransition.class)
    public void failedToFinished() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.fail();
        assertTrue(task1.getStatusString().equals("FAILED"));
        task1.finish();
    }

    @Test(expected = IllegalStateTransition.class)
    public void failedToFailed() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.execute();
        task1.fail();
        assertTrue(task1.getStatusString().equals("FAILED"));
        task1.fail();
    }

}
