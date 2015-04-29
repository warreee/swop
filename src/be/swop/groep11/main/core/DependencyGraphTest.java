package be.swop.groep11.main.core;

import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by warreee on 30/04/15.
 */
public class DependencyGraphTest {

    DependencyGraph dependencyGraph;
    Task aTask;
    Task bTask;
    Task cTask;

    @Before
    public void setUp() throws Exception {
        this.dependencyGraph = new DependencyGraph();

    }

    @Test
    public void getLeafsTest() {

    }

}
