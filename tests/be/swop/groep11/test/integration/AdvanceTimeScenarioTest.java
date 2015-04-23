package be.swop.groep11.test.integration;

import be.swop.groep11.main.actions.CancelException;
import be.swop.groep11.main.controllers.AdvanceTimeController;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AdvanceTimeScenarioTest {

    private SystemTime systemTime;
    private LocalDateTime now;
    private UserInterface mockedUI;
    private AdvanceTimeController advanceTimeController;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        this.systemTime = new SystemTime(now);
        this.mockedUI = mock(UserInterface.class);
        advanceTimeController = new AdvanceTimeController(systemTime,mockedUI);
    }

    @Test
    public void updateTime_validTest() throws Exception {
        //stubbing
        when(mockedUI.requestDatum(anyString())).thenReturn(now.plusDays(1));

        advanceTimeController = new AdvanceTimeController(systemTime,mockedUI);
        advanceTimeController.advanceTime();
    }

    @Test (expected = StopTestException.class)
    public void updateTime_invalidNewSystemTimeTest() throws Exception {
        //stubbing
        when(mockedUI.requestDatum(anyString())).thenReturn(now.minusDays(1));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(IllegalArgumentException.class));

        advanceTimeController = new AdvanceTimeController(systemTime,mockedUI);
        advanceTimeController.advanceTime();
    }

    @Test(expected = StopTestException.class)
    public void updateTime_CancelTest() throws Exception {
        //stubbing
        when(mockedUI.requestDatum(anyString())).thenThrow(new CancelException("Cancel in test"));
        doThrow(new StopTestException("Stop test")).when(mockedUI).printException(any(CancelException.class));

        advanceTimeController = new AdvanceTimeController(systemTime,mockedUI);
        advanceTimeController.advanceTime();
    }
}

