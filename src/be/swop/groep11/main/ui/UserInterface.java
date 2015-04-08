package be.swop.groep11.main.ui;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;

/**
 * Interface die de methodes bevat die een gebruikersinterface moet implementeren
 */
public interface UserInterface {

    /**
     * Toont een lijst van projecten.
     * @param projects Lijst van projecten
     */
    public void showProjectList(ImmutableList<Project> projects);

    /**
     * Selecteert een project uit een lijst van projecten.
     * @param projects Lijst van projecten
     * @return Nummer van geselecteerde project in lijst
     * @throws be.swop.groep11.main.ui.EmptyListException De lijst van projecten is leeg.
     * @throws be.swop.groep11.main.ui.commands.CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException;

    /**
     * Vraagt een invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    public String requestString(String request) throws CancelException;

    /**
     * Vraagt een geheel getal als invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    public int requestNumber(String request) throws CancelException;

    /**
     * Vraagt een double als invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    public double requestDouble(String request) throws CancelException;

    /**
     * Vraagt een datum en tijd als invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    public LocalDateTime requestDatum(String request) throws CancelException;

    /**
     * Toont een boodschap aan de gebruiker.
     */
    public void printMessage(String message);

    /**
     * Toont een exception aan de gebruiker
     */
    public void printException(Exception e);

    /**
     * Toont een lijst van taken.
     * @param tasks Lijst van taken
     */
    public void showTaskList(ImmutableList<Task> tasks);

    /**
     * Selecteert een taak uit een lijst van taken.
     * @param tasks Lijst van taken
     * @return Nummer van geselecteerde taak in lijst
     * @throws be.swop.groep11.main.ui.EmptyListException De lijst van taken is leeg.
     * @throws be.swop.groep11.main.ui.commands.CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException;

    /**
     * Toont de details van een project.
     */
    public void showProjectDetails(Project project);

    /**
     * Toont de details van een taak.
     */
    public void showTaskDetails(Task task);


}
