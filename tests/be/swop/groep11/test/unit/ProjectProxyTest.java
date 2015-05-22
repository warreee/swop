package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ProjectProxyTest extends ProjectTest {

    @Before
    public void setUpProxy(){
        project = new ProjectProxy(project);
    }

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
        assertFalse(proxy.equals("Random string"));
        assertFalse(proxy.equals(null));
    }

    @Test
    public void initMultipleProxyObjectsTest() throws Exception{
        ProjectProxy projectProxy1 = new ProjectProxy(project);
        ProjectProxy projectProxy2 = new ProjectProxy(projectProxy1);
    }

    //TODO change project status

    @Test
    public void AddNewTask_valid() {
        project.addNewTask("Test taak", 0.1, Duration.ofHours(2), mock(IRequirementList.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void AddNewTask_invalid1() {
        project.addNewTask("", 0.1, Duration.ofHours(2), mock(IRequirementList.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void AddNewTask_invalid2() {
        project.addNewTask("Test", -0.1, Duration.ofHours(2), mock(IRequirementList.class));
    }

    @Test(expected=IllegalArgumentException.class)
    public void AddNewTask_invalid3() {
        project.addNewTask("Test", 0.1, null, mock(IRequirementList.class));
    }

    @Test
    public void isOverTime_OverTimeProject() {
        Project project1 = new Project(name,description,LocalDateTime.of(2015,3,8,9,32),LocalDateTime.of(2015,3,13,18,0), systemTime,branchOffice);
        project1.addNewTask("Taak",0.1,Duration.ofHours(24), emptyRequirementList);
        project1.addNewTask("Afhankelijke taak", 0, Duration.ofHours(16), emptyRequirementList);
        makeAllTasksInBranchOfficeAvailable(branchOffice);
        project1.getTasks().get(1).addNewDependencyConstraint(project1.getTasks().get(0));
        project1.getTasks().get(0).execute(LocalDateTime.of(2015, 3, 14, 18, 0));
        System.out.println(project1.getEstimatedEndTime());
        assertTrue(project1.isOverTime());
    }

    @Test
    public void isOverTime_NotOverTimeProject() {
        Project project1 = new Project(name,description,LocalDateTime.of(2015,3,8,9,32),LocalDateTime.of(2015,3,13,18,0), systemTime, branchOffice);
        project1.addNewTask("Taak", 0.1, Duration.ofHours(8), emptyRequirementList);
        project1.addNewTask("Afhankelijke taak", 0, Duration.ofHours(16), emptyRequirementList);
        makeAllTasksInBranchOfficeAvailable(branchOffice);
        project1.getTasks().get(1).addNewDependencyConstraint(project1.getTasks().get(0));
        project1.getTasks().get(0).execute(LocalDateTime.of(2015, 3, 8, 0, 0));
        assertFalse(project1.isOverTime());
    }

    @Test
    public void NewProject_valid() throws Exception {
        new Project(name, description, create,due, systemTime, mock(BranchOffice.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void NewProject_invalid_Name() throws Exception {
        new Project("", description, create,due, systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_NameNull() throws Exception {
        new Project(null,description, create,due, systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_Description() throws Exception {
        new Project(name, "", create, due, systemTime, mock(BranchOffice.class));
    }
    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DescriptionNull() throws Exception {
        new Project(name, null, create,due, systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationTimeAfterDueTime() throws Exception {
        new Project(name, description, LocalDateTime.now().plusSeconds(3600),LocalDateTime.now(), systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationEqualDue() throws Exception {
        new Project(name, description, create, create, systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DueTimeBeforeCreationTime() throws Exception {
        //DueTime mag niet voor creation time zijn.
        new Project(name, description, LocalDateTime.now(),LocalDateTime.now().minusSeconds(60), systemTime, mock(BranchOffice.class));
    }
    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationAndDueNull() throws Exception {
        new  Project(name, description, null,null, systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationTimeNull() throws Exception {
        new Project(name, description, null,due, systemTime, mock(BranchOffice.class));
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DueTimeNull() throws Exception {
        //DueTime mag niet null zijn
        new Project(name, description, create,null, systemTime, mock(BranchOffice.class));
    }

    @Test
    public void gettersAndSettersTest() throws Exception {
        project.setProjectName("test");
        assertTrue(project.getName().equals("test"));

        project.setDescription("Omschrijving");
        assertTrue(project.getDescription().equals("Omschrijving"));

        assertTrue(project.getCreationTime().equals(create));
        assertTrue(project.getDueTime().equals(due));
        assertTrue(project.getProjectStatus() == ProjectStatus.ONGOING);
        assertTrue(project.getTasks().size() == 0);
        assertFalse(project.isOverTime());
        assertTrue(project.getEstimatedEndTime() != null);
    }


    @Test
    public void getFailedTasksTest() throws Exception{
        Project project1 = new Project(name,description,LocalDateTime.of(2015,3,8,9,32),LocalDateTime.of(2015,3,13,18,0), systemTime,branchOffice);
        project1 = new ProjectProxy(project1);
        project1.addNewTask("Taak",0.1,Duration.ofHours(24), emptyRequirementList);
        Task task1 = project1.getLastAddedTask();
        project1.addNewTask("taak", 0, Duration.ofHours(16), emptyRequirementList);
        Task task2 = project1.getLastAddedTask();
        makeAllTasksInBranchOfficeAvailable(branchOffice);
        assertTrue(project1.getFailedTasks().size() == 0);

        task1.execute(systemTime.getCurrentSystemTime().plusDays(3));
        task1.fail(systemTime.getCurrentSystemTime().plusDays(3).plusHours(1));

        task2.execute(systemTime.getCurrentSystemTime().plusDays(3));
        task2.fail(systemTime.getCurrentSystemTime().plusDays(3).plusHours(1));
        assertTrue(project1.getFailedTasks().size() == 2);
        assertTrue(project1.getFailedTasks().contains(task1));
        assertTrue(project1.getFailedTasks().contains(task2));
    }

}
