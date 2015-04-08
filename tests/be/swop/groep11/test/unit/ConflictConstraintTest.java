package be.swop.groep11.test.unit;

import be.swop.groep11.main.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 8/04/2015.
 */
public class ConflictConstraintTest {

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
    public void testConstructor_valid() throws Exception {
        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ConflictConstraint conflictConstraint = new ConflictConstraint(typeA,types);
        assertEquals(typeA, conflictConstraint.getOwnerType());
        assertTrue(conflictConstraint.getConstrainingTypes().equals(types));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_emptyList_invalid() throws Exception {
        ResourceTypeConstraint constraintB = new ConflictConstraint(typeA, new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullList_invalid() throws Exception {
        ResourceTypeConstraint constraintC = new ConflictConstraint(typeA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullOwnerType_invalid() throws Exception {
        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ResourceTypeConstraint constraintD = new ConflictConstraint(null,types);

    }


    @Test
    public void testIsSatisfied_True() throws Exception {
        List<ResourceTypeRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceTypeRequirement(typeA,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ConflictConstraint conflictConstraintA = new ConflictConstraint(typeB,types);

       assertTrue( conflictConstraintA.isSatisfied(requirements));//geen typeB in requirements
    }

    @Test
    public void testIsSatisfied_False() throws Exception {
        List<ResourceTypeRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceTypeRequirement(typeB,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ConflictConstraint conflictConstraintA = new ConflictConstraint(typeB,types);

        assertFalse(conflictConstraintA.isSatisfied(requirements));//geen typeB in requirements
    }

    @Test
    public void testResolve() throws Exception {
        List<ResourceTypeRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceTypeRequirement(typeB,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        types.add(typeA);

        ConflictConstraint conflictConstraint = new ConflictConstraint(typeB,types);
        List<ResourceTypeRequirement> result = conflictConstraint.resolve(requirements);
        assertFalse(result.contains(typeB));
    }

    @Test
    public void testIsValidOtherConstraint_requirementConstraint() throws Exception {
        List<ResourceType> typesC = new ArrayList<>();
        typesC.add(typeA);
        ConflictConstraint conflictConstraint = new ConflictConstraint(typeB,typesC);

        List<ResourceType> typesR = new ArrayList<>();
        typesR.add(typeB);
        RequirementConstraint requirementConstraint = new RequirementConstraint(typeA,typesR);

        assertFalse(conflictConstraint.isValidOtherConstraint(requirementConstraint));
    }

    @Test
    public void testIsValidOtherConstraint_conflictConstraint() throws Exception {
        List<ResourceType> typesC = new ArrayList<>();
        typesC.add(typeA);
        ConflictConstraint conflictConstraint = new ConflictConstraint(typeB,typesC);

        List<ResourceType> typesR = new ArrayList<>();
        typesR.add(typeB);
        ConflictConstraint conflictConstraintB = new ConflictConstraint(typeA,typesR);

        assertTrue(conflictConstraint.isValidOtherConstraint(conflictConstraintB));
    }
}