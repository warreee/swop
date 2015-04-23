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
import java.util.function.Consumer;
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
    ArrayList<Task> allTasks = new ArrayList<>();


    @Before
    public void setUp() throws Exception {
        taskA = new Task("Taak A", duration, deviation, systemTime, dependencyGraph);
        taskB = new Task("Taak B", duration, deviation, systemTime, dependencyGraph);
        taskC = new Task("Taak C", duration, deviation, systemTime, dependencyGraph);
        taskD = new Task("Taak D", duration, deviation, systemTime, dependencyGraph);
        taskE = new Task("Taak E", duration, deviation, systemTime, dependencyGraph);

        allTasks.add(taskA);
        allTasks.add(taskB);
        allTasks.add(taskC);
        allTasks.add(taskD);
        allTasks.add(taskE);

    }

    private void helpPrint() {

            allTasks.forEach(task -> {
                try {
                    dependencyGraph.getDependingOnTasks(task).stream().map((t) -> task.getDescription()
                            + " hangt af van: " + t.getDescription()).forEach(System.out::println);
                } catch (NullPointerException e) {
                }
            });

            allTasks.forEach(task -> {
                try {
                    dependencyGraph.getDependentTasks(task).stream().map((t) -> task.getDescription()
                            + " heeft als afhankelijke taak: " + t.getDescription()).forEach(System.out::println);
                } catch (NullPointerException e){
                }
            });

    }

    @Test
    public void addDependencyTest(){
        dependencyGraph.addDependency(taskB, taskA);
        dependencyGraph.addDependency(taskB, taskC);
        dependencyGraph.addDependency(taskB, taskD);
        dependencyGraph.addDependency(taskB, taskE);
        dependencyGraph.addDependency(taskC, taskE);

        assertTrue(taskA.getDependentTasks().contains(taskB));
        assertTrue(taskC.getDependentTasks().contains(taskB));
        assertTrue(taskD.getDependentTasks().contains(taskB));
        assertTrue(taskE.getDependentTasks().contains(taskB));
        assertFalse(taskA.getDependingOnTasks().contains(taskB));

        assertFalse(taskB.getDependentTasks().contains(taskA));
        assertTrue(taskB.getDependingOnTasks().contains(taskA));

        //helpPrint();

    }



    @Test
    public void removeDependencyTest() {
        dependencyGraph.addDependency(taskB, taskA);
        dependencyGraph.addDependency(taskB, taskC);
        helpPrint();
        dependencyGraph.changeDepeningOnAlternativeTask(taskA, taskD);
        System.out.println("???????????????????");
        helpPrint();
    }




}

