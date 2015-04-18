package be.swop.groep11.test.unit;

import be.swop.groep11.main.*;
import be.swop.groep11.main.task.IllegalStateTransition;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskStatus;
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
        project = new Project("Test project", "Mijn eerste project", date, date.plusHours(6), new User("Ik"),projectRepository);
        project.addNewTask("Taak1", 0.1, Duration.ofHours(1));
        project.addNewTask("Taak2", 0.1, Duration.ofHours(1));
        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);

        methodMakeAvailable = Task.class.getDeclaredMethod("makeAvailable");
        methodMakeUnAvailable = Task.class.getDeclaredMethod("makeUnAvailable");
        methodMakeAvailable.setAccessible(true);
        methodMakeUnAvailable.setAccessible(true);
    }


    /**
     * Taken zijn standaard geïnitialiseerd op available!
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test(expected = IllegalStateTransition.class)
    public void availableToAvailableTest() throws Exception {

        methodMakeAvailable.invoke(task1);

    }

    /**
     * Maak een taak unavailable, daarna terug available en daarna nog is available TODO: huh?
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
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
    }

    @Test (expected = IllegalStateTransition.class)
    public void availableToFailedTest() throws Exception {
        task1.fail();
    }


    @Test (expected = IllegalStateTransition.class)
    public void availableToFinishedTest() throws Exception {
        task1.finish();
    }


/*


    task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.makeAvailable();
        task1.addNewDependencyConstraint(task2); // Dit zou ervoor moeten zorgen dat taak1 op UNVAILABLE komt.


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
        // UNAVAILABLE -> FAILED mag altijd.
        task1.addNewDependencyConstraint(task2); // Dit zet de status van taak1 op Unavailable.
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        task2.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task2.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task2.setNewStatus(TaskStatus.FINISHED); // zorgt er ook voor dat taak 1 op available komt
        // maar AVAILABLE -> FAILED mag niet zonder start en eindtijd te zetten
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 11, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 12, 0));
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
    }

    @Test
    public void finishedToUnvailable() throws Exception {
        //FINISHED -> UNAVAILABLE mag nooit.
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.setNewStatus(TaskStatus.FINISHED); //Taak1 is nu FINISHED
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE, task1));
    }

    @Test
    public void finishedToAvailable() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.setNewStatus(TaskStatus.FINISHED); //Taak1 is nu FINISHED
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task1));
    }

    @Test
    public void finishedToFailed() throws Exception {
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.setNewStatus(TaskStatus.FINISHED); //Taak1 is nu FINISHED
        assertFalse(TaskStatus.isValidNewStatus(TaskStatus.FAILED, task1));
    }

    @Test
    public void finishedToFinished() throws Exception {
        task1.setStartTime(LocalDateTime.of(2015, 3, 12, 8, 0));
        task1.setEndTime(LocalDateTime.of(2015, 3, 12, 10, 0));
        task1.setNewStatus(TaskStatus.FINISHED); //Taak1 is nu FINISHED
        assertTrue(TaskStatus.isValidNewStatus(TaskStatus.FINISHED, task1));
    }*/
}
