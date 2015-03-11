package be.swop.groep11.test.integration;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by Ronald on 11/03/2015.
 */
public class ShowProjectsScenarioTest {


    private LocalDateTime now;

    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
    }

    @Test
    public void showProjects_validTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {
            @Override
            public void showProjectList(ImmutableList<Project> projects) {
                super.showProjectList(projects);
            }

            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
                return super.selectTaskFromList(tasks);
            }

            @Override
            public void showTaskDetails(Task task) {
                super.showTaskDetails(task);
            }

            @Override
            public void showProjectDetails(Project project) {
                super.showProjectDetails(project);
            }
        };
        showProject(ui);
    }


    @Test (expected = CancelException.class)
    public void showProjects_CancelSelectProjectTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {


            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
                return super.selectTaskFromList(tasks);
            }
            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                throw new CancelException("Cancel");
            }

        };
        showProject(ui);

    }

    @Test (expected = CancelException.class)
    public void showProjects_CancelSelectTaskTest() throws Exception {
        EmptyTestUI ui = new EmptyTestUI(now) {


            @Override
            public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
                throw new CancelException("Cancel");
            }
            @Override
            public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
                return null;
            }

        };
        showProject(ui);

    }

    private void showProject(EmptyTestUI ui){
        Project[] prjs = {};
        ImmutableList<Project> projects = ImmutableList.copyOf(prjs);
        //Step 2 & 3
        Project project = ui.selectProjectFromList(projects);
        //Step 4
        ui.showProjectDetails(project);

        Task[] tsks = {};
        ImmutableList<Task> tasks = ImmutableList.copyOf(tsks);
        //Step 5
        Task task = ui.selectTaskFromList(tasks);
        //Step 6
        ui.showTaskDetails(task);
    }
}
