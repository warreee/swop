package be.swop.groep11.test.unit;

import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.*;
import be.swop.groep11.main.ui.UserInterface;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Ronald on 1/05/2015.
 */
public class AbstractControllerAndMainControllerTest {


    private UserInterface mockedUI;
    private MainController main;
    private ControllerStack abmMock;

    @Before
    public void setUp() throws Exception {
        abmMock = mock(ControllerStack.class);
        this.mockedUI = mock(UserInterface.class);
        mockedUI.setControllerStack(abmMock);
        main = new MainController(mockedUI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void advanceTime() throws Exception {
        main.advanceTime();
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateTask() throws Exception {
        main.updateTask();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTask() throws Exception {
        main.createTask();
    }

    @Test(expected = IllegalArgumentException.class)
    public void planTask() throws Exception {
        main.planTask();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createProject() throws Exception {
        main.createProject();
    }

    @Test(expected = IllegalArgumentException.class)
    public void showProjects() throws Exception {
        main.showProjects();
    }

    @Test(expected = IllegalArgumentException.class)
    public void startSimulation() throws Exception {
        main.startSimulation();
    }

    @Test(expected = IllegalArgumentException.class)
    public void logon() throws Exception {
        main.logon();
    }

    @Test(expected = IllegalArgumentException.class)
    public void logout() throws Exception {
        main.logOut();
    }

    @Test(expected = IllegalArgumentException.class)
    public void performDelegations() throws Exception {
        main.performDelegations();
    }
    @Test(expected = IllegalArgumentException.class)
    public void selectDelegatedTo() throws Exception {
        main.selectDelegatedTo();
    }

    @Test(expected = IllegalArgumentException.class)
    public void delegateTask() throws Exception {
        main.delegateTask();
    }

}