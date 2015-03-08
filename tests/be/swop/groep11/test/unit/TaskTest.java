package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTest {

    private Task task;
    private Project project;
    private int taskID;
    @Before
    public void setUp() throws Exception {
        taskID = 0;
        project = new Project(0,"Test project", "Test beschrijving",
                    LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015,3,4,16,0),
                    new User("Alfred J. Kwak"));
        task = new Task(taskID,"Test taak", Duration.ofHours(8), 0.1, project);
    }

    /*
        Aanmaken van taak: geldige input controleren
     */

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Null() throws Exception {
        Task invalidTask = new Task(taskID,null, Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Empty() throws Exception {
        Task invalidTask = new Task(taskID,"", Duration.ofHours(8), 0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidAcceptableDeviation_Negative() throws Exception {
        Task invalidTask = new Task(taskID,"Test taak", Duration.ofHours(8), -0.1, project);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidProject_Null() throws Exception {
        Task invalidTask = new Task(taskID,"Test taak", Duration.ofHours(8), 0.1, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void newTask_InvalidProject_Finished() throws Exception {
        project.finish();
        Task invalidTask = new Task(taskID,"Test taak", Duration.ofHours(8), 0.1, project);
    }
}