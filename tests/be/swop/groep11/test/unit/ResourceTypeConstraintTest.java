package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequiresConstraint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 13/04/2015.
 */
public class ResourceTypeConstraintTest {

    private ResourceManager repository;
    private AResourceType typeA;
    private AResourceType typeB;
    private AResourceType typeC;

    @Before
    public void setUp() throws Exception {
        this.repository = new ResourceManager();
        repository.addNewResourceType("testA");
        repository.addNewResourceType("testB");
        repository.addNewResourceType("testC");

        this.typeA = repository.getResourceTypeByName("testA");
        this.typeB = repository.getResourceTypeByName("testB");
        this.typeC = repository.getResourceTypeByName("testC");
    }

    @Test
    public void testConflictConstructor_valid() throws Exception {
        ConflictConstraint constraint = new ConflictConstraint(typeA,typeB);

        assertEquals(typeA, constraint.getOwnerType());
        assertEquals(typeB, constraint.getConstrainingType());
        assertEquals(0, constraint.getMin());
        assertEquals(0,constraint.getMax());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConflictConstructor_invalid() throws Exception {
        new ConflictConstraint(typeA,null);
    }

    @Test
    public void testRequiresConstructor_valid() throws Exception {
        RequiresConstraint constraint = new RequiresConstraint(typeA,typeB);

        assertEquals(typeA, constraint.getOwnerType());
        assertEquals(typeB, constraint.getConstrainingType());
        assertEquals(1, constraint.getMin());
        assertEquals(Integer.MAX_VALUE,constraint.getMax());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiresConstructor_invalid() throws Exception {
        new RequiresConstraint(typeA,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresConstraintRequireSelfTest() throws Exception {
        new RequiresConstraint(typeA, typeA);
    }

    @Test
    public void isAcceptableAmountTest() throws Exception {
        ConflictConstraint con1 = new ConflictConstraint(typeA, typeB);
        assertTrue(con1.isAcceptableAmount(typeA, 10));

        ConflictConstraint con2 = new ConflictConstraint(typeC, typeC);
        assertFalse(con2.isAcceptableAmount(typeC, 2));
        assertTrue(con2.isAcceptableAmount(typeC, 1));
    }

    @Test
    public void conflictConstraintCyclicTest() throws Exception {
        fail("implement");
    }


}