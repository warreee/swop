package be.swop.groep11.test.integration;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.ProjectRepository;
import be.swop.groep11.main.User;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;

public class UseCaseShowProjects {

    User user;
    LocalDateTime create, due;
    String name, description;


    ProjectRepository projectRepository = new ProjectRepository();

    @Before
    public void setUp() throws Exception {
        user = new User("ROOT");

        create = LocalDateTime.now();
        due = LocalDateTime.now().plusSeconds(3600);
        name = "name";
        description = "description";


        projectRepository.addNewProject(name, description, create, due, user);
    }

    @Test
    public void test() throws Exception {

        projectRepository.getProjectByID(0);
        System.out.println("test");
    }
}