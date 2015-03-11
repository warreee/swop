package be.swop.groep11.test.integration;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.TaskMan;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class AdvanceTimeScenarioTest {

    private TaskMan taskMan;
    private LocalDateTime now;


    @Before
    public void setUp() throws Exception {
        now = LocalDateTime.now();
        taskMan = new TaskMan(now);

    }

    @Test
    public void updateTime_valid() throws Exception {
        testUI ui = new testUI(now) { };

        //Step 1: User has indicated he wants to modify the system time.
        //Stap 2 & 3:
        LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd");
        //step 4
        taskMan.updateSystemTime(newSystemTime);
    }

    @Test (expected = IllegalArgumentException.class)
    public void updateTime_invalid_beforeCurrentSystemTime() throws Exception {
        testUI ui = new testUI(now) {
            @Override
            public LocalDateTime requestDatum(String request) throws CancelException {
                return now.minusDays(1);
            }
        };
        //Step 1: User has indicated he wants to modify the system time.
        //Stap 2 & 3:
        LocalDateTime newSystemTime = ui.requestDatum("Nieuwe systeemtijd");
        //step 4
        taskMan.updateSystemTime(newSystemTime);
    }
    abstract class testUI implements UserInterface{
        private LocalDateTime now;
        public testUI( LocalDateTime now) {
            this.now = now;
        }

        @Override
        public void showProjectList(ImmutableList<Project> projects) {

        }

        @Override
        public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
            return null;
        }

        @Override
        public String requestString(String request) throws CancelException {
            return null;
        }

        @Override
        public int requestNumber(String request) throws CancelException {
            return 0;
        }

        @Override
        public double requestDouble(String request) throws CancelException {
            return 0;
        }

        @Override
        public LocalDateTime requestDatum(String request) throws CancelException {
            return now.plusDays(1);
        }

        @Override
        public void printMessage(String message) {

        }

        @Override
        public void printException(Exception e) {

        }

        @Override
        public void showTaskList(ImmutableList<Task> tasks) {

        }

        @Override
        public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
            return null;
        }

        @Override
        public void showProjectDetails(Project project) {

        }

        @Override
        public void showTaskDetails(Task task) {

        }

    }
}

