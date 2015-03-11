package be.swop.groep11.test.integration;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import be.swop.groep11.main.ui.EmptyListException;
import be.swop.groep11.main.ui.UserInterface;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;

/**
* Created by Ronald on 11/03/2015.
*/
abstract class EmptyTestUI implements UserInterface {
    private LocalDateTime now;
    public EmptyTestUI(LocalDateTime now) {
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
        return null;
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
