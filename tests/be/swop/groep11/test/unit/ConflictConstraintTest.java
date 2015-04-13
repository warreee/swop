package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.resource.constraint.ConflictCon;
import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequirementConstraint;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 8/04/2015.
 */
public class ConflictConstraintTest{

    private ResourceTypeRepository repository;
    private IResourceType typeA;
    private IResourceType typeB;
    private IResourceType typeC;

    @Before
    public void setUp() throws Exception {
        this.repository = new ResourceTypeRepository();
        repository.addNewResourceType("testA");
        this.typeA = repository.getResourceTypeByName("testA");

        repository.addNewResourceType("testB");
        this.typeB = repository.getResourceTypeByName("testB");
        repository.withConflictConstraint(typeB,typeC);

        repository.addNewResourceType("testC");
        this.typeC = repository.getResourceTypeByName("testC");
    }

    @Test
    public void testIsSatisfied_True() throws Exception {
        RequirementListBuilder builder = new RequirementListBuilder();
        builder.addRequirement(typeA,1);
        RequirementListing list = builder.getRequirements();

        //TODO fix

        ConflictCon constraint = new ConflictCon(typeA,typeB);
        constraint.isSatisfied(list);



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