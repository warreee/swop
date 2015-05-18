package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.exception.InterruptedAProcedureException;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

/**
 * Klasse voor de controller van de usecase delegatetask.
 */
public class DelegateTaskController  extends AbstractController {

    private Company company;
    private LogonController logonController;

    public DelegateTaskController(UserInterface userInterface, Company company, LogonController logonController) {
        super(userInterface);
        this.company = company;
        this.logonController = logonController;
    }

    @Override
    public void delegateTask() {
        try {
            selectDelegateTask();
            selectDestinationBranchoffice();
        } catch (CancelException | EmptyListException e) {
            getUserInterface().printException(e);
            throw new InterruptedAProcedureException(); // zorgt ervoor dat de status van de stack hersteld wordt
        }
    }

    Task delegatedTask;

    private void setDelegateTask(Task delegatedTask) {
        this.delegatedTask = delegatedTask;
    }

    private void selectDelegateTask() {
        ImmutableList<Task> tasks = ImmutableList.copyOf(logonController.getBranchOffice().getUnplannedTasks());
        setDelegateTask(getUserInterface().selectTaskFromList(tasks));
    }

    private void selectDestinationBranchoffice() {

    }

}
