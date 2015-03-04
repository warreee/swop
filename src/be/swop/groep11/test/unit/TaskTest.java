package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectStatus;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TaskTest {

    private Task task;
    private Project project;

    @Before
    public void setUp() throws Exception {
        project = new Project("Test project", "Test beschrijving",
                    LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015,3,4,16,0),
                    new User("Alfred J. Kwak"));
        task = new Task("Test taak", Duration.ofHours(8), 0.1, project);
    }

    /*
        Aanmaken van taak: geldige input controleren
     */

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Null() throws Exception {
        Task invalidTask = new Task(null, Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Empty() throws Exception {
        Task invalidTask = new Task("", Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidAcceptableDeviation_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), -0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidProject_Null() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), 0.1, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidProject_Finished() throws Exception {
        project.finish();
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), 0.1, project);
    }
}