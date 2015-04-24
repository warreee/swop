package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.RequirementListBuilder;
import be.swop.groep11.main.resource.ResourceTypeBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 24/04/2015.
 */
public class RequirementListBuilderTest {

    private IResourceType typeA;
    private IResourceType typeB;
    private IResourceType typeC;
    private  RequirementListBuilder rlb;

    @Before
    public void setUp() throws Exception {
        ResourceTypeBuilder bA = new ResourceTypeBuilder("A");
        ResourceTypeBuilder bB = new ResourceTypeBuilder("B");
        ResourceTypeBuilder bC = new ResourceTypeBuilder("C");

        //A requires B
        bA.withRequirementConstraint(bB.getResourceType());

        //B conflicts C
        bB.withConflictConstraint(bC.getResourceType());

        //C conflicts C
        bC.withConflictConstraint(bC.getResourceType());

        bA.addResourceInstance("a1");
        bA.addResourceInstance("a2");
        bB.addResourceInstance("b1");
        bC.addResourceInstance("c1");

        rlb = new RequirementListBuilder();

        typeA = bA.getResourceType();
        typeB = bB.getResourceType();
        typeC = bC.getResourceType();
    }

    @Test
    public void AddRequirement_TypeWithRequiresConstraint_valid() throws Exception {
        rlb.addNewRequirement(typeA,2);
        assertTrue(rlb.getRequirements().containsRequirementFor(typeB));
    }


    @Test
    public void testName() throws Exception {
        rlb.addNewRequirement(typeA,1);
        rlb.addNewRequirement(typeC,1);
        assertFalse(rlb.getRequirements().containsRequirementFor(typeC));

    }
}