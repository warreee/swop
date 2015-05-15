package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.OldPlan;
import be.swop.groep11.main.resource.RequirementListBuilder;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;

/**
 * Testklasse voor de klasse Plan.
 */
public class PlanTest {
    private OldPlan plan1, plan2;
    private ResourceManager resourceManager;
    private LocalDateTime now;

    @Before
    public void setUp() {
        now = LocalDateTime.of(2015,1,1,11,0);
        resourceManager = new ResourceManager();
        addTempDomainObjects();
        RequirementListBuilder builder1 = new RequirementListBuilder();
        RequirementListBuilder builder2 = new RequirementListBuilder();
        builder1.addNewRequirement(resourceManager.getDeveloperType(), 4);
        builder2.addNewRequirement(resourceManager.getDeveloperType(), 4);
        Task task1 = new Task("test taak1", Duration.ofHours(7), 0.1, new SystemTime(), new DependencyGraph(), builder1.getRequirements(), mock(Project.class));
        Task task2 = new Task("test taak2", Duration.ofHours(7), 0.1, new SystemTime(), new DependencyGraph(), builder2.getRequirements(), mock(Project.class));
        plan1 = new OldPlan(task1, now, resourceManager);
        //plan1.applyReservations();
        plan2 = new OldPlan(task2, now, resourceManager);
        //plan2.applyReservations(); TODO
    }

    /**
     * Test die nagaat of dat er bij niet overlappende resources een equivalent plan bestaat
     * Dit moet zo zijn.
     */
    @Test
    public void hasEquivalentPlanTrueTest() {
        fail("Nog niet geimplenteerd");
    }

    /**
     * Test die nagaat of dat er bij overlappende resources een equivalent plan bestaat.
     * Dit kan niet zo zijn want de resources zijn al nodig voor de andere taak.
     */
    @Test
    public void hasEquivalentPlanFalseTest() {
        fail("Nog niet geimplenteerd");
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
