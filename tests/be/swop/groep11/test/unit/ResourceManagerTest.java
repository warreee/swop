package be.swop.groep11.test.unit;

import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.ResourceManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by robin on 23/04/15.
 */
public class ResourceManagerTest {

    private ResourceManager resourceManager;

    @Before
    public void setUp() throws Exception{
        resourceManager = new ResourceManager();
    }

    @Test (expected = NoSuchElementException.class)
    public void emptyResourceManagerTest() throws Exception {
        assertTrue(resourceManager.getResourceTypes().isEmpty()); // Een lege ResourceManager zou geen types mogen bevatten. Ook niet het Developer type.
        assertFalse(resourceManager.containsType("Developer"));
        resourceManager.getResourceTypeByName("Developer");
    }

    @Test
    public void addResourceTypeToResourceManagerTest() throws Exception {
        // TODO: conflict met zichzelf
        addResourceTypeNameOnly("Test Resource ??n");
        assertEquals(1, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource ??n"); // Mag geen exception gooien

        addResourceTypeNameDailyAvailability("Test Resource twee", LocalTime.of(8, 0), LocalTime.of(16, 0));
        assertEquals(2, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource twee"); // Mag geen exception gooien

        addResourceTypeNameConflictRequires("Test Resource drie", new ArrayList<Integer>(Arrays.asList(0)), new ArrayList<Integer>(Arrays.asList(1)));
        assertEquals(3, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource drie"); // Mag geen exception gooien

        addResourceTypeNameDailyAvailabilityConflictRequires("Test Resource vier", LocalTime.of(8, 0), LocalTime.of(16, 0), new ArrayList<Integer>(Arrays.asList(0)), new ArrayList<Integer>(Arrays.asList(1)));
        assertEquals(4, resourceManager.getResourceTypes().size());
        resourceManager.getResourceTypeByName("Test Resource vier"); // Mag geen exception gooien
    }

    private void addResourceTypeNameOnly(String name){
        resourceManager.addNewResourceType(name);
    }

    private void addResourceTypeNameDailyAvailability(String name, LocalTime start, LocalTime end){
        resourceManager.addNewResourceType(name, new DailyAvailability(start, end));
    }

    private void addResourceTypeNameDailyAvailabilityConflictRequires(String name, LocalTime start, LocalTime end, List<Integer> req, List<Integer> con){
        ArrayList<IResourceType> required = new ArrayList<>();
        ArrayList<IResourceType> conflicting = new ArrayList<>();
        req.stream().forEach(x -> required.add(resourceManager.getResourceTypes().get(x)));
        con.stream().forEach(x -> conflicting.add(resourceManager.getResourceTypes().get(x)));
        resourceManager.addNewResourceType(name, new DailyAvailability(start, end), required, conflicting);
    }

    private void addResourceTypeNameConflictRequires(String name, List<Integer> req, List<Integer> con){
        ArrayList<IResourceType> required = new ArrayList<>();
        ArrayList<IResourceType> conflicting = new ArrayList<>();
        req.stream().forEach(x -> required.add(resourceManager.getResourceTypes().get(x)));
        con.stream().forEach(x -> conflicting.add(resourceManager.getResourceTypes().get(x)));
        resourceManager.addNewResourceType(name, required, conflicting);
    }
    }
}
