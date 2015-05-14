package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
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

    BranchOffice branchOffice, otherBranchOffice;
    ProjectRepository projectRepository1, projectRepository2;
    Task task;

    @Before
    public void setUp() throws Exception {
        ResourcePlanner resourcePlanner = mock(ResourcePlanner.class);
        when(resourcePlanner.canPlan(any())).thenReturn(true);
        projectRepository1 = mock(ProjectRepository.class);
        branchOffice = new BranchOffice("Branch office 1", "Locatie 1", projectRepository1, resourcePlanner);
        projectRepository2 = mock(ProjectRepository.class);
        otherBranchOffice = new BranchOffice("Branch office 2", "Locatie 2", projectRepository2, resourcePlanner);
        task = mock(Task.class);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(projectRepository1.getAllTasks()).thenReturn(tasks);
        when(projectRepository2.getAllTasks()).thenReturn(new ArrayList<>());
    }

    @Test
    public void delegateTask_ProperToOther() throws Exception {
        when(task.isUnavailable()).thenReturn(true);

        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice);
        branchOffice.delegateTask(task, otherBranchOffice);

        verify(task).setDelegatedTo(otherBranchOffice);
        when(task.isDelegated()).thenReturn(true);

        assertTrue(branchOffice.getAllTasks().contains(task));
        assertFalse(branchOffice.getDelegatedTasks().contains(task));
        assertFalse(branchOffice.getUnplannedTasks().contains(task));
        assertFalse(otherBranchOffice.getAllTasks().contains(task));
        assertTrue(otherBranchOffice.getDelegatedTasks().contains(task));
        assertTrue(otherBranchOffice.getUnplannedTasks().contains(task));
    }

    @Test
    public void delegateTask_OtherToProper() throws Exception {
        when(task.isUnavailable()).thenReturn(true);

        // branchOffice -> otherBranchOffice
        when(task.isDelegated()).thenReturn(false);
        when(task.getDelegatedTo()).thenReturn(branchOffice);
        branchOffice.delegateTask(task, otherBranchOffice);
        when(task.isDelegated()).thenReturn(true);
        when(task.getDelegatedTo()).thenReturn(otherBranchOffice);
        // otherBranchOffice -> branchOffice
        otherBranchOffice.delegateTask(task, branchOffice);
        verify(task).setDelegatedTo(branchOffice);
        when(task.getDelegatedTo()).thenReturn(branchOffice);
        when(task.isDelegated()).thenReturn(false);

        assertTrue(branchOffice.getAllTasks().contains(task));
        assertFalse(branchOffice.getDelegatedTasks().contains(task));
        assertTrue(branchOffice.getUnplannedTasks().contains(task));
        assertFalse(otherBranchOffice.getAllTasks().contains(task));
        assertFalse(otherBranchOffice.getDelegatedTasks().contains(task));
        assertFalse(otherBranchOffice.getUnplannedTasks().contains(task));
    }

    @Test
    public void delegateTask_OtherToOther() throws Exception {
        // TODO
    }

}
