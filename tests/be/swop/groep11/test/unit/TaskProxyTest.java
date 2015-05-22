package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by Arne De Brabandere_3 on 20/05/2015.
 */
public class TaskProxyTest extends TaskTest{

    /**
     * Overschrijft alle task object met hun proxyvariant. Aangezien het hier om een proxy gaat zou alles nog moeten
     * slagen.
     * @throws Exception
     */
    @Before
    public void setupProxy() throws Exception{
        setUp();
        task1 = new TaskProxy(task1);
        task2 = new TaskProxy(task2);
        task3 = new TaskProxy(task3);
    }

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
        assertFalse(proxy.equals("Random String"));
        assertFalse(proxy.equals(null));
    }

    @Test
    public void getterSettersTest(){
        task1.setEstimatedDuration(Duration.ofHours(3));
        assertTrue(task1.getEstimatedDuration().equals(Duration.ofHours(3)));

        task1.setAcceptableDeviation(.2);
        assertEquals(.2, task1.getAcceptableDeviation(), 1e-6);

        assertTrue(task1.getStartTime() == null);
        assertTrue(task1.getEndTime() == null);

        assertFalse(task1.hasStartTime());
        assertFalse(task1.hasEndTime());

        assertTrue(task1.getDependentTasks().size() == 0);
        assertTrue(task1.getDependingOnTasks().size() == 0);

        assertTrue(task1.getProject().equals(project));

        assertFalse(task1.isUnavailable());

        assertTrue(task1.getDuration(systemTime.getCurrentSystemTime()).equals(Duration.ofHours(3)));

        assertTrue(task1.getPlan().equals(testPlan));

        assertTrue(task1.isPlanned());

        assertFalse(task1.isDelegated());

        assertTrue(task1.getDelegatedTo().equals(branchOffice));

        assertFalse(task1.canHaveAsDelegatedTo(branchOffice));
    }

}
