package be.swop.groep11.test.unit;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ActionProcedure;
import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.CreateProjectController;
import be.swop.groep11.main.controllers.LogonController;
import be.swop.groep11.main.controllers.MainController;
import be.swop.groep11.main.exception.FailedConditionException;
import be.swop.groep11.main.exception.InterruptedAProcedureException;
import be.swop.groep11.test.integration.StopTestException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 15/05/2015.
 */
public class ControllerStackTest {

    private ControllerStack controllerStack;
    private MainController main;

    @Before
    public void setUp() throws Exception {
        controllerStack = spy(new ControllerStack());
        main = mock(MainController.class);

    }

    @Test(expected = NoSuchElementException.class)
    public void activateAndDeactivateController_corrrect() throws Exception {
        controllerStack.activateController(main);

        assertEquals(main, controllerStack.getActiveController());
        controllerStack.deActivateController(main);
        controllerStack.getActiveController();
    }

    @Test(expected = StopTestException.class)
    public void executeActionProcedure_valid() throws Exception {
        MainController main = mock(MainController.class);

        ActionProcedure ap = new ActionProcedure(() -> {
            throw new StopTestException("OK");
        }, () -> true);

        controllerStack.addActionProcedure(main, Action.CREATETASK, ap);
        controllerStack.activateController(main);

        HashSet<Action> actions = new HashSet<>();
        actions.add(Action.CREATETASK);
        assertEquals(actions, controllerStack.getAcceptableActions(main));

        controllerStack.executeAction(Action.CREATETASK);
    }

    @Test
    public void executeInvalidAction() throws Exception {
        final boolean[] test = {false};
        ActionProcedure invalid = new ActionProcedure(() -> {
           test[0] = true;
        }, () -> true);
        controllerStack.setInvalidActionProcedure(invalid);
        assertEquals(invalid, controllerStack.getInvalidProcedure());

        controllerStack.activateController(main);
        controllerStack.executeAction(Action.CREATETASK);
        assertTrue(test[0]);
    }

    @Test
    public void executeAPactivateAndDeactiveController() throws Exception {
        final boolean[] test = {false};
        CreateProjectController createProjectController = mock(CreateProjectController.class);
        ActionProcedure ap = new ActionProcedure(createProjectController,() -> {
            test[0] = true;
        }, () -> true,true);

        controllerStack.activateController(main);
        controllerStack.addActionProcedure(main, Action.CREATEPROJECT, ap);
        controllerStack.executeAction(Action.CREATEPROJECT);
        verify(controllerStack).activateController(createProjectController);
        verify(controllerStack).deActivateController(createProjectController);
    }

    @Test(expected = FailedConditionException.class)
    public void executeAPwithFailedCondition() throws Exception {
        final boolean[] test = {false};
        CreateProjectController createProjectController = mock(CreateProjectController.class);
        ActionProcedure ap = new ActionProcedure(createProjectController,() -> {
            test[0] = true;
        }, () -> false);

        controllerStack.activateController(main);
        controllerStack.addActionProcedure(main, Action.CREATEPROJECT, ap);
        controllerStack.executeAction(Action.CREATEPROJECT);
    }

    @Test
    public void executeAPInteruptedAPException_restoreStack() throws Exception {
        final boolean[] test = {false};
        CreateProjectController createProjectController = mock(CreateProjectController.class);
        ActionProcedure ap = new ActionProcedure(createProjectController,() -> {
            test[0] = true;
            throw new InterruptedAProcedureException();
        }, () -> true,false);

        controllerStack.activateController(main);
        controllerStack.addActionProcedure(main, Action.CREATEPROJECT, ap);
        controllerStack.executeAction(Action.CREATEPROJECT);

        verify(controllerStack).activateController(createProjectController);
        assertEquals(main, controllerStack.getActiveController());
    }

    @Test
    public void defaultActionProcedureExecution() throws Exception {
        LogonController logonController = mock(LogonController.class);
        final boolean[] logedOn = new boolean[1];
        ActionProcedure logon = new ActionProcedure(logonController, () -> logedOn[0] = true, () -> true, false);
        ActionProcedure logout = new ActionProcedure(logonController, () -> logedOn[0] = false, () -> true, true);

        int[] i = {0};
        ActionProcedure helpAP = new ActionProcedure(() -> i[0]++, () -> true);
        controllerStack.addDefaultActionProcedure(Action.HELP, helpAP);

        controllerStack.addActionProcedure(main, Action.LOGON, logon);
        controllerStack.addActionProcedure(logonController, Action.LOGOUT, logout);

        controllerStack.activateController(main);
        controllerStack.executeAction(Action.HELP);
        controllerStack.executeAction(Action.LOGON);
        assertEquals(logonController, controllerStack.getActiveController());
        assertTrue(logedOn[0]);
        controllerStack.executeAction(Action.HELP);
        controllerStack.executeAction(Action.LOGOUT);
        assertFalse(logedOn[0]);

        assertEquals(2, i[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addInvalidProcedure_Illegal() throws Exception {
        controllerStack.setInvalidActionProcedure(null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void addDefaultActionProcedure_invalid() throws Exception {
        controllerStack.addDefaultActionProcedure(Action.HELP,null);

    }
}