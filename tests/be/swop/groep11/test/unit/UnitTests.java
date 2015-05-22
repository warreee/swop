package be.swop.groep11.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TaskTest.class, SystemTimeTest.class, TaskStatusTest.class,ControllerStackTest.class,
        TimeSpanTest.class, ResourceTypeConstraintTest.class,
        ProjectTest.class, ProjectRepositoryTest.class,AbstractControllerAndMainControllerTest.class,CompanyTest.class,BranchOfficeTest.class,
        ResourceTypeTest.class, DependencyGraphTest.class, DeveloperTypeTest.class, DailyAvailabilityTest.class
        ,ResourceRepositoryTest.class,ObservableTest.class,TaskProxyTest.class, ProjectProxyTest.class, BranchOfficeProxyTest.class,
        ResourcePlannerTest.class,PlanBuilderTest.class,PlanTest.class,RequirementListBuilderTest.class})
public class UnitTests {
}
