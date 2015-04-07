package be.swop.groep11.test;

import be.swop.groep11.test.integration.IntegrationTests;
import be.swop.groep11.test.unit.UnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
/**
 * Created by Ronald on 23/02/2015.
 */
@RunWith(Suite.class)
@SuiteClasses({UnitTests.class, IntegrationTests.class})
public class AllTests {

}
