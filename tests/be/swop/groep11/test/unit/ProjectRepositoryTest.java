package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectRepositoryTest {

    private ProjectRepository projRep;
    private User user;
    private LocalDateTime create;
    private LocalDateTime due;
    private String name;
    private String description;

    @Before
    public void setUp() throws Exception {
        projRep = new ProjectRepository(new SystemTime());

        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";
    }

    @Test
    public void GetProjectsList() throws Exception {
        projRep.addNewProject(name, description, create,due);
        projRep.addNewProject(name, description, create,due);
        projRep.addNewProject(name, description, create,due);

        assertEquals(3, projRep.getProjects().size());
    }


    @Test
    public void AddNewProjectValidTest() throws Exception {

        projRep.addNewProject(name, description, create, due);
        Project proj = projRep.getProjects().get(projRep.getProjects().size()-1);

        assertEquals(create, proj.getCreationTime());
        assertEquals(due, proj.getDueTime());
        assertEquals(name, proj.getName());
        assertEquals(description,proj.getDescription());
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidTimesTest() throws Exception {
        projRep.addNewProject(name, description, due, create);
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidNameTest() throws Exception {
        projRep.addNewProject(null, description, create, due);
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidDescriptionTest() throws Exception {
        projRep.addNewProject(name, null, create, due);
    }

    @Test
    public void getAvailableTasksTest() {
        projRep.addNewProject("project 1", description, create, due);
        projRep.addNewProject("project 2", description, create, due);
        Project proj1 = projRep.getProjects().get(0);
        Project proj2 = projRep.getProjects().get(1);
        proj1.addNewTask("taak 1", 0.1, Duration.ofHours(8));
        proj2.addNewTask("taak 2", 0.0, Duration.ofMinutes(30));
        proj2.addNewTask("taak 3", 0.2, Duration.ofHours(100));
        proj2.getTasks().get(0).execute(LocalDateTime.now());
        ImmutableList<Task> availableTasks = projRep.getAllAvailableTasks();
        assertTrue(availableTasks.size() == 2);
        assertTrue(availableTasks.contains(proj1.getTasks().get(0)));
        assertTrue(availableTasks.contains(proj2.getTasks().get(1)));
    }

    @Test
    public void Memento_Test() throws Exception {
        projRep.addNewProject("project 1", description, create, due);
        projRep.addNewProject("project 2", description, create, due);
        Project proj1 = projRep.getProjects().get(0);
        Project proj2 = projRep.getProjects().get(1);
        proj1.addNewTask("taak 1", 0.1, Duration.ofHours(8));
        proj2.addNewTask("taak 2", 0.0, Duration.ofMinutes(30));
        proj2.addNewTask("taak 3", 0.2, Duration.ofHours(100));
        IProjectRepositoryMemento memento = projRep.createMemento();

        ProjectRepository projRep2 = new ProjectRepository(new SystemTime());
        projRep2.setMemento(memento);
        assertTrue(repositoryContainsProject(projRep2, "project 1"));
        assertTrue(repositoryContainsProject(projRep2, "project 2"));
        assertTrue(repositoryContainsTask(projRep2, "project 1", "taak 1"));
        assertTrue(repositoryContainsTask(projRep2, "project 1", "taak 2"));
        assertTrue(repositoryContainsTask(projRep2, "project 2", "taak 3"));
    }

    private boolean repositoryContainsProject(ProjectRepository projectRepository, String projectName) {
        for (Project project : projectRepository.getProjects()) {
            if (project.getName().equals(projectName)) {
                return true;
            }
        }
        return false;
    }

    private boolean repositoryContainsTask(ProjectRepository projectRepository, String projectName, String taskDescription) {
        if (repositoryContainsProject(projectRepository, projectName)) {
            return false;
        }
        for (Project project : projectRepository.getProjects()) {
            if (project.getName().equals(projectName)) {
                for (Task task : project.getTasks()) {
                    if (task.getDescription().equals(taskDescription)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


   //SEE ProjectTest voor alle unit test voor het aanmaken van een project.
}