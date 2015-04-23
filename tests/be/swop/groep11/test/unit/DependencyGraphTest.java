package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
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
    ArrayList<Task> dependencies;
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
        assertTrue(taskA.getDependingOnTasks().contains(taskB));
        assertFalse(taskB.getDependingOnTasks().contains(taskA));
        assertFalse(taskB.getDependingOnTasks().contains(taskB));
        assertFalse(taskA.getDependingOnTasks().contains(taskA));

        assertFalse(taskA.getDependentTasks().contains(taskB));
        assertTrue(taskB.getDependentTasks().contains(taskA));
        Set<Task> te = dependencyGraph.getDependentTasks(taskA);
        System.out.println("test");
        dependencyGraph.getDependentTasks(taskA).stream().map((t) -> t.getDescription() + "\n").forEach(System.out::println);
        dependencyGraph.getDependentTasks(taskB).stream().map((t) -> t.getDescription() + "\n").forEach(System.out::println);
    }







}

