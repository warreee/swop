package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.User;
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
        projRep = new ProjectRepository();
        user = new User("ROOT");

        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";
    }

    @Test
    public void GetProjectsList() throws Exception {
       //TODO test implementeren immutable list
    }

    /*
    @Test
    public void AddNewProject_valid() throws Exception {

        int ID = projRep.addNewProject(name, description, create,due,user);
        Project proj = projRep.getProjectByID(ID);

        assertEquals(create,proj.getCreationTime());
        assertEquals(due,proj.getDueTime());
        assertEquals(user,proj.getCreator());
        assertEquals(name,proj.getName());
        assertEquals(description,proj.getDescription());
    }
    */

   //SEE ProjectTest voor alle unit test voor het aanmaken van een project.
}