package be.swop.groep11.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TaskTest.class,TaskManTest.class, ProjectRepositoryTest.class,DependencyConstraintTest.class})
public class UnitTests {
    //TODO toevoegen unit test klassen aan "@SuiteClasses({})"
}