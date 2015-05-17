package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ronald on 12/05/2015.
 */
public class ResourceRepositoryTest {

    private ResourceRepository resRepo;
    private ResourceTypeRepository rtr;

    @Before
    public void setUp() throws Exception {
        rtr = new ResourceTypeRepository();
        resRepo = new ResourceRepository(rtr);
//        resRepo.setResourceTypeRepository(rtr);

    }

    @Test
    public void constructor_valid() throws Exception {
        ResourceRepository resRepo = new ResourceRepository(rtr);
        assertEquals(rtr, resRepo.getResourceTypeRepository());
        assertFalse(resRepo.canHaveAsResourceTypeRepository(rtr));
        assertFalse(resRepo.canHaveAsResourceTypeRepository(null));


    }
    @Test (expected = IllegalArgumentException.class)
    public void setResourceTypeRepository_invalid() throws Exception {
        ResourceRepository resRepo = new ResourceRepository(rtr);
        resRepo.setResourceTypeRepository(rtr);
        resRepo.setResourceTypeRepository(rtr);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setResourceTypeRepository_null_invalid() throws Exception {
        ResourceRepository resRepo = new ResourceRepository(rtr);
        resRepo.setResourceTypeRepository(null);
    }

    @Test
    public void addValidResourceInstance() throws Exception {
        rtr.addNewResourceType("T");
        AResourceType type = rtr.getResourceTypeByName("T");
        ResourceInstance r = new Resource("test",type);
        resRepo.addResourceInstance(r);
        assertEquals(r, resRepo.getResources(type).get(0));
        assertEquals(1, resRepo.amountOfResourceInstances(type));
        assertTrue(resRepo.containsResourceInstance(r));
    }

    @Test (expected = IllegalArgumentException.class)
    public void addInValidResourceInstance() throws Exception {
        AResourceType type = new ResourceType("testType");
        ResourceInstance r = new Resource("test", type);
        resRepo.addResourceInstance(r);
    }
}
