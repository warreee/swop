package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

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

        assertEquals(create,proj.getCreationTime());
        assertEquals(due,proj.getDueTime());
        assertEquals(name,proj.getName());
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
    public void CreateMemento_Test() throws Exception {
        projRep.addNewProject(name, description, create,due);
        projRep.addNewProject(name, description, create,due);
        Project proj1 = projRep.getProjects().get(0);
        Project proj2 = projRep.getProjects().get(1);
        proj1.addNewTask(description, 0.1, Duration.ofHours(8));
        proj2.addNewTask(description, 0.0, Duration.ofMinutes(30));
        proj2.addNewTask(description, 0.2, Duration.ofHours(100));
        // TODO
    }


   //SEE ProjectTest voor alle unit test voor het aanmaken van een project.
}