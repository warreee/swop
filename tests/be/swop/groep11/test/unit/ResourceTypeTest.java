package be.swop.groep11.test.unit;

import be.swop.groep11.main.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by robin on 8/04/15.
 */
public class ResourceTypeTest {

    @Before
    public void setUp(){

    }

    @Test
    public void newResourceTypeTest(){
        ResourceType t1 = new ResourceType("Autobus");
        ResourceType t2 = new ResourceType("Lijnbus", new DailyAvailability(LocalTime.of(8, 10), LocalTime.of(16,0)));
        ArrayList<ResourceType> rList = new ArrayList<>();
        rList.add(t1);
        RequirementConstraint r1 = new RequirementConstraint(t2, rList);

    }

}
