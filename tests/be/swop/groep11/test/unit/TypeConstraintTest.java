package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.ResourceType;
import be.swop.groep11.main.resource.ResourceTypeRepository;
import be.swop.groep11.main.resource.constraint.ConflictCon;
import be.swop.groep11.main.resource.constraint.RequiresCon;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 13/04/2015.
 */
public class TypeConstraintTest {

    private ResourceTypeRepository repository;
    private ResourceType typeA;
    private ResourceType typeB;

    @Before
    public void setUp() throws Exception {
        this.repository = new ResourceTypeRepository();
        repository.addResourceType("testA");
        this.typeA = repository.getResourceTypeByName("testA");


        repository.addResourceType("testB");
        this.typeB = repository.getResourceTypeByName("testB");
    }

    @Test
    public void testConflictConstructor_valid() throws Exception {
        ConflictCon constraint = new ConflictCon(typeA,typeB);

        assertEquals(typeA, constraint.getOwnerType());
        assertEquals(typeB, constraint.getConstrainingType());
        assertEquals(0, constraint.getMin());
        assertEquals(0,constraint.getMax());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConflictConstructor_invalid() throws Exception {
        ConflictCon constraint = new ConflictCon(typeA,null);
    }

    @Test
    public void testRequiresConstructor_valid() throws Exception {
        RequiresCon constraint = new RequiresCon(typeA,typeB);

        assertEquals(typeA, constraint.getOwnerType());
        assertEquals(typeB, constraint.getConstrainingType());
        assertEquals(1, constraint.getMin());
        assertEquals(Integer.MAX_VALUE,constraint.getMax());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiresConstructor_invalid() throws Exception {
        RequiresCon constraint = new RequiresCon(typeA,null);
    }


}