package be.swop.groep11.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TaskTest.class, SystemTimeTest.class, TaskStatusTest.class,ControllerStackTest.class,
        TimeSpanTest.class, ResourceTypeConstraintTest.class,AbstractControllerTest.class,
        ProjectTest.class, ProjectRepositoryTest.class,MainControllerTest.class,CompanyTest.class,BranchOfficeTest.class,
        ResourceTypeTest.class, DependencyGraphTest.class, DeveloperTypeTest.class, DailyAvailabilityTest.class, ResourceManagerTest.class,
        ResourceScheduleTest.class,ResourceRepositoryTest.class})
public class UnitTests {
}