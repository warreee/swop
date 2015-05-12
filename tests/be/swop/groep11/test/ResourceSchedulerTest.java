package be.swop.groep11.test;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.ConflictException;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Created by Ronald on 12/05/2015.
 */
public class ResourceSchedulerTest {

    private Task task;
    private ResourceScheduler resourceScheduler;

    @Before
    public void setUp() throws Exception {
        SystemTime systemTime = new SystemTime();
        IRequirementList requirementList = mock(IRequirementList.class);

        task = new Task("test", Duration.ofMinutes(100), 10,systemTime,new DependencyGraph(),requirementList);

        resourceScheduler = new ResourceScheduler();
    }



    @Test
    public void test() throws Exception {

        List<ResourceReservation> reservationList = resourceScheduler.getReservations(task);


        AResourceType type = new ResourceType("TestType");
        TimeSpan ts = new TimeSpan(LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        List<ResourceInstance> resourceList = resourceScheduler.getAvailableResources(type, ts);

        List<ResourceRequirement> resourceRequirements = new ArrayList<>();
        ResourceRequirement req = new ResourceRequirement(type, 3);

        List<ResourceInstance> resourceListB = resourceScheduler.getAvailableResources(resourceRequirements, task.getEstimatedDuration());

        List<LocalDateTime> startTimes = resourceScheduler.getNextStartTimes(task.getRequirementList(), task.getEstimatedDuration(), 3);


        List<ResourceInstance> selectedResources = new ArrayList<>();

//        resourceScheduler.makeReservations(task, selectedResources, selectedDevelopers, ts);

        resourceScheduler.canMakeReservations(task, selectedResources, ts);

        fail("todo");

    }

    @Test
    public void testB() throws Exception {
        BranchOffice bo = new BranchOffice(); //Selected
        //task is selected task;

        IRequirementList requirementList = task.getRequirementList();

        //Step 4
        List<LocalDateTime> startTimes = resourceScheduler.getNextStartTimes(task.getRequirementList(), task.getEstimatedDuration(), 3);
        //ui show list
        //ui select or enter alternative
        LocalDateTime selectedStartTime = LocalDateTime.now();

        TimeSpan timeSpan = requirementList.calculateReservationTimeSpan(selectedStartTime, task.getEstimatedDuration());

        List<ResourceInstance> propesedResources = null;
        try {
            propesedResources = resourceScheduler.proposeResources(task.getRequirementList(), timeSpan);
            //Melden indien er geen resources beschikbaar zijn, ...
            //User selects alternative resources
            //


            //User select developers
            List<Developer> selectedDevelopers = null;

            //Maak reservaties
            resourceScheduler.makeReservations(task,propesedResources,selectedDevelopers, timeSpan);

        } catch (ConflictException e) {
            //vraag conflicting task op
            //run resolve conflict
            e.printStackTrace();
        }



    }

    private class ResourceScheduler {


        public List<ResourceReservation> getReservations(Task task) {

            return null;
        }

        public List<ResourceInstance> getAvailableResources(AResourceType type, TimeSpan ts) {
            return null;
        }

        public List<ResourceInstance> getAvailableResources(List<ResourceRequirement> resourceRequirements, Duration estimatedDuration) {
            return null;
        }

        public List<LocalDateTime> getNextStartTimes(IRequirementList resourceRequirements, Duration estimatedDuration, int n) {
            return null;
        }

        public void makeReservations(Task task, List<ResourceInstance> selectedResources, List<Developer> selectedDevelopers, TimeSpan ts) {

        }

        public boolean canMakeReservations(Task task, List<ResourceInstance> selectedResources, TimeSpan ts) {
            return false;
        }

        public List<ResourceInstance> proposeResources(IRequirementList requirementList, TimeSpan timeSpan) {
            return null;
        }
    }
}
