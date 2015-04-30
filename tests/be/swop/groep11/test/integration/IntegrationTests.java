package be.swop.groep11.test.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({AdvanceTimeScenarioTest.class,CreateProjectScenarioTest.class,CreateTaskScenarioTest.class,UpdateTaskStatusScenarioTest.class,ShowProjectsScenarioTest.class,PlanTaskScenarioTest.class})
/**
 * Dit functioneert als verzameling voor alle IntegrationTests.
 */
public class IntegrationTests {
}