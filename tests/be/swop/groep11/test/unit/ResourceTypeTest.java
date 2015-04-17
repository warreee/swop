package be.swop.groep11.test.unit;

/**
 * Created by robin on 8/04/15.
 */
public class ResourceTypeTest {
/*
    IResourceType t1, t2, t3, t4, t5, t6;
    private ResourceTypeRepository repo;

    @Before
    public void setUp(){
        this.repo = new ResourceTypeRepository();

        repo.addNewResourceType("Autobus");
        repo.addNewResourceType("Lijnbus");
        repo.addNewResourceType("Bushok");

        DailyAvailability a = new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(16, 0));

        repo.addNewResourceType("Fiets", a);
        repo.addNewResourceType("Segway", a);
        repo.addNewResourceType("Hurr Durr", a);
    }

    //TODO sommige onderstaande testen zitten in ResourceTypeBuilderTest, ... (fix me)

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
*/

}
