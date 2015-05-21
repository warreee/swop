package be.swop.groep11.test.unit;

import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.UnsatisfiableRequirementException;
import be.swop.groep11.main.resource.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ronald on 24/04/2015.
 */
public class RequirementListBuilderTest {

    private AResourceType typeA;
    private AResourceType typeB;
    private AResourceType typeC;

    private RequirementListBuilder rlb;
    private ResourceRepository resourceRepository;
    private ResourceTypeRepository resourceTypeRepository;

    @Before
    public void setUp() throws Exception {
        resourceTypeRepository = new ResourceTypeRepository();
        resourceTypeRepository.addNewResourceType("A");
        resourceTypeRepository.addNewResourceType("B");
        resourceTypeRepository.addNewResourceType("C");
        typeA = resourceTypeRepository.getResourceTypeByName("A");
        typeB = resourceTypeRepository.getResourceTypeByName("B");
        typeC = resourceTypeRepository.getResourceTypeByName("C");

        resourceRepository = new ResourceRepository(resourceTypeRepository);
        resourceRepository.addResourceInstance(new Resource("a1", typeA));
        resourceRepository.addResourceInstance(new Resource("a2", typeA));
        resourceRepository.addResourceInstance(new Resource("b1", typeB));
        resourceRepository.addResourceInstance(new Resource("c1", typeC));

        rlb = new RequirementListBuilder(resourceRepository);
    }

    @Test
    public void AddRequirement_TypeWithRequiresConstraint_valid() throws Exception {
        //A requires B
        resourceTypeRepository.withRequirementConstraint(typeA, typeB);

        rlb.addNewRequirement(typeA, 2);

        assertTrue(rlb.getRequirements().containsRequirementFor(typeB));
        assertEquals(2, rlb.getRequirements().getRequirementFor(typeA).getAmount());
    }

    @Test(expected = UnsatisfiableRequirementException.class)
    public void AddIllegalRequirement() throws Exception {
        //A requires B
        resourceTypeRepository.withRequirementConstraint(typeA, typeB);
        //B conflicts C
        resourceTypeRepository.withConflictConstraint(typeB, typeC);
        rlb.addNewRequirement(typeA, 1);
        rlb.addNewRequirement(typeC, 1);
    }

    @Test(expected = IllegalRequirementAmountException.class)
    public void selfConflictingType_addIllegalConstraint() throws Exception {
        //C conflicts C
        resourceTypeRepository.withConflictConstraint(typeC, typeC);
        rlb.addNewRequirement(typeC,2);
    }


    @Test
    public void recursiveRequirementsAddition() throws Exception {
        AResourceType D = addResourceTypeAndInstance("D");
        AResourceType E = addResourceTypeAndInstance("E");
        AResourceType F = addResourceTypeAndInstance("F");
        AResourceType G = addResourceTypeAndInstance("G");

        resourceTypeRepository.withRequirementConstraint(D,E);
        resourceTypeRepository.withRequirementConstraint(E,F);
        resourceTypeRepository.withRequirementConstraint(F,G);

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

        resourceTypeRepository.withRequirementConstraint(D, E);
        resourceTypeRepository.withRequirementConstraint(E, F);
        resourceTypeRepository.withRequirementConstraint(F, G);
        resourceTypeRepository.withConflictConstraint(E, G);

        rlb.addNewRequirement(D, 1);
    }

    @Test(expected = UnsatisfiableRequirementException.class)
    public void illegalRequiresAndConflictConstraintsOnSameTypes() throws Exception {
        resourceTypeRepository.withRequirementConstraint(typeA,typeB);
        resourceTypeRepository.withConflictConstraint(typeA,typeB);

    }

    @Test
    public void testGetNextPossibleStartTime() throws Exception{
        resourceTypeRepository.addNewResourceType("DA 1", new DailyAvailability(LocalTime.of(10, 0), LocalTime.of(16, 0)));
        resourceTypeRepository.addNewResourceType("DA 2", new DailyAvailability(LocalTime.of(12, 0), LocalTime.of(17, 0)));

        resourceRepository.addResourceInstance(new Resource("IDA 1", resourceTypeRepository.getResourceTypeByName("DA 1")));
        resourceRepository.addResourceInstance(new Resource("IDA 2", resourceTypeRepository.getResourceTypeByName("DA 2")));

        rlb.addNewRequirement(resourceTypeRepository.getResourceTypeByName("DA 1"), 1);
        rlb.addNewRequirement(resourceTypeRepository.getResourceTypeByName("DA 2"), 1);

        IRequirementList requirementList = rlb.getRequirements();

        LocalDateTime next = requirementList.calculateNextPossibleStartTime(LocalDateTime.of(2015, 4, 10, 8, 0));
        assertTrue(next.equals(LocalDateTime.of(2015, 4, 10, 12, 0)));

        next = requirementList.calculateNextPossibleStartTime(LocalDateTime.of(2015, 4, 10, 16, 0));
        assertTrue(next.equals(LocalDateTime.of(2015, 4, 11, 12, 0)));

        next = requirementList.calculateNextPossibleStartTime(LocalDateTime.of(2015, 4, 10, 12, 0));
        assertTrue(next.equals(LocalDateTime.of(2015, 4, 10, 12, 0)));

        next = requirementList.calculateNextPossibleStartTime(LocalDateTime.of(2015, 4, 10, 14, 0));
        assertTrue(next.equals(LocalDateTime.of(2015, 4, 10, 14, 0)));
    }

    private AResourceType addResourceTypeAndInstance(String name) {
        resourceTypeRepository.addNewResourceType(name);
        AResourceType type = resourceTypeRepository.getResourceTypeByName(name);
        resourceRepository.addResourceInstance(new Resource("instance", type));
        return type;
    }
}