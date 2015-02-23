package be.swop.groep11.test;

import be.swop.groep11.main.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class UseCase1 {
    private ProjectManager projectManager;
    @Before
    public void setUp() throws Exception {
        projectManager = new ProjectManager();
    }


    /*
    Indien er geen projecten zijn toegevoegd, dan verwachten we een lege lijst.
     */
    @Test
    public void noProjectsTest() throws Exception {
        assertTrue("Geen projecten in Tman: ", projectManager.getProjects().isEmpty());
    }

    /*
    We voegen 4 projecten toe en kijken na dat:
    - Er 4 projecten bestaan
    Hierna voegen we er nog een toe en kijken na dat:
    - Er 5 projecten bestaan
     */

    @Test
    public void multipleProjectsTest() throws Exception {
        addProjectsToProjectManager(4);
        assertTrue("4 projecten in Tman: ", projectManager.getProjects().size() == 4);
        addProjectsToProjectManager(1);
        assertTrue("5 projecten in Tman: ", projectManager.getProjects().size() == 5);
    }

    /////////////////////////////////////////HELPER FUNCTIONS///////////////////////////////////////////////////////////
    /*
    Deze methode voeg numberOfProjects aantal projecten toe aan projectManager
     */
    private void addProjectsToProjectManager(long numberOfProjects){

        for (int i = 0; i < numberOfProjects; i++) {
            String name = "Test Project";
            String description = "Test Description";
            LocalDateTime creationTime = LocalDateTime.now();
            LocalDateTime duetime = creationTime.plusDays(2);
            User user = new User("Test User");
            projectManager.addProject(name, description, creationTime, duetime, user);
        }
    }
}