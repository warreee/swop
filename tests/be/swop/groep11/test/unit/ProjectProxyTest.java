package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
import org.junit.Test;
import org.mockito.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProjectProxyTest {

    @Test
    public void equals_TrueTest() {
        Project project = new Project("project naam", "beschrijving", LocalDateTime.of(2015,1,1,8,0), LocalDateTime.of(2015,1,1,10,0), mock(SystemTime.class), mock(BranchOffice.class));
        ProjectProxy proxy = new ProjectProxy(project);
        assertTrue(project.equals(proxy));
        assertTrue(proxy.equals(project));
        assertTrue(proxy.equals(proxy));
    }

    @Test
    public void equals_FalseTest() {
        Project project = new Project("project naam", "beschrijving", LocalDateTime.of(2015,1,1,8,0), LocalDateTime.of(2015,1,1,10,0), mock(SystemTime.class), mock(BranchOffice.class));
        ProjectProxy proxy = new ProjectProxy(project);
        Project project2 = new Project("project naam 2", "beschrijving", LocalDateTime.of(2015,1,1,9,0), LocalDateTime.of(2015, 1, 1, 10, 0), mock(SystemTime.class), mock(BranchOffice.class));
        assertFalse(project2.equals(proxy));
        assertFalse(proxy.equals(project2));
    }

}
