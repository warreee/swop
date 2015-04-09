package be.swop.groep11.test.unit;

import be.swop.groep11.main.*;
import be.swop.groep11.main.resource.constraint.ConflictConstraint;
import be.swop.groep11.main.resource.constraint.RequirementConstraint;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by robin on 8/04/15.
 */
public class ResourceTypeTest {
    ResourceType t1, t2, t3, t4, t5, t6;

    @Before
    public void setUp(){
        ResourceType t1 = new ResourceType("Autobus");
        ResourceType t2 = new ResourceType("Lijnbus");
        ResourceType t3 = new ResourceType("Bushok");

        ResourceType t4 = new ResourceType("Fiets", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(16, 0)));
        ResourceType t5 = new ResourceType("Segway", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(16, 0)));
        ResourceType t6 = new ResourceType("Hurr Durr", new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(16, 0)));
    }

    @Test
    public void newResourceTypeTest(){
        ResourceType t1 = new ResourceType("Autobus");
        ResourceType t2 = new ResourceType("Lijnbus", new DailyAvailability(LocalTime.of(8, 10), LocalTime.of(16,0)));
        ArrayList<ResourceType> rList = new ArrayList<>();
        rList.add(t1);
        RequirementConstraint r1 = new RequirementConstraint(t2, rList);
    }

    @Test (expected = IllegalArgumentException.class)
    public void illegalRequirementConstraintTest(){
        ArrayList<ResourceType> rList = new ArrayList<>();
        rList.add(t2);
        RequirementConstraint r1 = new RequirementConstraint(t2, rList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void conflictingRequirementConflictConstraint(){
        ArrayList<ResourceType> rList = new ArrayList<>();
        ArrayList<ResourceType> cList = new ArrayList<>();
        ArrayList<ResourceTypeConstraint> allConstraints = new ArrayList<>();

        // Type 2 is nodig voor Type 1
        rList.add(t2);
        RequirementConstraint r1 = new RequirementConstraint(t1, rList);
        allConstraints.add(r1);

        // Type 2 conflicteert met Type 1
        cList.add(t1);
        ConflictConstraint c1 = new ConflictConstraint(t2, cList);
        allConstraints.add(c1);

        // We zetten de ResourceTypeConstraints van type 1. Dit zou een exception moeten geven.
        t1.setResourceTypeConstraints(allConstraints);
    }

}
