package be.swop.groep11.main.ui;

import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.ui.commands.CancelException;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * Interface die de methodes bevat die een gebruikersinterface moet implementeren
 */
public interface UserInterface {

    /**
     * Toont een lijst van projecten.
     * @param projects Lijst van projecten
     */
    void showProjectList(ImmutableList<Project> projects);

    /**
     * Selecteert een project uit een lijst van projecten.
     * @param projects Lijst van projecten
     * @return Nummer van geselecteerde project in lijst
     * @throws be.swop.groep11.main.ui.EmptyListException De lijst van projecten is leeg.
     * @throws be.swop.groep11.main.ui.commands.CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException; //TODO Kan weg

    /**
     * Vraagt een invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    String requestString(String request) throws CancelException;

    /**
     * Vraagt een geheel getal als invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    int requestNumber(String request) throws CancelException;

    /**
     * Vraagt een double als invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    double requestDouble(String request) throws CancelException;

    /**
     * Vraagt een datum en tijd als invoer van de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @return De invoer van de gebruiker
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    LocalDateTime requestDatum(String request) throws CancelException;

    /**
     * Toont een boodschap aan de gebruiker.
     */
    void printMessage(String message);

    /**
     * Toont een exception aan de gebruiker
     */
    void printException(Exception e);

    /**
     * Toont een lijst van taken.
     * @param tasks Lijst van taken
     */
    void showTaskList(ImmutableList<Task> tasks);

    /**
     * Selecteert een taak uit een lijst van taken.
     * @param tasks Lijst van taken
     * @return Nummer van geselecteerde taak in lijst
     * @throws be.swop.groep11.main.ui.EmptyListException De lijst van taken is leeg.
     * @throws be.swop.groep11.main.ui.commands.CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException; //TODO Kan weg

    /**
     * Toont de details van een project.
     */
    void showProjectDetails(Project project);

    /**
     * Toont de details van een taak.
     */
    void showTaskDetails(Task task);

    void addControllerToStack(AbstractController abstractController);
    void removeControllerFromStack(AbstractController abstractController);
    void showHelp(AbstractController controller);

    /**
     * Laat de gebruiker een element kiezen uit de gegeven lijst.
     * @param tList             De lijst waaruit men kan kiezen
     * @param listEntryPrinter  De manier waarop ieder element wordt voorgesteld
     * @param <T>               Het type van het geslecteerde element
     * @return                  Geeft het element dat de gebruiker selecteerde.
     * @throws CancelException  indien gebruiker Command.CANCEL ingeeft als invoer. Of indien de gegeven lijst leeg is.
     */
    <T> T selectFromList(List<T> tList, Function<T, String> listEntryPrinter)throws CancelException;

    <T> T requestUserInput(String request,userInput<T> userInput) throws CancelException;

    /**
     * Vraag een getal aan de user tussen een min en max waarde.
     *
     * @param userInput de functie waarmee de invoer aan de gebruiker gevraagd wordt.
     * @param min       De minimum toegelaten waarde (inclusief)
     * @param max       De maximum toegelaten waarde (inclusief)
     * @param <T>       Het Type van het gevraagde getal tussen min en max.
     * @return          Een getal van Type <T> dat uit [min,max] komt.
     * @throws CancelException  gooi indien de gebruiker het Command.CANCEL in geeft.
     */
    <T extends Number & Comparable<T>> T numberBetween(userInput<T> userInput,T min,T max)throws CancelException;

}
