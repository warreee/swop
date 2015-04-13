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

import static org.junit.Assert.*;

/**
 * Created by Ronald on 8/04/2015.
 */
public class RequirementConstraintTest extends ResourceTypeConstraintTest {

    //TODO nieuwe constraints

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testConstructor_valid() throws Exception {
        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ResourceTypeConstraint requirementConstraint = new RequirementConstraint(typeA,types);
        assertEquals(typeA, requirementConstraint.getOwnerType());
        assertTrue(requirementConstraint.getConstrainingTypes().equals(types));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_emptyList_invalid() throws Exception {
        ResourceTypeConstraint constraint = new RequirementConstraint(typeA, new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullList_invalid() throws Exception {
        ResourceTypeConstraint constraint = new RequirementConstraint(typeA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullOwnerType_invalid() throws Exception {
        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ResourceTypeConstraint constraint =new RequirementConstraint(null,types);

    }



    @Test
    public void testIsSatisfied_True() throws Exception {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(typeA,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeA);
        RequirementConstraint constraint = new RequirementConstraint(typeB,types);

        assertTrue( constraint.isSatisfied(requirements));//Een Requirement voor typeA reeds aanwezig in de lijst.
    }



    @Test
    public void testIsSatisfied_False() throws Exception {
        List<ResourceRequirement> requirements = new ArrayList<>();
        requirements.add(new ResourceRequirement(typeB,1));

        List<ResourceType> types = new ArrayList<>();
        types.add(typeA);
        RequirementConstraint constraint = new RequirementConstraint(typeB,types);

        assertFalse(constraint.isSatisfied(requirements));//Een Requirement voor typeA is niet aanwezig in de lijst.
    }

    //TODO fix onderstaande

    @Test
    public void testResolve() throws Exception {
//        fail("Not implemented");
        List<ResourceRequirement> requirements = new ArrayList<>(); //Leeg

        List<ResourceType> types = new ArrayList<>();
        types.add(typeA);

        RequirementConstraint constraint = new RequirementConstraint(typeB,types); // typeA heeft typeB nodig
        List<ResourceRequirement> result = constraint.resolve(requirements);
        boolean contains = false;
        boolean temp = false;
        for(ResourceRequirement req : result){
            temp = req.getType().equals(typeA);
            contains = (temp == true)? true: contains;
        }
        assertTrue(contains);
    }

    @Test
    public void testIsValidOtherConstraint_requirementConstraint() throws Exception {
        fail("Not implemented");



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
        fail("Not implemented");

        List<ResourceType> typesC = new ArrayList<>();
        typesC.add(typeA);
        ConflictConstraint conflictConstraint = new ConflictConstraint(typeB,typesC);

        List<ResourceType> typesR = new ArrayList<>();
        typesR.add(typeB);
        ConflictConstraint conflictConstraintB = new ConflictConstraint(typeA,typesR);

        assertTrue(conflictConstraint.isValidOtherConstraint(conflictConstraintB));
    }
}