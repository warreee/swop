package be.swop.groep11.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
/**
 * Created by Ronald on 23/02/2015.
 */
@RunWith(Suite.class)
@SuiteClasses({be.swop.groep11.test.unit.UnitTests.class, be.swop.groep11.test.integration.IntegrationTests.class})
public class AllTests {

}
