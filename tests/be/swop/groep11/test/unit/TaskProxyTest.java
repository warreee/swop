package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by Arne De Brabandere_3 on 20/05/2015.
 */
public class TaskProxyTest {

    @Test
    public void equals_TrueTest() {
        Task task = new Task("description", Duration.ofHours(1), 0.0, mock(DependencyGraph.class), mock(IRequirementList.class), mock(Project.class));
        TaskProxy proxy = new TaskProxy(task);
        assertTrue(task.equals(proxy));
        assertTrue(proxy.equals(task));
        assertTrue(proxy.equals(proxy));
    }

    @Test
    public void equals_FalseTest() {
        Task task1 = new Task("description", Duration.ofHours(1), 0.0, mock(DependencyGraph.class), mock(IRequirementList.class), mock(Project.class));
        TaskProxy proxy = new TaskProxy(task1);
        Task task2 = new Task("description 2", Duration.ofHours(2), 0.0, mock(DependencyGraph.class), mock(IRequirementList.class), mock(Project.class));
        assertFalse(task2.equals(proxy));
        assertFalse(proxy.equals(task2));
    }

}
