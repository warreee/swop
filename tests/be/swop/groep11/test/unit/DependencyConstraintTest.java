package be.swop.groep11.test.unit;

import be.swop.groep11.main.DependencyConstraint;
import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class DependencyConstraintTest {

    Task task1, task2, task3, task4;

    @Before
    public void setUp() throws Exception {
        Project project = new Project("Test project", "Test beschrijving",
                LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015,3,4,16,0),
                new User("Alfred J. Kwak"));
        task1 = new Task("Taak 1", Duration.ofHours(8), 0.1, project);
        task2 = new Task("Taak 2", Duration.ofHours(6), 0.1, project);
        task3 = new Task("Taak 3", Duration.ofHours(7), 0.5, project);
        task4 = new Task("Taak 4", Duration.ofHours(2), 0.0, project);
        task1.addNewDependencyConstraint(task2);
        task1.addNewDependencyConstraint(task3);
        task2.addNewDependencyConstraint(task3);
        task3.addNewDependencyConstraint(task4);
    }

    @Test
    public void isValidDependingOnTest() throws Exception {
        assertFalse(DependencyConstraint.isValidDependingOn(task4,task1));
        assertTrue(DependencyConstraint.isValidDependingOn(task1,task4));
        assertFalse(DependencyConstraint.isValidDependingOn(task4,task2));
        assertTrue(DependencyConstraint.isValidDependingOn(task2,task4));
        assertFalse(DependencyConstraint.isValidDependingOn(task2,task1));
        assertFalse(DependencyConstraint.isValidDependingOn(task1,task1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void cyclicDependencyAddTest() throws Exception {
        // Voeg een cyclische dependency toe.
        task3.addNewDependencyConstraint(task2);
    }

    @Test
    public void dependancyEqualsTest() throws Exception {
        DependencyConstraint d1 = new DependencyConstraint(task1, task2);
        DependencyConstraint d2 = new DependencyConstraint(task1, task2);
        DependencyConstraint d3 = new DependencyConstraint(task1, task3);
        assertTrue(d1.equals(d2));
        assertTrue(d1.equals(d1));
        assertFalse(d1.equals(null));
        assertFalse(d1.equals(d3));
    }

}