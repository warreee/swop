package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Klasse om BranchOffice te te testen
 */
public class BranchOfficeTest {

    BranchOffice branchOffice1, branchOffice2, branchOffice3;
    ProjectRepository projectRepository1, projectRepository2, projectRepository3;
    ResourcePlanner resourcePlanner1, resourcePlanner2, resourcePlanner3;
    Task task;
    User user1, user2, user3;

    @Before
    public void setUp() throws Exception {
        // branch office 1
        projectRepository1 = mock(ProjectRepository.class);
        resourcePlanner1 = mock(ResourcePlanner.class);
        when(resourcePlanner1.canPlan(any())).thenReturn(true);
        branchOffice1 = new BranchOffice("Branch office 1", "Locatie 1", projectRepository1, resourcePlanner1);
        // branch office 2
        projectRepository2 = mock(ProjectRepository.class);
        resourcePlanner2 = mock(ResourcePlanner.class);
        when(resourcePlanner2.canPlan(any())).thenReturn(true);
        branchOffice2 = new BranchOffice("Branch office 2", "Locatie 2", projectRepository2, resourcePlanner2);
        // branch office 3
        projectRepository3 = mock(ProjectRepository.class);
        resourcePlanner3 = mock(ResourcePlanner.class);
        when(resourcePlanner3.canPlan(any())).thenReturn(true);
        branchOffice3 = new BranchOffice("Branch office 3", "Locatie 3", projectRepository3, resourcePlanner3);

        // taak in branch office 1
        task = mock(Task.class);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(projectRepository1.getAllTasks()).thenReturn(tasks);
        when(projectRepository2.getAllTasks()).thenReturn(new ArrayList<>());
        when(projectRepository3.getAllTasks()).thenReturn(new ArrayList<>());

        user1 = mock(User.class);
        user2 = mock(User.class);
        user3 = mock(User.class);
    }

    @Test
    public void delegateTask_ProperToOther() throws Exception {
        when(task.isUnavailable()).thenReturn(true);

        // branchOffice1 -> branchOffice2 = proper to other
        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice1);
        branchOffice1.delegateTask(task, branchOffice2);

        verify(task).setDelegatedTo(branchOffice2);
        when(task.isDelegated()).thenReturn(true);

        assertTrue(branchOffice1.getAllTasks().contains(task));
        assertFalse(branchOffice1.getDelegatedTasks().contains(task));
        assertFalse(branchOffice1.getUnplannedTasks().contains(task));
        assertFalse(branchOffice2.getAllTasks().contains(task));
        assertTrue(branchOffice2.getDelegatedTasks().contains(task));
        assertTrue(branchOffice2.getUnplannedTasks().contains(task));
    }

    @Test
    public void delegateTask_OtherToProper() throws Exception {
        when(task.isUnavailable()).thenReturn(true);

        // branchOffice1 -> branchOffice2
        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice1);
        branchOffice1.delegateTask(task, branchOffice2);
        when(task.isDelegated()).thenReturn(true);
        when(task.getDelegatedTo()).thenReturn(branchOffice2);
        // branchOffice2 -> branchOffice1 = other to proper
        branchOffice2.delegateTask(task, branchOffice1);
        verify(task).setDelegatedTo(branchOffice1);
        when(task.getDelegatedTo()).thenReturn(branchOffice1);
        when(task.isDelegated()).thenReturn(false);

        assertTrue(branchOffice1.getAllTasks().contains(task));
        assertFalse(branchOffice1.getDelegatedTasks().contains(task));
        assertTrue(branchOffice1.getUnplannedTasks().contains(task));
        assertFalse(branchOffice2.getAllTasks().contains(task));
        assertFalse(branchOffice2.getDelegatedTasks().contains(task));
        assertFalse(branchOffice2.getUnplannedTasks().contains(task));
    }

    @Test
    public void delegateTask_OtherToOther() throws Exception {
        when(task.isUnavailable()).thenReturn(true);

        // branchOffice1 -> branchOffice2
        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice1);
        branchOffice1.delegateTask(task, branchOffice2);
        when(task.isDelegated()).thenReturn(true);
        when(task.getDelegatedTo()).thenReturn(branchOffice2);
        // branchOffice2 -> branchOffice3 = other to other
        branchOffice2.delegateTask(task, branchOffice3);
        verify(task).setDelegatedTo(branchOffice3);
        when(task.getDelegatedTo()).thenReturn(branchOffice3);
        when(task.isDelegated()).thenReturn(true);

        assertFalse(branchOffice2.getAllTasks().contains(task));
        assertFalse(branchOffice2.getDelegatedTasks().contains(task));
        assertFalse(branchOffice2.getUnplannedTasks().contains(task));
        assertFalse(branchOffice3.getAllTasks().contains(task));
        assertTrue(branchOffice3.getDelegatedTasks().contains(task));
        assertTrue(branchOffice3.getUnplannedTasks().contains(task));
    }

    @Test (expected = IllegalArgumentException.class)
    public void delegateTask_SameBranchOffice() throws Exception {
        when(task.isUnavailable()).thenReturn(true);

        // branchOffice1 -> branchOffice1
        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice1);
        branchOffice1.delegateTask(task, branchOffice1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void delegateTask_CanNotPlanTask() throws Exception {
        when(resourcePlanner2.canPlan(task)).thenReturn(false);

        when(task.isUnavailable()).thenReturn(true);

        // branchOffice1 -> branchOffice2
        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice2);
        branchOffice1.delegateTask(task, branchOffice2);
    }


    @Test
    public void updateAllStatusTest() {
        fail("Nog niet geïmplementeerd!");
    }

    @Test
    public void addEmployeeTest() {
        branchOffice1.addEmployee(user1);
        branchOffice1.addEmployee(user2);
        branchOffice1.addEmployee(user3);

        assertTrue(branchOffice1.getEmployees().size() == 3);
    }

}
