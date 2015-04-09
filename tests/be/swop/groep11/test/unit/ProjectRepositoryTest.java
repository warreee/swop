package be.swop.groep11.test.unit;

import be.swop.groep11.main.project.Project;
import be.swop.groep11.main.project.ProjectRepository;
import be.swop.groep11.main.User;
import be.swop.groep11.main.TMSystem;
import org.junit.Before;
import org.junit.Test;

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
        projRep = new TMSystem().getProjectRepository();
        user = new User("ROOT");

        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";
    }

    @Test
    public void GetProjectsList() throws Exception {
        projRep.addNewProject(name, description, create,due,user);
        projRep.addNewProject(name, description, create,due,user);
        projRep.addNewProject(name, description, create,due,user);

        assertEquals(3, projRep.getProjects().size());
    }


    @Test
    public void AddNewProjectValidTest() throws Exception {

        projRep.addNewProject(name, description, create,due,user);
        Project proj = projRep.getProjects().get(projRep.getProjects().size()-1);

        assertEquals(create,proj.getCreationTime());
        assertEquals(due,proj.getDueTime());
        assertEquals(user,proj.getCreator());
        assertEquals(name,proj.getName());
        assertEquals(description,proj.getDescription());
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidTimesTest() throws Exception {
        projRep.addNewProject(name, description, due, create, user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidNameTest() throws Exception {
        projRep.addNewProject(null, description, create, due, user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void AddNewProjectInvalidDescriptionTest() throws Exception {
        projRep.addNewProject(name, null, create, due, user);
    }


   //SEE ProjectTest voor alle unit test voor het aanmaken van een project.
}