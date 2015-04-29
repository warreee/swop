package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.AResourceType;
import be.swop.groep11.main.resource.RequirementListBuilder;
import be.swop.groep11.main.resource.ResourceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ronald on 24/04/2015.
 */
public class RequirementListBuilderTest {

    private AResourceType typeA;
    private AResourceType typeB;
    private AResourceType typeC;
    private ResourceManager resourceManager;

    private RequirementListBuilder rlb;

    @Before
    public void setUp() throws Exception {
        this.resourceManager = new ResourceManager();
        resourceManager.addNewResourceType("A");
        resourceManager.addNewResourceType("B");
        resourceManager.addNewResourceType("C");
        typeA = resourceManager.getResourceTypeByName("A");
        typeB = resourceManager.getResourceTypeByName("B");
        typeC = resourceManager.getResourceTypeByName("C");

        //A requires B
        resourceManager.withRequirementConstraint(typeA, typeB);

        //B conflicts C
        resourceManager.withConflictConstraint(typeB, typeC);

        //C conflicts C
        resourceManager.withConflictConstraint(typeC, typeC);


        resourceManager.addResourceInstance(typeA,"a1");
        resourceManager.addResourceInstance(typeA,"a2");
        resourceManager.addResourceInstance(typeB,"b1");
        resourceManager.addResourceInstance(typeC,"c1");

        rlb = new RequirementListBuilder();
    }

    @Test
    public void AddRequirement_TypeWithRequiresConstraint_valid() throws Exception {
        rlb.addNewRequirement(typeA,2);
        assertTrue(rlb.getRequirements().containsRequirementFor(typeB));
    }
//TODO implement testen

    @Test
    public void testName() throws Exception {
        rlb.addNewRequirement(typeA,1);
        rlb.addNewRequirement(typeC,1);
        assertFalse(rlb.getRequirements().containsRequirementFor(typeC));

    }
}