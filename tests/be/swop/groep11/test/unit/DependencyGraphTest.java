package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertTrue;

/**
 * Created by warreee on 4/21/15.
 */
public class DependencyGraphTest {
    String description = "description";
    Duration duration = Duration.ofDays(1);
    SystemTime systemTime = new SystemTime();
    Double deviation = 0.2;
    DependencyGraph dependencyGraph = new DependencyGraph();
    Task taskA;
    Task taskB;
    Task taskC;
    Task taskD;
    Task taskE;

    @Before
    public void setUp() throws Exception {
        taskA = new Task(description, duration, deviation, systemTime, dependencyGraph);
        taskB = new Task(description, duration, deviation, systemTime, dependencyGraph);
        taskC = new Task(description, duration, deviation, systemTime, dependencyGraph);
        taskD = new Task(description, duration, deviation, systemTime, dependencyGraph);
        taskE = new Task(description, duration, deviation, systemTime, dependencyGraph);

    }

    @Test
    public void addDependencyTest(){
        dependencyGraph.addDependency(taskA, taskB);
        assertTrue(true);
    }

}

