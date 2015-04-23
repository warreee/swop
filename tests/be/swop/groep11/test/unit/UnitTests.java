package be.swop.groep11.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TaskTest.class, TMSystemTest.class, TaskStatusTest.class,
        TimeSpanTest.class, ResourceTypeBuilderTest.class,
        ResourceTypeConstraintTest.class,
        ProjectTest.class, ProjectRepositoryTest.class, DependencyConstraintTest.class,
        ResourceTest.class})
public class UnitTests {
}