package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.ResourceType;
import be.swop.groep11.main.resource.ResourceTypeRepository;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by Ronald on 9/04/2015.
 */
public abstract class ResourceTypeConstraintTest {
    ResourceTypeRepository repository;
    ResourceType typeA;
    ResourceType typeB;

    @Before
    public void setUp() throws Exception {
        this.repository = new ResourceTypeRepository();
        repository.addResourceType("testA");
        this.typeA = repository.getResourceTypeByName("testA");
        repository.addResourceType("testB");
        this.typeB = repository.getResourceTypeByName("testB");
    }

    @Test
    public abstract void testConstructor_valid() throws Exception;

    @Test(expected = IllegalArgumentException.class)
    public abstract void testConstructor_emptyList_invalid() throws Exception ;

    @Test(expected = IllegalArgumentException.class)
    public abstract void testConstructor_nullList_invalid() throws Exception ;

    @Test(expected = IllegalArgumentException.class)
    public abstract void testConstructor_nullOwnerType_invalid() throws Exception ;

    @Test
    public abstract void testIsSatisfied_True() throws Exception ;

    @Test
    public abstract void testIsSatisfied_False() throws Exception ;

    @Test
    public abstract void testResolve() throws Exception ;

    @Test
    public abstract void testIsValidOtherConstraint_requirementConstraint() throws Exception;

    @Test
    public abstract void testIsValidOtherConstraint_conflictConstraint() throws Exception;
}
