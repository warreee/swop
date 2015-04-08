package be.swop.groep11.test.unit;

import be.swop.groep11.main.ConflictConstraint;
import be.swop.groep11.main.ResourceType;
import be.swop.groep11.main.ResourceTypeRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void testCanHaveAsConstrainingTypes_valid() throws Exception {
        List<ResourceType> types = new ArrayList<>();
        types.add(typeB);
        ConflictConstraint conflictConstraint = new ConflictConstraint(typeA,types );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCanHaveAsConstrainingTypes_invalid() throws Exception {
        List<ResourceType> types = new ArrayList<>();

        ConflictConstraint conflictConstraint = new ConflictConstraint(typeA,types );
    }

    @Test
    public void testIsSatisfied() throws Exception {

    }

    @Test
    public void testResolve() throws Exception {

    }

    @Test
    public void testIsValidOtherConstraint() throws Exception {

    }
}