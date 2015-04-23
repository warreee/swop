package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.ResourceInstance;
import be.swop.groep11.main.resource.ResourceTypeBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ronald on 13/04/2015.
 */
public class ResourceTypeBuilderTest {
    //TODO test ResourceTypeBuilder, ResourceType,
    private ResourceTypeBuilder builderA;
    private ResourceTypeBuilder builderB;

    @Before
    public void setUp() throws Exception {
        this.builderA = new ResourceTypeBuilder("A");
        this.builderB = new ResourceTypeBuilder("B");
    }

    @Test
    public void testBasicResourceType() throws Exception {
        IResourceType typeA = builderA.getResourceType();

        assertEquals("A",typeA.getName());
        assertEquals(0, typeA.amountOfInstances());
        assertEquals(0, typeA.amountOfConstraints());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testName_Invalid() throws Exception {
        new ResourceTypeBuilder("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithDailyAvailability_invalid() throws Exception {
        builderA.withDailyAvailability(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithRequirementConstraint_invaid() throws Exception {
        builderA.withRequirementConstraint(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithConflictConstraint_invalid() throws Exception {
        builderA.withConflictConstraint(null);
    }

    @Test
    public void testWithDailyAvailability() throws Exception {
        LocalTime start = LocalTime.of(10, 10), end = LocalTime.of(15, 10);
        DailyAvailability d = new DailyAvailability(start,end);

        builderA.withDailyAvailability(start, end);
        builderB.withDailyAvailability(d);

        IResourceType typeA = builderA.getResourceType();
        IResourceType typeB = builderB.getResourceType();

        assertEquals(end,typeA.getDailyAvailability().getEndTime());
        assertEquals(start,typeA.getDailyAvailability().getStartTime());

        assertEquals(d.getEndTime(), typeB.getDailyAvailability().getEndTime());
        assertEquals(d.getStartTime(), typeB.getDailyAvailability().getStartTime());
    }

    @Test
    public void testWithRequirementConstraints_valid() throws Exception {
        IResourceType typeA = builderA.getResourceType();
        IResourceType typeB = builderB.getResourceType();

        builderA.withRequirementConstraint(typeB);
        assertTrue(typeA.hasConstraintFor(typeB));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithConstraints_invalid() throws Exception {
        IResourceType typeA = builderA.getResourceType();
        IResourceType typeB = builderB.getResourceType();

        builderA.withRequirementConstraint(typeB);
        builderB.withConflictConstraint(typeA);
    }

    @Test
    public void testSelfConflictingConstraint() throws Exception {
        builderA.setConflictWithSelf(true);

        IResourceType typeA = builderA.getResourceType();
        assertTrue(typeA.hasConstraintFor(typeA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelfRequireConstraint_invalid() throws Exception {
        IResourceType typeA = builderA.getResourceType();
        builderA.withRequirementConstraint(typeA);
    }

    @Test
    public void testAddInstance() throws Exception {
        builderA.addResourceInstance("in1");
        IResourceType typeA = builderA.getResourceType();
        assertEquals(1, typeA.getResourceInstances().size());
        ResourceInstance instance = typeA.getResourceInstances().get(0);
        assertEquals(typeA, instance.getResourceType());
        assertEquals("in1", instance.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddInstance_invalid() throws Exception {
        builderA.addResourceInstance("");
    }
}