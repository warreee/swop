package be.swop.groep11.test.unit;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.core.User;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectTest {

    private User user;
    private ProjectRepository repository;
    private LocalDateTime create;
    private LocalDateTime due;
    private String name;
    private String description;
    private Project project;
    private SystemTime systemTime;

    @Before
    public void setUp() throws Exception {
        user = new User("ROOT");

        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";
        systemTime = new SystemTime(LocalDateTime.of(2015,1,1,0,0));
        repository = new ProjectRepository(systemTime);

        project = new Project(name, description, create, due, systemTime );
    }

    //TODO change project status

    @Test
    public void AddNewTask_valid() {
        project.addNewTask("Test taak", 0.1, Duration.ofHours(2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void AddNewTask_invalid1() {
        project.addNewTask("", 0.1, Duration.ofHours(2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void AddNewTask_invalid2() {
        project.addNewTask("Test", -0.1, Duration.ofHours(2));
    }

    @Test(expected=IllegalArgumentException.class)
    public void AddNewTask_invalid3() {
        project.addNewTask("Test", 0.1, null);
    }

    @Test
    public void isOverTime_OverTimeProject() {
        Project project1 = new Project(name,description,LocalDateTime.of(2015,3,8,9,32),LocalDateTime.of(2015,3,13,18,0), systemTime );
        project1.addNewTask("Taak",0.1,Duration.ofHours(24));
        project1.addNewTask("Afhankelijke taak", 0, Duration.ofHours(16));
        project1.getTasks().get(1).addNewDependencyConstraint(project1.getTasks().get(0));
        project1.getTasks().get(0).execute(LocalDateTime.of(2015, 3, 14, 18, 0));
        System.out.println(project1.getEstimatedEndTime());
        assertTrue(project1.isOverTime());
        //TODO fix
    }

    @Test
    public void isOverTime_NotOverTimeProject() {

        Project project1 = new Project(name,description,LocalDateTime.of(2015,3,8,9,32),LocalDateTime.of(2015,3,13,18,0), systemTime);
        project1.addNewTask("Taak",0.1,Duration.ofHours(8));
        project1.addNewTask("Afhankelijke taak", 0, Duration.ofHours(16));
        project1.getTasks().get(1).addNewDependencyConstraint(project1.getTasks().get(0));
        project1.getTasks().get(0).execute(LocalDateTime.of(2015, 3, 8, 0, 0));
        assertFalse(project1.isOverTime());
    }

    @Test
    public void NewProject_valid() throws Exception {
        new Project(name, description, create,due, systemTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void NewProject_invalid_Name() throws Exception {
        new Project("", description, create,due, systemTime );
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_NameNull() throws Exception {
        new Project(null,description, create,due, systemTime );
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_Description() throws Exception {
        new Project(name, "", create, due, systemTime );
    }
    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DescriptionNull() throws Exception {
        new Project(name, null, create,due, systemTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationTimeAfterDueTime() throws Exception {
        new Project(name, description, LocalDateTime.now().plusSeconds(3600),LocalDateTime.now(), systemTime );
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationEqualDue() throws Exception {
        new Project(name, description, LocalDateTime.now(), LocalDateTime.now(), systemTime );
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DueTimeBeforeCreationTime() throws Exception {
        //DueTime mag niet voor creation time zijn.
        new Project(name, description, LocalDateTime.now(),LocalDateTime.now().minusSeconds(60), systemTime);
    }
    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationAndDueNull() throws Exception {
        new  Project(name, description, null,null, systemTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_CreationTimeNull() throws Exception {
        new Project(name, description, null,due, systemTime );
    }

    @Test (expected = IllegalArgumentException.class)
    public void NewProject_invalid_DueTimeNull() throws Exception {
        //DueTime mag niet null zijn
        new Project(name, description, create,null, systemTime);
    }
}