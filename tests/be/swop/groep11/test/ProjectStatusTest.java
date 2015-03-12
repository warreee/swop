package be.swop.groep11.test;

import be.swop.groep11.main.*;
import be.swop.groep11.main.System;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ProjectStatusTest {

    //TODO veranderen van naam & specificeren welk gedrag er getest wordt. Ivm basic functionaliteit projectRepository zie unit tests.

    private ProjectRepository projectRepository;
    @Before
    public void setUp() throws Exception {
        System system = new System();
        projectRepository = system.getProjectRepository();
    }


    /**
    * Indien er geen projecten zijn toegevoegd, dan verwachten we een lege lijst.
     */
    @Test
    public void noProjectsTest() throws Exception {
        assertTrue("Geen projecten in Tman: ", projectRepository.getProjects().isEmpty());
    }

/**
    *  We voegen 4 projecten toe en kijken na dat:
    * - Er 4 projecten bestaan
    * Hierna voegen we er nog een toe en kijken na dat:
    * - Er 5 projecten bestaan
    * Hierna testen we of de stati van deze 5 projecten overeenkomen met ONGOING aangezien dit de standaard status is.
     */
    @Test
    public void multipleProjectsTest() throws Exception {
        addProjectsToProjectManager(4);
        assertTrue("4 projecten in Tman: ", projectRepository.getProjects().size() == 4);
        addProjectsToProjectManager(1);
        assertTrue("5 projects in Tman: ", projectRepository.getProjects().size() == 5);
        for(Project p: projectRepository.getProjects()){
            assertTrue("Projectstatus is ongoing: ", p.getProjectStatus().equals(ProjectStatus.ONGOING));
        }
    }

  /*  /**
    * We voegen een project toe, daarna een taak aan dit project.
    * Hierna finishen we de taak.
    * We finishen het project.
    * We kijken na of het project effectief gefinished is.
     */
    /*@Test
    public void projectFinishedTest() throws Exception {
        addProjectsToProjectManager(1);
        Project testProject = projectRepository.getProjects().get(0);
        addTasksToProject(testProject, 1);
        Task testTask = testProject.getTasks().get(0);
        //testTask.finish();
        assertFalse("Project status is not finished: ", testProject.getProjectStatus().equals(ProjectStatus.FINISHED));
        testProject.finish();
        assertTrue("Project status is finished: ", testProject.getProjectStatus().equals(ProjectStatus.FINISHED));
    }

    /**
     * We voegen een project toe en daarna een taak aan dit project.
     * We proberen het project te finishen.
     * We kijken of dit gefaald is.
     */
    /*@Test
    public void projectNotFinishedTest() throws Exception {
        addProjectsToProjectManager(1);
        Project testProject = projectRepository.getProjects().get(0);
        addTasksToProject(testProject, 1);
        assertFalse("Project status is not finished: ", testProject.getProjectStatus().equals(ProjectStatus.FINISHED));
        testProject.finish();
        assertFalse("Project status is not finished: ", testProject.getProjectStatus().equals(ProjectStatus.FINISHED));
    }*/


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////HELPER FUNCTIONS///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Deze methode voegt numberOfProjects aantal projecten toe aan projectRepository.
     * @param numberOfProjects Het aantal toe te voegen projecten.
     */
    private void addProjectsToProjectManager(long numberOfProjects){

        for (int i = 0; i < numberOfProjects; i++) {
            String name = "Test Project";
            String description = "Test Description";
            LocalDateTime creationTime = LocalDateTime.now();
            LocalDateTime duetime = creationTime.plusDays(2);
            User user = new User("Test User");
            projectRepository.addNewProject(name, description, creationTime, duetime, user);
        }
    }

    /**
     * Voegt numberOfTasks Tasks toe aan een gegeven project.
     * @param project Het project waaraan taken moeten worden toegevoegd.
     * @param numberOfTasks Het aantal toe te voegen taken.
     */
    private void addTasksToProject(Project project, long numberOfTasks){
        for(int i=0; i<numberOfTasks; i++){
            String name = "Test task";
            String description = "Test description.";
            double acceptableDeviation = 0.05;
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = LocalDateTime.now();
            project.addNewTask(description, acceptableDeviation, Duration.ofHours(8));
        }
    }
}