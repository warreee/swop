package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectRepositoryTest {

    private BranchOffice branchOffice;
    private ProjectRepository projRep;

    private LocalDateTime create;
    private LocalDateTime due;

    private String name;
    private String description;

    private SystemTime systemTime;

    @Before
    public void setUp() throws Exception {
        systemTime = new SystemTime(LocalDateTime.of(2015,1,1,8,0));

        // mock branch office waarin elke taak available is
        branchOffice = mock(BranchOffice.class);
        makeAllTasksInBranchOfficeAvailable(branchOffice);

        projRep = new ProjectRepository(systemTime);
        projRep.setBranchOffice(branchOffice);
        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";
    }

    private void makeAllTasksInBranchOfficeAvailable(BranchOffice branchOffice) {
        // zorg dat elke taak een plan heeft
        Plan plan = mock(Plan.class);
        when(plan.hasEquivalentPlan()).thenReturn(true);
        when(plan.isWithinPlanTimeSpan(any())).thenReturn(true);
        when(branchOffice.getPlanForTask(any())).thenReturn(plan);
        when(branchOffice.isTaskPlanned(any())).thenReturn(true);
        // update de system time ==> dit zorgt dat elke taak geupdate wordt naar available
        systemTime.updateSystemTime(systemTime.getCurrentSystemTime().plusDays(1));
    }

    @Test
    public void GetProjectsList() throws Exception {
        projRep.addNewProject(name, description, create,due);
        projRep.addNewProject(name, description, create,due);
        projRep.addNewProject(name, description, create,due);

        assertEquals(3, projRep.getProjects().size());
    }


    @Test
    public void AddNewProjectValidTest() throws Exception {

        projRep.addNewProject(name, description, create, due);
        Project proj = projRep.getProjects().get(projRep.getProjects().size()-1);

        assertEquals(create, proj.getCreationTime());
        assertEquals(due, proj.getDueTime());
        assertEquals(name, proj.getName());
        assertEquals(description, proj.getDescription());
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidTimesTest() throws Exception {
        projRep.addNewProject(name, description, due, create);
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidNameTest() throws Exception {
        projRep.addNewProject(null, description, create, due);
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidDescriptionTest() throws Exception {
        projRep.addNewProject(name, null, create, due);
    }

    @Test
    public void getAvailableTasksTest() {
        //TODO fix test, dit is verkeerd.
        projRep.addNewProject("project 1", description, create, due);
        projRep.addNewProject("project 2", description, create, due);
        Project proj1 = projRep.getProjects().get(0);
        Project proj2 = projRep.getProjects().get(1);
        proj1.addNewTask("taak 1", 0.1, Duration.ofHours(8), mock(IRequirementList.class));
        proj2.addNewTask("taak 2", 0.0, Duration.ofMinutes(30), mock(IRequirementList.class));
        proj2.addNewTask("taak 3", 0.2, Duration.ofHours(100), mock(IRequirementList.class));
        makeAllTasksInBranchOfficeAvailable(projRep.getBranchOffice());
        proj2.getTasks().get(0).execute(LocalDateTime.now());
        ImmutableList<Task> availableTasks = projRep.getAllAvailableTasks();
        assertTrue(availableTasks.size() == 2);
        assertTrue(availableTasks.contains(proj1.getTasks().get(0)));
        assertTrue(availableTasks.contains(proj2.getTasks().get(1)));
    }

    @Test
    public void Memento_Test() throws Exception {
        projRep.addNewProject("project 1", description, create, due);
        projRep.addNewProject("project 2", description, create, due);
        Project proj1 = projRep.getProjects().get(0);
        Project proj2 = projRep.getProjects().get(1);
        proj1.addNewTask("taak 1", 0.1, Duration.ofHours(8), mock(IRequirementList.class));
        proj2.addNewTask("taak 2", 0.0, Duration.ofMinutes(30), mock(IRequirementList.class));
        proj2.addNewTask("taak 3", 0.2, Duration.ofHours(100), mock(IRequirementList.class));
        IProjectRepositoryMemento memento = projRep.createMemento();

        BranchOffice branchOffice = mock(BranchOffice.class);
        ProjectRepository projRep2 = new ProjectRepository(new SystemTime());
        projRep2.setBranchOffice(branchOffice);
        projRep2.setMemento(memento);
        assertTrue(repositoryContainsProject(projRep2, "project 1"));
        assertTrue(repositoryContainsProject(projRep2, "project 2"));
        assertTrue(repositoryContainsTask(projRep2, "project 1", "taak 1"));
        assertTrue(repositoryContainsTask(projRep2, "project 2", "taak 2"));
        assertTrue(repositoryContainsTask(projRep2, "project 2", "taak 3"));
    }

    private boolean repositoryContainsProject(ProjectRepository projectRepository, String projectName) {
        for (Project project : projectRepository.getProjects()) {
            if (project.getName().equals(projectName)) {
                return true;
            }
        }
        return false;
    }

    private boolean repositoryContainsTask(ProjectRepository projectRepository, String projectName, String taskDescription) {
        if (! repositoryContainsProject(projectRepository, projectName)) {
            return false;
        }
        for (Project project : projectRepository.getProjects()) {
            if (project.getName().equals(projectName)) {
                for (Task task : project.getTasks()) {
                    if (task.getDescription().equals(taskDescription)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


   //SEE ProjectTest voor alle unit test voor het aanmaken van een project.
}