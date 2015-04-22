package be.swop.groep11.test.integration;

import be.swop.groep11.main.core.TMSystem;
import be.swop.groep11.main.controllers.AdvanceTimeController;
import be.swop.groep11.main.actions.CancelException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class AdvanceTimeScenarioTest {

    private TMSystem TMSystem;
    private LocalDateTime now;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        TMSystem = new TMSystem(now);

    }

    @Test
    public void updateTime_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now){
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                return now.plusDays(1);
            }
        };
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(TMSystem,ui);
        advanceTimeController.advanceTime();
    }

    @Test (expected = StopTestException.class)
    public void updateTime_invalidNewSystemTimeTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                return now.minusDays(1);
            }
            @Override
            public void printException(Exception e) {
                throw new StopTestException("Cancel");
            }
        };
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(TMSystem,ui);
        advanceTimeController.advanceTime();
    }

    @Test
    public void updateTime_CancelTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                throw new CancelException("Cancel");
            }

        };
        AdvanceTimeController advanceTimeController = new AdvanceTimeController(TMSystem,ui);
        advanceTimeController.advanceTime();
    }
}

