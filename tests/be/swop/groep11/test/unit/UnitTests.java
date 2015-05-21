package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.BranchOfficeProxy;
import be.swop.groep11.main.core.ProjectProxy;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TaskTest.class, SystemTimeTest.class, TaskStatusTest.class,ControllerStackTest.class,
        TimeSpanTest.class, ResourceTypeConstraintTest.class,AbstractControllerTest.class,
        ProjectTest.class, ProjectRepositoryTest.class,MainControllerTest.class,CompanyTest.class,BranchOfficeTest.class,
        ResourceTypeTest.class, DependencyGraphTest.class, DeveloperTypeTest.class, DailyAvailabilityTest.class
        ,ResourceRepositoryTest.class,ObservableTest.class,TaskProxyTest.class, ProjectProxyTest.class, BranchOfficeProxyTest.class})
public class UnitTests {
}