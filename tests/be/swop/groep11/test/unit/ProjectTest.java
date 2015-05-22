package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.booleanThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ProjectTest {

    protected User user;
    protected ProjectRepository repository;
    protected LocalDateTime create;
    protected LocalDateTime due;
    protected String name;
    protected String description;
    protected Project project;
    protected SystemTime systemTime;
    protected IRequirementList emptyRequirementList;
    protected BranchOffice branchOffice;

    @Before
    public void setUp() throws Exception {
        user = new User("ROOT");

        create = LocalDateTime.of(2015, 1, 1, 0, 0);
        due = create.plusDays(2).plusHours(6);
        name = "name";
        description = "description";
        systemTime = new SystemTime(LocalDateTime.of(2014,12,31,16,0));
        branchOffice = mock(BranchOffice.class);
        when(branchOffice.containsTask(any())).thenReturn(true);
        makeAllTasksInBranchOfficeAvailable(branchOffice);
        repository = new ProjectRepository(systemTime);

        project = new Project(name, description, create, due, systemTime, mock(BranchOffice.class));

        emptyRequirementList = mock(IRequirementList.class);
        when(emptyRequirementList.iterator()).thenReturn(mock(Iterator.class));
    }

    protected void makeAllTasksInBranchOfficeAvailable(BranchOffice branchOffice) {
        // zorg dat elke taak een plan heeft
        Plan plan = mock(Plan.class);
        when(plan.hasEquivalentPlan()).thenReturn(true);
        when(plan.isWithinPlanTimeSpan(any())).thenReturn(true);
        when(branchOffice.getPlanForTask(any())).thenReturn(plan);
        when(branchOffice.isTaskPlanned(any())).thenReturn(true);
        // update de system time ==> dit zorgt dat elke taak geupdate wordt naar available
        systemTime.updateSystemTime(systemTime.getCurrentSystemTime().plusDays(1));
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
        new Project(name, description, LocalDateTime.now(), LocalDateTime.now(), systemTime, mock(BranchOffice.class));
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
    public void getFailedTasksTest() throws Exception{
        project.addNewTask("Taak", 0.1, Duration.ofHours(8), emptyRequirementList);
        project = spy(project);
        Task task1 = spy(project.getLastAddedTask());
        when(project.getTasks()).thenReturn(Arrays.asList(task1));
        assertTrue(project.getFailedTasks().size() == 0);
        when(task1.isFailed()).thenReturn(true);
        assertTrue(project.getFailedTasks().size() == 1);
        assertTrue(project.getFailedTasks().get(0).equals(task1));
    }

}