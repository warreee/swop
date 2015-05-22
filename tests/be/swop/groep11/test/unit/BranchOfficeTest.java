package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
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
    User projectManager, developer1, developer2;
    ResourceRepository resourceRepository1;

    @Before
    public void setUp() throws Exception {
        // branch office 1
        projectRepository1 = mock(ProjectRepository.class);
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();
        resourceRepository1 = new ResourceRepository(resourceTypeRepository);
        SystemTime systemTime = mock(SystemTime.class);
        resourcePlanner1 = mock(ResourcePlanner.class);
        when(resourcePlanner1.hasEnoughResourcesToPlan(any())).thenReturn(true);
        when(resourcePlanner1.getResourceRepository()).thenReturn(resourceRepository1);
        branchOffice1 = new BranchOffice("Branch office 1", "Locatie 1", projectRepository1, resourcePlanner1);
        // branch office 2
        projectRepository2 = mock(ProjectRepository.class);
        resourcePlanner2 = mock(ResourcePlanner.class);
        when(resourcePlanner2.hasEnoughResourcesToPlan(any())).thenReturn(true);
        branchOffice2 = new BranchOffice("Branch office 2", "Locatie 2", projectRepository2, resourcePlanner2);
        // branch office 3
        projectRepository3 = mock(ProjectRepository.class);
        resourcePlanner3 = mock(ResourcePlanner.class);
        when(resourcePlanner3.hasEnoughResourcesToPlan(any())).thenReturn(true);
        branchOffice3 = new BranchOffice("Branch office 3", "Locatie 3", projectRepository3, resourcePlanner3);

        // taak in branch office
        Project project = mock(Project.class);
        when(project.getBranchOffice()).thenReturn(branchOffice1);
        task = new Task("taak", Duration.ofHours(1), 0.1, mock(DependencyGraph.class), mock(IRequirementList.class), project);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(projectRepository1.getAllTasks()).thenReturn(tasks);
        when(projectRepository2.getAllTasks()).thenReturn(new ArrayList<>());
        when(projectRepository3.getAllTasks()).thenReturn(new ArrayList<>());
        task.setDelegatedTo(branchOffice1);

        projectManager = new ProjectManager("pm");
        developer1 = new Developer("dev1", resourceRepository1.getDeveloperType());
        developer2 = new Developer("dev2", resourceRepository1.getDeveloperType());
    }

    @Test
    public void delegateTask_ProperToOther() throws Exception {
        branchOffice1.delegateTask(task, branchOffice2);

        assertTrue(branchOffice1.getOwnTasks().contains(task));
        assertFalse(branchOffice1.getDelegatedTasks().contains(task));
        assertFalse(branchOffice1.getUnplannedTasks().contains(task));
        assertFalse(branchOffice2.getOwnTasks().contains(task));
        assertTrue(branchOffice2.getDelegatedTasks().contains(task));
        assertTrue(branchOffice2.getUnplannedTasks().contains(task));
        assertTrue(task.getDelegatedTo().equals(branchOffice2));
        assertTrue(task.isDelegated());
    }

    @Test
    public void delegateTask_OtherToProper() throws Exception {
        branchOffice1.delegateTask(task, branchOffice2);
        branchOffice2.delegateTask(task, branchOffice1);

        assertTrue(branchOffice1.getOwnTasks().contains(task));
        assertFalse(branchOffice1.getDelegatedTasks().contains(task));
        assertTrue(branchOffice1.getUnplannedTasks().contains(task));
        assertFalse(branchOffice2.getOwnTasks().contains(task));
        assertFalse(branchOffice2.getDelegatedTasks().contains(task));
        assertFalse(branchOffice2.getUnplannedTasks().contains(task));
    }

    @Test
    public void delegateTask_OtherToOther() throws Exception {
        branchOffice1.delegateTask(task, branchOffice2);
        branchOffice2.delegateTask(task, branchOffice3);

        assertFalse(branchOffice2.getOwnTasks().contains(task));
        assertFalse(branchOffice2.getDelegatedTasks().contains(task));
        assertFalse(branchOffice2.getUnplannedTasks().contains(task));
        assertFalse(branchOffice3.getOwnTasks().contains(task));
        assertTrue(branchOffice3.getDelegatedTasks().contains(task));
        assertTrue(branchOffice3.getUnplannedTasks().contains(task));
    }

    @Test (expected = IllegalArgumentException.class)
    public void delegateTask_CanNotPlanTask() throws Exception {
        when(resourcePlanner2.hasEnoughResourcesToPlan(task)).thenReturn(false);
        branchOffice1.delegateTask(task, branchOffice2);
    }

    @Test
    public void addEmployeeTest() {
        branchOffice1.addEmployee(projectManager);
        branchOffice1.addEmployee(developer1);
        branchOffice1.addEmployee(developer2);

        assertTrue(branchOffice1.getEmployees().size() == 3);
        assertTrue(branchOffice1.amountOfEmployees() == 3);
        assertTrue(branchOffice1.getDevelopers().size() == 2);
        assertTrue(branchOffice1.amountOfDevelopers() == 2);
        assertTrue(branchOffice1.amountOfProjectManagers() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void branchOfficeNameTest(){
        assertTrue(branchOffice1.getName().equals("Branch office 1"));
        branchOffice1.setName("test");
        assertTrue(branchOffice1.getName().equals("test"));
        branchOffice1.setName("");
        branchOffice1.setName(null);
    }

    @Test
    public void containsTaskTest() throws Exception{
        assertTrue(branchOffice1.containsTask(task));
        assertFalse(branchOffice1.containsTask(mock(Task.class)));
    }

    @Test
    public void getProjectsTest() {
        when(projectRepository1.getProjects()).thenReturn(mock(ImmutableList.class));

        branchOffice1.getProjects();
        verify(projectRepository1.getProjects());
    }

}
