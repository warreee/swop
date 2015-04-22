package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.ResourceManager;
import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequiresConstraint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Ronald on 13/04/2015.
 */
public class ResourceTypeConstraintTest {

    private ResourceManager repository;
    private IResourceType typeA;
    private IResourceType typeB;
    private IResourceType typeC;

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
        ConflictConstraint constraint = new ConflictConstraint(typeA,null);
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
        RequiresConstraint constraint = new RequiresConstraint(typeA,null);
    }


    @Test
    public void testIsSatisfied_True() throws Exception {
        fail("Not implemented");


    }


    @Test
    public void testResolve() throws Exception {
        fail("Not implemented");
    }

    @Test
    public void testIsValidOtherConstraint_requirementConstraint() throws Exception {
        fail("Not implemented");
    }

    @Test
    public void testIsValidOtherConstraint_conflictConstraint() throws Exception {
        fail("Not implemented");
    }

}