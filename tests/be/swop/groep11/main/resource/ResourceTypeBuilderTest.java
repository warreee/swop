package be.swop.groep11.main.resource;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by robin on 22/05/15.
 */
public class ResourceTypeBuilderTest {

    ResourceTypeBuilder resourceTypeBuilder;

    @Before
    public void setUp() throws Exception{
        resourceTypeBuilder = new ResourceTypeBuilder();
    }

    @Test
    public void testSetName() throws Exception {
        resourceTypeBuilder.setName("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullName(){
        resourceTypeBuilder.setName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetemptyName(){
        resourceTypeBuilder.setName("");
    }

    @Test
    public void testSetAvailability() throws Exception {
        resourceTypeBuilder.setAvailability(new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(16, 0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAvailabilityNull() throws Exception {
        resourceTypeBuilder.setAvailability(null);
    }

    @Test
    public void testSetConflictingTypes() throws Exception {
        resourceTypeBuilder.setConflictingTypes(Arrays.asList(new ResourceType("test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetConflictingTypesNull() throws Exception {
        resourceTypeBuilder.setConflictingTypes(null);
    }

    @Test
    public void testSetRequireTypes() throws Exception {
        resourceTypeBuilder.setRequireTypes(Arrays.asList(new ResourceType("test")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetRequireTypesNull() throws Exception {
        resourceTypeBuilder.setRequireTypes(null);
    }

    @Test
    public void testGetResourceType() throws Exception {
        ResourceType A = new ResourceType("A");
        ResourceType B = new ResourceType("B");
        resourceTypeBuilder.setName("Test");
        resourceTypeBuilder.setConflictingTypes(Arrays.asList(A));
        resourceTypeBuilder.setRequireTypes(Arrays.asList(B));

        AResourceType result = resourceTypeBuilder.getResourceType();
        assertTrue(result.getTypeName().equals("Test"));
        assertTrue(result.amountOfConstraints() == 2);
        assertTrue(result.hasConstraintFor(A));
        assertTrue(result.hasConstraintFor(B));
    }
}