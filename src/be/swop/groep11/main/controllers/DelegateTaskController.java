package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.exception.InterruptedAProcedureException;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.util.stream.Collectors;

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

    /**
     * Voert de usecase van delegateTask uit.
     */
    @Override
    public void delegateTask() {
        try {
            selectDelegateTask();
            selectDestinationBranchOffice();
            logonController.getBranchOffice().delegateTask(getDelegationTask(), getDestinationBranchOffice());
        } catch (CancelException | EmptyListException e) {
            getUserInterface().printException(e);
            throw new InterruptedAProcedureException(); // zorgt ervoor dat de status van de stack hersteld wordt
        }
    }

    Task delegationTask;


    private void setDelegationTask(Task delegatedTask) {
        this.delegationTask = delegatedTask;
    }

    private Task getDelegationTask() {
        return this.delegationTask;
    }

    /**
     * Geeft een lijst van TAKEN weer in de UI
     * En zorgt ervoor dat de gekozen taak als gedelegerde taak wordt gezet.
     */
    private void selectDelegateTask() {
        ImmutableList<Task> tasks = ImmutableList.copyOf(logonController.getBranchOffice().getUnplannedTasks());
        setDelegationTask(getUserInterface().selectTaskFromList(tasks));
    }

    BranchOffice destinationBranchOffice;

    private void setDestinationBranchOffice(BranchOffice branchOffice){
        this.destinationBranchOffice = branchOffice;
    }
    private BranchOffice getDestinationBranchOffice() {
        return this.destinationBranchOffice;
    }

    /**
     * Geeft een lijst van branchoffices weer in de UI
     * En zorgt ervoor dat de gekozen branchoffice als gedelegerde branchoffice wordt gezet.
     */
    private void selectDestinationBranchOffice() {
        ImmutableList<BranchOffice> branchOffices = ImmutableList.copyOf(
                company.getBranchOffices().stream() //Lambda om alle branchoffice te nemen zonder die branchoffice waaruit gedelegeerd wordt
                        .filter(branchOffice -> branchOffice != logonController.getBranchOffice()).collect(Collectors.toList()));
        setDestinationBranchOffice(getUserInterface().selectBranchOfficeFromList(branchOffices));
    }




}
