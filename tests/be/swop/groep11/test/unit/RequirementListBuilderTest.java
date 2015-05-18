package be.swop.groep11.test.unit;

import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.UnsatisfiableRequirementException;
import be.swop.groep11.main.resource.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by Ronald on 24/04/2015.
 */
public class RequirementListBuilderTest {

    private AResourceType typeA;
    private AResourceType typeB;
    private AResourceType typeC;
    private ResourceManager resourceManager;

    private RequirementListBuilder rlb;
    private ResourceRepository resourceRepository;

    @Before
    public void setUp() throws Exception {
        // TODO: we maken geen gebruik meer van ResourceManager.
        this.resourceManager = new ResourceManager();
        resourceManager.addNewResourceType("A");
        resourceManager.addNewResourceType("B");
        resourceManager.addNewResourceType("C");
        typeA = resourceManager.getResourceTypeByName("A");
        typeB = resourceManager.getResourceTypeByName("B");
        typeC = resourceManager.getResourceTypeByName("C");

        resourceManager.addResourceInstance(typeA,"a1");
        resourceManager.addResourceInstance(typeA,"a2");
        resourceManager.addResourceInstance(typeB,"b1");
        resourceManager.addResourceInstance(typeC,"c1");

        resourceRepository = mock(ResourceRepository.class);


        rlb = new RequirementListBuilder(resourceRepository);
    }

    @Test
    public void AddRequirement_TypeWithRequiresConstraint_valid() throws Exception {
        //A requires B
        resourceManager.withRequirementConstraint(typeA, typeB);

        rlb.addNewRequirement(typeA, 2);

        assertTrue(rlb.getRequirements().containsRequirementFor(typeB));
        assertEquals(2, rlb.getRequirements().getRequirementFor(typeA).getAmount());
    }

    @Test(expected = UnsatisfiableRequirementException.class)
    public void AddIllegalRequirement() throws Exception {
        //A requires B
        resourceManager.withRequirementConstraint(typeA, typeB);
        //B conflicts C
        resourceManager.withConflictConstraint(typeB, typeC);
        rlb.addNewRequirement(typeA, 1);
        rlb.addNewRequirement(typeC, 1);
    }

    @Test(expected = IllegalRequirementAmountException.class)
    public void selfConflictingType_addIllegalConstraint() throws Exception {
        //C conflicts C
        resourceManager.withConflictConstraint(typeC, typeC);
        rlb.addNewRequirement(typeC,2);
    }


    @Test
    public void recursiveRequirementsAddition() throws Exception {
        AResourceType D = addResourceTypeAndInstance("D");
        AResourceType E = addResourceTypeAndInstance("E");
        AResourceType F = addResourceTypeAndInstance("F");
        AResourceType G = addResourceTypeAndInstance("G");

        resourceManager.withRequirementConstraint(D,E);
        resourceManager.withRequirementConstraint(E,F);
        resourceManager.withRequirementConstraint(F,G);

        rlb.addNewRequirement(D, 1);
        assertTrue(rlb.getRequirements().containsRequirementFor(D));
        assertTrue(rlb.getRequirements().containsRequirementFor(E));
        assertTrue(rlb.getRequirements().containsRequirementFor(F));
        assertTrue(rlb.getRequirements().containsRequirementFor(G));
    }

    @Test(expected = UnsatisfiableRequirementException.class)
    public void illegalRecursiveRequirementsAddition() throws Exception {
        AResourceType D = addResourceTypeAndInstance("D");
        AResourceType E = addResourceTypeAndInstance("E");
        AResourceType F = addResourceTypeAndInstance("F");
        AResourceType G = addResourceTypeAndInstance("G");

        resourceManager.withRequirementConstraint(D, E);
        resourceManager.withRequirementConstraint(E, F);
        resourceManager.withRequirementConstraint(F, G);
        resourceManager.withConflictConstraint(E, G);

        rlb.addNewRequirement(D, 1);
    }

    @Test(expected = UnsatisfiableRequirementException.class)
    public void illegalRequiresAndConflictConstraintsOnSameTypes() throws Exception {
        resourceManager.withRequirementConstraint(typeA,typeB);
        resourceManager.withConflictConstraint(typeA,typeB);

    }

    private AResourceType addResourceTypeAndInstance(String name) {
        resourceManager.addNewResourceType(name);
        AResourceType type = resourceManager.getResourceTypeByName(name);
        resourceManager.addResourceInstance(type,"instance");
        return type;
    }
}