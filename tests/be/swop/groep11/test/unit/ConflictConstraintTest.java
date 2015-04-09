package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.ResourceType;
import be.swop.groep11.main.resource.ResourceRequirement;
import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequirementConstraint;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ronald on 8/04/2015.
 */
public class ConflictConstraintTest extends ResourceTypeConstraintTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
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
        ResourceTypeConstraint constraint = new ConflictConstraint(typeA, new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullList_invalid() throws Exception {
        ResourceTypeConstraint constraint = new ConflictConstraint(typeA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullOwnerType_invalid() throws Exception {
        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ResourceTypeConstraint constraint =new ConflictConstraint(null,types);

    }

    @Test
    public void testIsSatisfied_True() throws Exception {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(typeA,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ConflictConstraint conflictConstraintA = new ConflictConstraint(typeB,types);

        assertTrue( conflictConstraintA.isSatisfied(requirements));//geen typeB in requirements
    }

    @Test
    public void testIsSatisfied_False() throws Exception {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(typeB,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ConflictConstraint conflictConstraintA = new ConflictConstraint(typeB,types);

        assertFalse(conflictConstraintA.isSatisfied(requirements));//geen typeB in requirements
    }

    @Test
    public void testResolve() throws Exception {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(typeB,1));
        requirements.add(new ResourceRequirement(typeA,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        types.add(typeA);
        //Requirements voor TypeA als TypeB mogen reeds niet aanwezig zijn
        ConflictConstraint conflictConstraint = new ConflictConstraint(typeB,types);
        List<ResourceRequirement> result = conflictConstraint.resolve(requirements);
        System.out.println(result.toString());
        boolean contains = (result.size() == 0)? false:true;
        boolean temp = true;
        for(ResourceRequirement req : result){
            temp = req.getType().equals(typeB);
            contains = (temp == false)? false: contains;
        }
        assertFalse(contains);
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