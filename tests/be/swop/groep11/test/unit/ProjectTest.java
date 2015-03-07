package be.swop.groep11.test.unit;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class ProjectTest {

    private User user;
    private LocalDateTime create;
    private LocalDateTime due;
    private String name;
    private String description;

    private int ID;

    @Before
    public void setUp() throws Exception {
        user = new User("ROOT");

        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";

        ID=0;
    }

    //VRAAG: is dit de juiste plaats voor deze testen, of horen deze bij ProjectRepositoryTests
    //TODO addNewTask
    //TODO change project status

    @Test
    public void NewProject_valid() throws Exception {
        new Project(ID,name, description, create,due,user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void NewProject_invalid_ProjectID() throws Exception {
        new Project(-1,name, description, create,due,user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void NewProject_invalid_Name() throws Exception {
        new Project(ID,"", description, create,due,user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_NameNull() throws Exception {
        new Project(ID,null,description, create,due,user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_Description() throws Exception {
        new Project(ID,name, "", create, due, user);
    }
    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DescriptionNull() throws Exception {
        new Project(ID,name, null, create,due,user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationTimeAfterDueTime() throws Exception {
        new Project(ID,name, description, LocalDateTime.now().plusSeconds(3600),LocalDateTime.now(),user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationEqualDue() throws Exception {
        new Project(ID,name, description, LocalDateTime.now(), LocalDateTime.now(), user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DueTimeBeforeCreationTime() throws Exception {
        //DueTime mag niet voor creation time zijn.
        new Project(ID,name, description, LocalDateTime.now(),LocalDateTime.now().minusSeconds(60),user);
    }
    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationAndDueNull() throws Exception {
        new  Project(ID,name, description, null,null,user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationTimeNull() throws Exception {
        new Project(ID,name, description, null,due,user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DueTimeNull() throws Exception {
        //DueTime mag niet null zijn
        new Project(ID,name, description, create,null,user);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_User() throws Exception {
       new Project(ID,name, description, create,due,null);
    }
}