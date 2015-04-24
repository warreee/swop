package be.swop.groep11.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TaskTest.class, SystemTimeTest.class, TaskStatusTest.class,
        TimeSpanTest.class, ResourceTypeBuilderTest.class,
        ResourceTypeConstraintTest.class,RequirementListBuilderTest.class,
        ProjectTest.class, ProjectRepositoryTest.class, DependencyConstraintTest.class,
        ResourceTest.class, DependencyGraphTest.class, DeveloperTest.class, DailyAvailabilityTest.class, ResourceManagerTest.class})
public class UnitTests {
}