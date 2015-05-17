package be.swop.groep11.test.integration;

import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.PlanningController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Arne De Brabandere_3 on 30/04/2015.
 */
public class PlanTaskScenarioTest {

    private LocalDateTime now;
    private UserInterface mockedUI;
    private SystemTime systemTime;
    private ProjectRepository projectRepository;
    private ResourceManager resourceManager;
    private PlanningController planningController;
    private List<Project> projects;
    private List<Task> tasks;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.of(2015, 1, 1, 11, 0);
        this.mockedUI = mock(UserInterface.class);

        systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        projectRepository = new ProjectRepository(systemTime);
        resourceManager = new ResourceManager();

        addTempDomainObjects();

        projectRepository.addNewProject("Naam1", "Omschrijving1", now, now.plusDays(10));
        LogonController logonController = mock(LogonController.class);
        when(logonController.hasIdentifiedProjectManager()).thenReturn(true);

        this.planningController = new PlanningController(logonController,systemTime,mockedUI);

        this.projects = projectRepository.getProjects();

        RequirementListBuilder builder1 = new RequirementListBuilder();
        builder1.addNewRequirement(resourceManager.getResourceTypeByName("Auto"), 2);
        builder1.addNewRequirement(resourceManager.getResourceTypeByName("Koets"), 1);
        builder1.addNewRequirement(resourceManager.getDeveloperType(), 3);
        projects.get(0).addNewTask("Taak 1", 0.1, Duration.ofMinutes(120), builder1.getRequirements());

        RequirementListBuilder builder2 = new RequirementListBuilder();
        builder2.addNewRequirement(resourceManager.getResourceTypeByName("Koets"), 2);
        builder2.addNewRequirement(resourceManager.getDeveloperType(), 1);
        projects.get(0).addNewTask("Taak 2", 0.0, Duration.ofMinutes(60), builder2.getRequirements());

        this.tasks = projects.get(0).getTasks();
    }

    @Test
    public void PlanTask_NoConflictsTest() throws Exception {
        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
        // starttijd uit lijst selecteren:
        when(mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(true);
        // kies als starttijd de eerste in de lijst:
        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
            @Override
            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ((List<LocalDateTime>) args[0]).get(0);
            }
        });
        // reserveer de voorgestelde instanties:
        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
        // kies
        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
                gekozenDevelopers.add(dev1);
                gekozenDevelopers.add(dev2);
                gekozenDevelopers.add(dev3);
                return gekozenDevelopers;
            }
        });

        planningController.planTask();
    }

    @Test (expected = IllegalStateException.class)
    public void PlanTask_ConflictTest() throws Exception {
        OldPlan plan = resourceManager.getNextPlans(1, tasks.get(1), now).get(0);
//        tasks.get(1).plan(plan); TODO

        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
        // starttijd zelf kiezen:
        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(false);
        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
        // reserveer de voorgestelde instanties:
        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
        // kies developers
        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
                gekozenDevelopers.add(dev1);
                gekozenDevelopers.add(dev2);
                gekozenDevelopers.add(dev3);
                return gekozenDevelopers;
            }
        });

        planningController.planTask();
    }

    @Test
    public void PlanTask_ResolveConflict_MoveCurrentTaskTest() throws Exception {
        OldPlan plan = resourceManager.getNextPlans(1, tasks.get(1), now).get(0);
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(3));
        plan.addReservations(developers);
//        tasks.get(1).plan(plan); TODO

        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
        // starttijd zelf kiezen: maar de 2e keer niet zelf kiezen (bij resolve conflicts)
        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(false).thenReturn(true);
        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
        // kies als starttijd de eerste in de lijst (2de keer):
        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
            @Override
            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ((List<LocalDateTime>) args[0]).get(0);
            }
        });
        // reserveer de voorgestelde instanties:
        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
        // kies developers
        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
                gekozenDevelopers.add(dev1);
                gekozenDevelopers.add(dev2);
                gekozenDevelopers.add(dev3);
                return gekozenDevelopers;
            }
        });

        // resolve conflict
        when(mockedUI.requestBoolean(Matchers.contains("conflicterende"))).thenReturn(false);

        planningController.planTask();
    }

    @Test
    public void PlanTask_ResolveConflict_MoveOtherTasksTest() throws Exception {
        OldPlan plan = resourceManager.getNextPlans(1, tasks.get(1), now).get(0);
        List<ResourceInstance> developers = new ArrayList<>();
        developers.add(resourceManager.getDeveloperType().getResourceInstances().get(3));
        plan.addReservations(developers);
//        tasks.get(1).plan(plan); TODO

        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
        // starttijd zelf kiezen: maar de 2e keer niet zelf kiezen (bij resolve conflicts)
        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(false).thenReturn(true);
        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
        // kies als starttijd de eerste in de lijst (2de keer):
        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
            @Override
            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ((List<LocalDateTime>) args[0]).get(0);
            }
        });
        // reserveer de voorgestelde instanties:
        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(true);
        // kies developers
        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
                gekozenDevelopers.add(dev1);
                gekozenDevelopers.add(dev2);
                gekozenDevelopers.add(dev3);
                return gekozenDevelopers;
            }
        });

        // resolve conflict
        when(mockedUI.requestBoolean(Matchers.contains("conflicterende"))).thenReturn(true);

        planningController.planTask();
    }

    @Test
    public void PlanTask_ResolveConflict_SelectResoourcesTest() throws Exception {
        // selecteer de juiste taak:
        when(mockedUI.selectTaskFromList(anyObject())).thenReturn(tasks.get(0));
        // starttijd zelf kiezen:
        when (mockedUI.requestBoolean(Matchers.contains("starttijd hieruit selecteren"))).thenReturn(true);
        when(mockedUI.requestDatum(Matchers.contains("starttijd"))).thenReturn(now);
        // kies als starttijd de eerste in de lijst:
        when (mockedUI.selectLocalDateTimeFromList(anyObject())).thenAnswer(new Answer<LocalDateTime>() {
            @Override
            public LocalDateTime answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ((List<LocalDateTime>) args[0]).get(0);
            }
        });
        // reserveer zelf de resource instanties:
        when (mockedUI.requestBoolean(Matchers.contains("resource instanties reserveren"))).thenReturn(false);
        // kies resoruces
        when(mockedUI.selectMultipleFromList(Matchers.contains("instanties"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenInstanties = new ArrayList<ResourceInstance>();
                ResourceInstance inst1 = resourceManager.getResourceTypeByName("Auto").getResourceInstances().get(0);
                ResourceInstance inst2 = resourceManager.getResourceTypeByName("Auto").getResourceInstances().get(1);
                gekozenInstanties.add(inst1);
                gekozenInstanties.add(inst2);
                return gekozenInstanties;
            }
        }).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenInstanties = new ArrayList<ResourceInstance>();
                ResourceInstance inst1 = resourceManager.getResourceTypeByName("Koets").getResourceInstances().get(0);
                gekozenInstanties.add(inst1);
                return gekozenInstanties;
            }
        });
        // kies developers
        when(mockedUI.selectMultipleFromList(Matchers.contains("developers"), anyObject(), anyObject(), anyInt(), anyBoolean(), anyObject())).thenAnswer(new Answer<List<ResourceInstance>>() {
            @Override
            public List<ResourceInstance> answer(InvocationOnMock invocation) throws Throwable {
                List<ResourceInstance> gekozenDevelopers = new ArrayList<ResourceInstance>();
                ResourceInstance dev1 = resourceManager.getDeveloperType().getResourceInstances().get(0);
                ResourceInstance dev2 = resourceManager.getDeveloperType().getResourceInstances().get(1);
                ResourceInstance dev3 = resourceManager.getDeveloperType().getResourceInstances().get(2);
                gekozenDevelopers.add(dev1);
                gekozenDevelopers.add(dev2);
                gekozenDevelopers.add(dev3);
                return gekozenDevelopers;
            }
        });

        // resolve conflict
        when(mockedUI.requestBoolean(Matchers.contains("conflicterende"))).thenReturn(false);

        planningController.planTask();
    }

    private void addTempDomainObjects() {

        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter SWOP");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ward");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Ronald");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Robin");
        resourceManager.addResourceInstance(resourceManager.getDeveloperType(), "Kabouter Arne");

        resourceManager.addNewResourceType("Auto");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Aston Martin Rapide");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Toyota Auris");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Auto"), "Rolls Royce Phantom");

        resourceManager.addNewResourceType("Koets", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(14, 0)));
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 1");
        resourceManager.addResourceInstance(resourceManager.getResourceTypeByName("Koets"), "Koets 2");

    }

}
