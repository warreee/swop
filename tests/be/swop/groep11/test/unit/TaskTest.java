package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.resource.RequirementListBuilder;
import be.swop.groep11.main.resource.ResourceRepository;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskTest {

    private Task task1, task2, task3;
    private Project project;
    private SystemTime systemTime;
    private DependencyGraph dependencyGraph;
    private LocalDateTime now;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.systemTime = new SystemTime(now);

        //BrancOffice
        BranchOffice branchOffice = mock(BranchOffice.class);


        project = new Project("Test project", "Test beschrijving",
                LocalDateTime.of(2015, 3, 4, 8, 30), LocalDateTime.of(2015, 3, 4, 16, 0),
                systemTime, branchOffice);

        ResourceRepository resourceRepository = mock(ResourceRepository.class);

        project.addNewTask("Test taak 1", 0.1, Duration.ofHours(8), new RequirementListBuilder(resourceRepository).getRequirements());
        project.addNewTask("Test taak 2", 0, Duration.ofMinutes(120), mock(IRequirementList.class));
        project.addNewTask("Test taak 3", 0.2, Duration.ofHours(16), mock(IRequirementList.class));

        dependencyGraph = new DependencyGraph();

        task1 = project.getTasks().get(0);
        task2 = project.getTasks().get(1);
        task3 = project.getTasks().get(2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        when(branchOffice.getUnplannedTasks()).thenReturn(tasks);
        when(branchOffice.containsTask(any())).thenReturn(true);
        LocalDateTime startTime = LocalDateTime.of(2015, 1, 1, 0, 0);


        Plan testPlan = mock(Plan.class);
        when(testPlan.hasEquivalentPlan()).thenReturn(true);
        when(testPlan.isWithinPlanTimeSpan(any())).thenReturn(true);
        when(branchOffice.isTaskPlanned(eq(task1))).thenReturn(true);
        when(branchOffice.isTaskPlanned(eq(task2))).thenReturn(true);
        when(branchOffice.isTaskPlanned(eq(task3))).thenReturn(true);
        when(branchOffice.getPlanForTask(eq(task1))).thenReturn(testPlan);
        when(branchOffice.getPlanForTask(eq(task2))).thenReturn(testPlan);
        when(branchOffice.getPlanForTask(eq(task3))).thenReturn(testPlan);
        when(testPlan.hasEquivalentPlan()).thenReturn(true);
        when(testPlan.isWithinPlanTimeSpan(any())).thenReturn(true);

        TimeSpan timeSpan = new TimeSpan(startTime, startTime.plusHours(1));
        when(testPlan.getTimeSpan()).thenReturn(timeSpan);

        systemTime.addObserver(task1.getSystemTimeObserver());
        systemTime.addObserver(task2.getSystemTimeObserver());
        systemTime.addObserver(task3.getSystemTimeObserver());
        systemTime.updateSystemTime(systemTime.getCurrentSystemTime().plusDays(1));
    }
    //TODO review testen, wordt alles getest dat getest moet worden?
    /*
        Aanmaken van taak: geldige input controleren
     */
    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Null() throws Exception {
        Task invalidTask = new Task(null, Duration.ofHours(8), 0.1, dependencyGraph, mock(IRequirementList.class), mock(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDescription_Empty() throws Exception {
        Task invalidTask = new Task("", Duration.ofHours(8), 0.1, dependencyGraph, mock(IRequirementList.class), mock(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidDuration_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(-1), 0.1, dependencyGraph, mock(IRequirementList.class), mock(Project.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTask_InvalidAcceptableDeviation_Negative() throws Exception {
        Task invalidTask = new Task("Test taak", Duration.ofHours(8), -0.1, dependencyGraph, mock(IRequirementList.class), mock(Project.class));
    }

    /*
        Tests voor alternatieve taken
     */
    @Test
    public void SetAlternativeTask_valid() {
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.fail(now.plusDays(1));
        task1.setAlternativeTask(task2);
        assertTrue(task1.getAlternativeTask() == task2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void SetAlternativeTask_notFailed() throws Exception {
        task1.setAlternativeTask(task2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void SetAlternativeTask_sameTask() throws Exception {
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.fail(now.plusHours(1));
        task1.setAlternativeTask(task1);
    }

    @Test
    public void SetAlternativeTask_withDepedencyConstraints() throws Exception {
        task3.addNewDependencyConstraint(task1);
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.fail(now.plusDays(1));
        task1.setAlternativeTask(task2);
        assertTrue(task3.getDependingOnTasks().contains(task2));
        assertFalse(task3.getDependingOnTasks().contains(task1));
    }

    /*
        Tests voor status van geëindigde taak
     */
    @Test
    public void FinishedStatus_notFinished() throws Exception {
        assertEquals(Task.TaskEvaluation.NOTFINISHED.toString(), task1.getFinishedStatus(systemTime.getCurrentSystemTime()));
    }

    @Test
    public void FinishedStatus_early() throws Exception {
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.finish(LocalDateTime.of(2015,1,1,1,0).plusHours(2));
        assertEquals(Task.TaskEvaluation.EARLY.toString(), task1.getFinishedStatus(systemTime.getCurrentSystemTime()));

    }

    @Test
    public void FinishedStatus_onTime() throws Exception {
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.finish(LocalDateTime.of(2015,1,1,1,0).plusHours(8));
        assertEquals(Task.TaskEvaluation.ONTIME.toString(), task1.getFinishedStatus(systemTime.getCurrentSystemTime()));

    }

    @Test
    public void FinishedStatus_late() throws Exception {
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.finish(LocalDateTime.of(2015,1,1,1,0).plusHours(18));
        assertEquals(Task.TaskEvaluation.OVERDUE.toString(), task1.getFinishedStatus(systemTime.getCurrentSystemTime()));

    }

    /*
        Delay en duration
     */
    @Test
    public void Delay_EarlyFinishedTask() {
        task1.execute(LocalDateTime.of(2015,1,1,1,0));
        task1.finish(LocalDateTime.of(2015,1,1,1,0).plusHours(1));
        assertTrue(task1.getDelay().equals(Duration.ofDays(0)));
    }

    @Test
    public void Delay_FinishedAfterEstimatedDuration() {
        task1.execute(LocalDateTime.of(2015, 3, 8, 8, 0));
        task1.finish(LocalDateTime.of(2015, 3, 8, 16, 3));
        assertTrue(task1.getDelay().equals(Duration.ofSeconds(180)));
    }

    /**
     * getAlternativeStatus
     */
    @Test
    public void AlternativeStatus_NoAlternativeTask() {
        assertFalse(task1.getAlternativeFinished());
        task1.execute(LocalDateTime.of(2015, 3, 8, 10, 0));
        task1.finish(LocalDateTime.of(2015, 3, 8, 12, 0));
        assertTrue(task1.getAlternativeFinished());
    }

    @Test
    public void AlternativeStatus_AlternativeTask() throws Exception {
        task1.execute(LocalDateTime.of(2015, 3, 8, 10, 0));
        task1.fail(LocalDateTime.of(2015, 3, 8, 12, 0));
        task1.setAlternativeTask(task2);
        assertFalse(task1.getAlternativeFinished());
        task2.execute(LocalDateTime.of(2015, 3, 8, 10, 0));
        task2.finish(LocalDateTime.of(2015, 3, 8, 12, 0));
        assertTrue(task1.getAlternativeFinished());
    }

    @Test
    public void getOverTimePercentageTest() throws Exception {
        task1.execute(LocalDateTime.of(2015, 7, 8, 8, 0));
        assertEquals(task1.getOverTimePercentage(), 0.0, 1E-14);

        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 13, 0));
        assertEquals(task1.getOverTimePercentage(), 0.0, 1E-14);

        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 17, 0));
        assertEquals(task1.getOverTimePercentage(), 0.125, 1E-14);
    }

    @Test
    public void isOverTimeTest() throws Exception {

        task1.execute(LocalDateTime.of(2015, 7, 8, 8, 0));
        assertFalse(task1.isOverTime());

        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 16, 0));
        assertFalse(task1.isOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 18, 0));
        assertTrue(task1.isOverTime());
    }

    @Test
    public void isUnacceptablyOverTime() {
        task1.execute(LocalDateTime.of(2015, 7, 8, 8, 0));
        assertFalse(task1.isUnacceptablyOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 16, 0));
        assertFalse(task1.isUnacceptablyOverTime());
        systemTime.updateSystemTime(LocalDateTime.of(2015, 7, 8, 19, 0));
        assertTrue(task1.isUnacceptablyOverTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidDescriptionTest() {

        task1.setDescription(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void InvalidProjectTest() {

        Task testTask = new Task("Test taak 2", Duration.ofMinutes(120), 0, mock(DependencyGraph.class), mock(IRequirementList.class), null);

    }

    /**
     * Controleert ook de methode isDelegatedTo
     */
    @Test
    public void SetDelegatedToTest() {
        BranchOffice branchOffice = mock(BranchOffice.class);
        BranchOffice otherBranchOffice = mock(BranchOffice.class);
        Project project = mock(Project.class);
        when(project.getBranchOffice()).thenReturn(branchOffice);
        Task testTask = new Task("Test taak X", Duration.ofMinutes(120), 0, mock(DependencyGraph.class), mock(IRequirementList.class), project);
        ArrayList<Task> temp = new ArrayList<>();
        temp.add(testTask);
        when(otherBranchOffice.getUnplannedTasks()).thenReturn(temp);
        testTask.setDelegatedTo(otherBranchOffice);
        assertTrue(testTask.getDelegatedTo() == otherBranchOffice);
        assertTrue(testTask.isDelegated());
    }


    @Test(expected = IllegalArgumentException.class)
    public void InvalidSetDelegatedToTest1() {
        task1.setDelegatedTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void InvalidSetDelegatedToTest2() {
        task1.setDelegatedTo(mock(BranchOffice.class));
    }

    @Test
    public void getStatusStringTest() {
       assertEquals(task1.getStatusString(), "AVAILABLE");
    }

    @Test
    public void getDescirptionTest() {
        assertEquals(task1.getDescription(), "Test taak 1");
    }

    @Test
    public void getRequirementListTest() {
        assertTrue(task1.getRequirementList() != null);
    }
}