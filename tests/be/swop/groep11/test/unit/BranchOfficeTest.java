package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.*;

/**
 * Klasse om BranchOffice te te testen
 */
public class BranchOfficeTest {

    BranchOffice branchOffice, otherBranchOffice;
    Task task;

    @Before
    public void setUp() throws Exception {
        ProjectRepository projectRepository1 = mock(ProjectRepository.class);
        branchOffice = new BranchOffice("Branch office 1", "Locatie 1", projectRepository1);
        ProjectRepository projectRepository2 = mock(ProjectRepository.class);
        otherBranchOffice = new BranchOffice("Branch office 2", "Locatie 2", projectRepository2);
        task = mock(Task.class);
        List<Task> tasks = new ArrayList<>();
        when(projectRepository1.getAllAvailableTasks()).thenReturn(null);
    }

    @Test
    public void delegateTask_ProperToOther() throws Exception {
        when(task.isDelegated()).thenReturn(false);
        branchOffice.delegateTask(task, otherBranchOffice);
    }

}
