package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.IllegalDependencyException;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
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
                } catch (NullPointerException e) {
                }
            });

    }

    @Test
    public void addDependencyTest(){
        taskB.addNewDependencyConstraint(taskA);
        dependencyGraph.addNewDependency(taskB, taskC);
        dependencyGraph.addNewDependency(taskB, taskD);
        dependencyGraph.addNewDependency(taskB, taskE);
        dependencyGraph.addNewDependency(taskC, taskE);

        assertTrue(taskA.getDependentTasks().contains(taskB));
        assertTrue(taskC.getDependentTasks().contains(taskB));
        assertTrue(taskD.getDependentTasks().contains(taskB));
        assertTrue(taskE.getDependentTasks().contains(taskB));
        assertFalse(taskA.getDependingOnTasks().contains(taskB));

        assertFalse(taskB.getDependentTasks().contains(taskA));
        assertTrue(taskB.getDependingOnTasks().contains(taskA));

        //helpPrint();

    }

    @Test (expected = IllegalDependencyException.class)
    public void addDuplicateDependencyTest() {
        taskA.addNewDependencyConstraint(taskB);
        taskA.addNewDependencyConstraint(taskB);
    }

    @Test
    public void removeDependencyTest() {
        dependencyGraph.addNewDependency(taskB, taskA);
        dependencyGraph.addNewDependency(taskC, taskB);
        //helpPrint();
        dependencyGraph.changeDependingOnAlternativeTask(taskA, taskD);
        System.out.println("");
        //helpPrint();
        dependencyGraph.getDependentTasks(taskA);
        assertFalse(dependencyGraph.getDependentTasks(taskB).contains(taskA));
    }

    /**
     * A --> B --> C --> D --> E --> A
     */
    @Test (expected = IllegalDependencyException.class)
    public void circularDependencyTest() {

        dependencyGraph.addNewDependency(taskC, taskB);
        dependencyGraph.addNewDependency(taskD, taskC);
        dependencyGraph.addNewDependency(taskE, taskD);
        dependencyGraph.addNewDependency(taskA, taskE);
        dependencyGraph.addNewDependency(taskB, taskA);


    }

    @Test
    public void getLeafsTest() {

        taskB.addNewDependencyConstraint(taskA);
        taskC.addNewDependencyConstraint(taskB);
        helpPrint();
        ArrayList<Task> leafs = dependencyGraph.getLeafs();

        assertTrue(leafs.get(0) == taskC);

        taskD.addNewDependencyConstraint(taskB);
        leafs = dependencyGraph.getLeafs();

        assertEquals(leafs.get(1), taskC);
        assertEquals(leafs.get(0), taskD);
    }

    /*
     * A -> B \
     *          E
     * C -> D /
     */
    @Test
    public void getPathsToTest(Task task) {

        ArrayList<ArrayList<Task>> paths = dependencyGraph.getPathsTo(taskE);

    }


}


