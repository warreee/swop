package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.exception.InterruptedAProcedureException;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Klasse voor de controller van de usecase delegatetask.
 */
public class DelegateTaskController extends AbstractController {

    private Company company;
    private LogonController logonController;

    /**
     * Constructor om een nieuwe delegatedTaskController te maken.
     * @param userInterface Gebruikersinterface
     * @param company Company waarvan deze controller gebruik maakt.
     * @param logonController De logon controller om de gebruiker login te controlleren.
     */
    public DelegateTaskController(UserInterface userInterface, Company company, LogonController logonController) {
        super(userInterface);
        this.company = company;
        this.logonController = logonController;
    }

    /**
     * Laat de gebruiker een branch office kiezen om de taak naar te delegeren.
     * En voert de delegatie onmiddellijk uit voor de branch offices.
     */
    @Override
    public void delegateTask() {
        this.selectDelegatedTo();
        this.performDelegations();
    }

    /**
     * Laat de gebruiker een branch office kiezen om de taak naar te delegeren.
     */
    @Override
    public void selectDelegatedTo() {
        try {
            selectDelegateTask();
            selectDestinationBranchOffice();
            this.delegationsFrom.put(getDelegationTask(), getDelegationTask().getDelegatedTo());
            this.delegationsTo.put(getDelegationTask(), getDestinationBranchOffice());
            getDelegationTask().setDelegatedTo(getDestinationBranchOffice());
            getUserInterface().printMessage("Taak gedelegeerd naar: " + destinationBranchOffice.getName());
        } catch (CancelException | EmptyListException | IllegalArgumentException e) {
            getUserInterface().printException(e);
            throw new InterruptedAProcedureException();
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
        setDelegationTask(getUserInterface().selectTaskFromList(logonController.getBranchOffice().getUnplannedTasks()));
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
        List<BranchOffice> branchOffices =
                company.getBranchOffices().stream() //Lambda om alle branchoffice te nemen zonder die branchoffice waaruit gedelegeerd wordt
                        .filter(branchOffice -> branchOffice != logonController.getBranchOffice()).collect(Collectors.toList());
        setDestinationBranchOffice(getUserInterface().selectBranchOfficeFromList(branchOffices));
    }


    /**
     * Voert de delegaties uit voor de branch offices.
     */
    public void performDelegations() {
        try {
            for (Task task : delegationsFrom.keySet()) {
                task.setDelegatedTo(delegationsFrom.get(task));
                delegationsFrom.get(task).delegateTask(task, delegationsTo.get(task));
            }
            this.delegationsFrom = new HashMap<>();
            this.delegationsTo = new HashMap<>();
        } catch (IllegalArgumentException e) {
            getUserInterface().printException(e);
            throw new InterruptedAProcedureException();
        }
    }

    /**
     * Zorgt ervoor dat de delegaties niet uitgevoerd worden.
     */
    public void clearDelegations() {
        for (Task task : delegationsFrom.keySet()) {
            task.setDelegatedTo(delegationsFrom.get(task));
        }
        this.delegationsFrom = new HashMap<>();
        this.delegationsTo = new HashMap<>();
    }

    private Map<Task,BranchOffice> delegationsFrom = new HashMap<>();
    private Map<Task,BranchOffice> delegationsTo = new HashMap<>();



}
