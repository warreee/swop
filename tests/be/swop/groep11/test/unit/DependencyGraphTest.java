package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.exception.IllegalDependencyException;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
    Task taskF;
    Task taskG;
    Task taskH;
    ArrayList<Task> allTasks = new ArrayList<>();


    @Before
    public void setUp() throws Exception {
        taskA = new Task("Taak A", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskB = new Task("Taak B", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskC = new Task("Taak C", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskD = new Task("Taak D", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskE = new Task("Taak E", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskF = new Task("Taak F", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskG = new Task("Taak G", duration, deviation, systemTime, dependencyGraph, mock(Project.class));
        taskH = new Task("Taak H", duration, deviation, systemTime, dependencyGraph, mock(Project.class));

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
        taskC.addNewDependencyConstraint(taskB);
        dependencyGraph.addNewDependency(taskB, taskD);
        dependencyGraph.addNewDependency(taskB, taskE);
        dependencyGraph.addNewDependency(taskC, taskE);

        assertTrue(taskA.getDependentTasks().contains(taskB));
        assertTrue(taskB.getDependentTasks().contains(taskC));
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

    /**
     * A -> B -> C
     */
    @Test
    public void removeDependencyTest() {
        dependencyGraph.addNewDependency(taskB, taskA);
        dependencyGraph.addNewDependency(taskC, taskB);
        helpPrint();
        dependencyGraph.changeDependingOnAlternativeTask(taskA, taskD);
        System.out.println("");
        helpPrint();
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
        //helpPrint();
        ArrayList<Task> leafs = dependencyGraph.getLeafs();

        assertTrue(leafs.get(0) == taskC);

        taskD.addNewDependencyConstraint(taskB);
        leafs = dependencyGraph.getLeafs();

        assertTrue(leafs.contains(taskC));
        assertTrue(leafs.contains(taskD));
    }

    /*
     * A -> B \
     *          E
     * C -> D /
     */
    @Test
    public void getPathsToTest() {
        taskB.addNewDependencyConstraint(taskA);
        taskE.addNewDependencyConstraint(taskB);
        taskD.addNewDependencyConstraint(taskC);
        taskE.addNewDependencyConstraint(taskD);
        ArrayList<ArrayList<Task>> paths = dependencyGraph.getPathsTo(taskE);
        assertEquals(paths.size(), 2);
        assertTrue(paths.get(0).contains(taskE) && paths.get(1).contains(taskE));
        assertTrue((paths.get(0).contains(taskC) && paths.get(1).contains(taskA)) ^
                (paths.get(1).contains(taskC) && paths.get(0).contains(taskA)));
        assertTrue((paths.get(0).contains(taskB) && paths.get(1).contains(taskD)) ^
                (paths.get(1).contains(taskB) && paths.get(0).contains(taskD)));

    }

    /*
    * A -> B >   > G -> H
    *         \ /  |
    *          E   |
    *         ^ \  >
    * C -> D /   > F
    */
    @Test
    public void getComplicatedPathsToTest() {
        taskB.addNewDependencyConstraint(taskA);
        taskE.addNewDependencyConstraint(taskB);
        taskD.addNewDependencyConstraint(taskC);
        taskE.addNewDependencyConstraint(taskD);
        taskG.addNewDependencyConstraint(taskE);
        taskF.addNewDependencyConstraint(taskE);
        taskF.addNewDependencyConstraint(taskG);
        taskH.addNewDependencyConstraint(taskG);

        HashMap<Task, ArrayList<ArrayList<Task>>> leafPaths = new HashMap<>();
        ArrayList<Task> leafs =dependencyGraph.getLeafs();
        for (Task leaf : leafs){
            leafPaths.put(leaf, dependencyGraph.getPathsTo(leaf));
        }
        assertEquals(leafPaths.get(taskH).size(), 2);
        assertEquals(leafPaths.get(taskF).size(), 4);
        //helpPrint();
    }


}


