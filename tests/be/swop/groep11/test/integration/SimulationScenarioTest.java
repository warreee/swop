package be.swop.groep11.test.integration;


import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.SimulationController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class SimulationScenarioTest {

    private ProjectRepository repository;
    private LocalDateTime now;
    private SystemTime systemTime;
    private SimulationController simulationController;
    private UserInterface mockedUI;
    private ImmutableList<Project> projects;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        systemTime = new SystemTime(now);
        BranchOffice branchOffice = mock(BranchOffice.class);
        repository = new ProjectRepository(systemTime);
        repository.addNewProject("Naam1", "Omschrijving1", LocalDateTime.now(), now.plusDays(10));
        repository.getProjects().get(0).addNewTask("TaakOmschrijving", 0.5, Duration.ofHours(8));

        this.mockedUI = mock(UserInterface.class);
        this.simulationController = new SimulationController(mock(ControllerStack.class), repository, mockedUI);
    }

    @Test
    public void simulationRealizeTest() throws Exception {
        simulationController.startSimulation();

        repository.getProjects().get(0).addNewTask("Mag er nog in staan", 5, Duration.ofHours(2));
        assertTrue(repository.getProjects().get(0).getTasks().size() == 2);
        assertEquals("TaakOmschrijving", repository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Mag er nog in staan", repository.getProjects().get(0).getTasks().get(1).getDescription());

        simulationController.realize();

        assertTrue(repository.getProjects().get(0).getTasks().size() == 2);
        assertEquals("TaakOmschrijving", repository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Mag er nog in staan", repository.getProjects().get(0).getTasks().get(1).getDescription());
    }

    @Test
    public void simulationCancelTest() throws Exception {
        simulationController.startSimulation();

        repository.getProjects().get(0).addNewTask("Mag er niet meer in staan", 5, Duration.ofHours(2));
        assertTrue(repository.getProjects().get(0).getTasks().size() == 2);
        assertEquals("TaakOmschrijving", repository.getProjects().get(0).getTasks().get(0).getDescription());
        assertEquals("Mag er niet meer in staan", repository.getProjects().get(0).getTasks().get(1).getDescription());

        simulationController.cancel();

        assertTrue(repository.getProjects().get(0).getTasks().size() == 1);
        assertEquals("TaakOmschrijving", repository.getProjects().get(0).getTasks().get(0).getDescription());
    }
}