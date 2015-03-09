package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TaskTest.class,TaskManTest.class, ProjectTest.class, ProjectRepositoryTest.class,DependencyConstraintTest.class})
public class UnitTests {
    //TODO toevoegen unit test klassen aan "@SuiteClasses({})"
}