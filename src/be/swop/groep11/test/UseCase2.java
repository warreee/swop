package be.swop.groep11.test;

import be.swop.groep11.main.ProjectManager;
import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectStatus;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class UseCase2 {

    private ProjectManager projectManager;
    private LocalDateTime now;
    private User user;

    @Before
    public void setUp() throws Exception {
        projectManager = new ProjectManager();
        LocalDateTime now = LocalDateTime.now();
        String name = "Test Project";
        String description = "Test Description";
        LocalDateTime creationTime = now;
        LocalDateTime dueTime = now.plusDays(2);
        User user = new User("Test User");
        projectManager.addProject(name, description, creationTime, dueTime, user);
    }

    /**
     * Controleer of het project de juiste naam en beschrijving heeft.
     */
    @Test
    public void testProjectName() throws Exception {
        Project project = projectManager.getProjects().get(0);
        assertTrue(project.getName().equals("Test Project"));
    }

    /**
     * Controleer of het project de juiste beschrijving heeft.
     */
    @Test
    public void testProjectDescription() throws Exception {
        Project project = projectManager.getProjects().get(0);
        assertTrue(project.getDescription().equals("Test Description"));
    }

    /**
     * Controleer of het project de juiste creatietijd heeft.
     */
    @Test
    public void testProjectCreationTime() throws Exception {
        Project project = projectManager.getProjects().get(0);
        assertTrue(project.getCreationTime().equals(now));
    }

    /**
     * Controleer of het project de juiste eindtijd heeft.
     */
    @Test
    public void testProjectDueTime() throws Exception {
        Project project = projectManager.getProjects().get(0);
        assertTrue(project.getDueTime().equals(now.plusDays(2)));
    }

    /**
     * Controleer of het project de juiste status (ProjectStatus.ONGOING) heeft.
     */
    @Test
    public void testProjectStatus() throws Exception {
        Project project = projectManager.getProjects().get(0);
        assertTrue(project.getStatus().equals(ProjectStatus.ONGOING));
    }

    /**
     * Controleer of het project door de juiste gebruiker is aangemaakt.
     */
    @Test
    public void testProjectCreator() throws Exception {
        Project project = projectManager.getProjects().get(0);
        assertTrue(project.getCreator().equals(user));
    }

    /**
     * Wanneer een nieuw project aangemaakt wordt, moet een geldige naam opgegeven worden.
     */
    @Test
    public void validName() throws Exception {
        String validName = "Test";
        assertTrue("\"Test\" is een geldige naam: ", Project.isValidName(validName));
        String description = "Test Description";
        LocalDateTime creationTime = now;
        LocalDateTime dueTime = now.plusDays(2);
        User user = new User("Test User");
        projectManager.addProject(validName, description, creationTime, dueTime, user);
    }

    /**
     * Wanneer een nieuw project aangemaakt wordt, mag de naam niet leeg zijn.
     * Indien dit toch zo is, wordt bij het aanmaken van het project een IllegalArgumentException geworpen.
     */
    @Test (expected = IllegalArgumentException.class)
    public void emptyName() throws Exception() {
        String invalidName = "";
        assertFalse("\"\" is geen geldige naam: ", Project.isValidName(invalidName));
        String description = "Test Description";
        LocalDateTime creationTime = now;
        LocalDateTime dueTime = now.plusDays(2);
        User user = new User("Test User");
        projectManager.addProject(invalidName, description, creationTime, dueTime, user);
    }

    /**
     * Wanneer een nieuw project aangemaakt wordt, mag er nog geen project met die naam zijn.
     * Indien dit toch zo is, wordt bij het aanmaken van het project een IllegalArgument geworpen.
     */
    @Test (expected = IllegalArgumentException.class)
    public void existingName() throws Exception() {
        String invalidName = "Test Project";
        assertFalse("\"Test Project\" is geen geldige naam: ", Project.isValidName(invalidName));
        String description = "Test Description";
        LocalDateTime creationTime = now;
        LocalDateTime dueTime = now.plusDays(2);
        User user = new User("Test User");
        projectManager.addProject(invalidName, description, creationTime, dueTime, user);
    }

    // TODO: deze testklasse is nog niet volledig

}