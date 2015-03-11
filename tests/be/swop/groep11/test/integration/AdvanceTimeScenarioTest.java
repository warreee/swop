package be.swop.groep11.test.integration;

import be.swop.groep11.main.TaskMan;
import be.swop.groep11.main.ui.commands.CancelException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class AdvanceTimeScenarioTest {

    private TaskMan taskMan;
    private LocalDateTime now;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        taskMan = new TaskMan(now);

    }

    @Test
    public void updateTime_valid() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now){
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                return now.plusDays(1);
            }
        };

        //Step 1: User has indicated he wants to modify the system time.
        //Stap 2 & 3:
        LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd");
        //step 4
        taskMan.updateSystemTime(newSystemTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateTime_invalid_beforeCurrentSystemTime() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                return now.minusDays(1);
            }
        };
        //Step 1: User has indicated he wants to modify the system time.
        //Stap 2 & 3:
        LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd");
        //step 4
        taskMan.updateSystemTime(newSystemTime);
    }
}

