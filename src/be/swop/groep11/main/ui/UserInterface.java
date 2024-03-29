package be.swop.groep11.main.ui;

import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.User;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.task.Task;
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
     * Toont een lijst van taken.
     * @param tasks Lijst van taken
     */
    void showTaskList(ImmutableList<Task> tasks);

    /**
     * Toont de details van een project.
     */
    void showProjectDetails(Project project);

    /**
     * Toont de details van een taak.
     */
    void showTaskDetails(Task task,LocalDateTime dateTime);

    /**
     * Selecteert een branch office uit een lijst van branch offices.
     * @param branchOffices Lijst van branch offices
     * @return Geselecteerde branch office
     * @throws EmptyListException De lijst van branch offices is leeg.
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    BranchOffice selectBranchOfficeFromList(List<BranchOffice> branchOffices) throws EmptyListException, CancelException;

    /**
     * Selecteert een user uit een lijst van users.
     * @param users Lijst van users
     * @return Geselecteerde user
     * @throws EmptyListException De lijst van users is leeg.
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    default User selectEmployeeFromList(ImmutableList<User> users) throws EmptyListException, CancelException {
        printMessage("Selecteer een employee uit deze lijst:");
        return selectFromList(users, usr -> usr.getDescription());
    }

    /**
     * Selecteert een project uit een lijst van projecten.
     * @param projects Lijst van projecten
     * @return Geselecteerde project
     * @throws EmptyListException De lijst van projecten is leeg.
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    Project selectProjectFromList(List<Project> projects) throws EmptyListException, CancelException;

    /**
     * Selecteert een taak uit een lijst van taken.
     * @param tasks Lijst van taken
     * @return Geselecteerde taak
     * @throws EmptyListException De lijst van taken is leeg.
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    Task selectTaskFromList(List<Task> tasks) throws EmptyListException, CancelException;

    /**
     * Selecteerd een localdatetime uit een lijst van localdatetimes
     * @param dateTimes lijst van datetimes
     * @return Geselecteerde DateTime
     * @throws EmptyListException De lijst van datetimes is leeg
     * @throws CancelException de gebruiker heeft aangegeven dat hij de usecase wilt stoppen
     */
    LocalDateTime selectLocalDateTimeFromList(List<LocalDateTime> dateTimes) throws EmptyListException, CancelException;

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
     * Stelt een ja/nee vraag/dialog invoer van de gebruiker.
     * @param request       De vraag waarop de gebruiker ja of neen moet antwoorden
     * @return              Waar indien de gebruiker "y" (niet hoofdletter gevoelig) ingaf
     *                      Niet waar indien de gebruiker "n" (niet hoofdletter gevoelig) ingaf
     * @throws CancelException  Indien de gebruiker cancel ingaf i.p.v. "y/n"
     */
    boolean requestBoolean(String request)throws CancelException;

    /**
     * Toont een boodschap aan de gebruiker.
     */
    void printMessage(String message);

    /**
     * Toont een exception aan de gebruiker
     */
    void printException(Exception e);

    /**
     * Laat de UI een lijst mogelijke Actions weergeven.
     */
    default void showHelp() {
        showHelp(getControllerStack().getActiveController());
    }

    /**
     * Toont de beschikbare acties die uitgevoerd kunnen worden in een controller.
     * @param controller De controller
     */
    void showHelp(AbstractController controller);

    /**
     * Stopt de gebruikersinterface.
     */
    void wantsToExit();

    /**
     * Geeft de action behaviour mapping van de gebruikersinterface.
     */
    ControllerStack getControllerStack();

    /**
     * Zet de action behaviour mapping van de gebruikersinterface.
     */
    void setControllerStack(ControllerStack controllerStack);

    /**
     * Laat de gebruiker een element kiezen uit de gegeven lijst.
     * @param tList             De lijst waaruit men kan kiezen
     * @param listEntryPrinter  De manier waarop ieder element wordt voorgesteld
     * @param <T>               Het type van het geslecteerde element
     * @return                  Geeft het element dat de gebruiker selecteerde.
     * @throws CancelException  indien gebruiker Command.CANCEL ingeeft als invoer. Of indien de gegeven lijst leeg is.
     */
    <T> T selectFromList(List<T> tList, Function<T, String> listEntryPrinter)throws CancelException;

    /**
     * Laat de gebruiker meerdere elementen kiezen uit de gegeven lijst.
     * @param request          De request string die een omschrijving van de invoer bevat
     * @param list             De lijst van elementen waaruit men kan kiezen
     * @param preselectedList  De voorgeselecteerde elementen
     * @param maxSelected      Het maximum aantal elementen dat de gebruiker mag selecteren
     * @param exactAmount      True als de gebruiker steeds exact maxSelected elementen moet selecteren
     * @param listEntryPrinter De manier waarop ieder element wordt voorgesteld
     * @param <T>              Het type van het geslecteerde element
     * @return                 Geeft een lijst van de elementen die de gebruiker selecteerde.
     * @throws CancelException indien gebruiker Command.CANCEL ingeeft als invoer. Of indien de gegeven lijst leeg is.
     */
    <T> List<T> selectMultipleFromList(String request,List<T> list,List<T> preselectedList,int maxSelected,boolean exactAmount,Function<T,String> listEntryPrinter) throws EmptyListException,CancelException;

    /**
     * Laat de gebruiker meerdere elementen kiezen uit de gegeven lijst.
     * @param request          De request string die een omschrijving van de invoer bevat
     * @param list             De lijst van elementen waaruit men kan kiezen
     * @param preselectedList  De voorgeselecteerde elementen
     * @param listEntryPrinter De manier waarop ieder element wordt voorgesteld
     * @param <T>              Het type van het geslecteerde element
     * @return                 Geeft een lijst van de elementen die de gebruiker selecteerde.
     * @throws CancelException indien gebruiker Command.CANCEL ingeeft als invoer. Of indien de gegeven lijst leeg is.
     */
    default <T> List<T> selectMultipleFromList(String request,List<T> list,List<T> preselectedList,Function<T,String> listEntryPrinter) throws EmptyListException,CancelException {
        return selectMultipleFromList(request,list, preselectedList, list.size(),false,listEntryPrinter);
    }

    /**
     * Vraagt een een getal tussen de gegeven Minimum & Maximum waarde aan de gebruiker.
     * Toont ook de beschrijving van de verwachte invoer.
     * @param request Beschrijving van de verwachte invoer
     * @param min De minimum waarde
     * @param max De maximum waarde
     * @return Een getal tussen min & max, (inclusief grenzen)
     * @throws CancelException De gebruiker heeft aangegeven dat hij de use case wil stoppen
     */
    int requestNumberBetween(String request, int min, int max) throws CancelException;

    /**
     * Geeft de Function<Task,String> die een voorstelling voor een taak kan genereren.
     */
    Function<Task,String> getTaskPrinter();

    /**
     * Methode om de gegeven lijst weer te geven via waarbij de voorstelling
     * van ieder element wordt opgebouwd a.d.h.v. de gegeven listEntryPrinter.
     * @param list              De weer te gegeven lijst.
     * @param listEntryPrinter  De Function<T,String> die de voorstelling van een element uit list opbouwd.
     * @param <T>               De klasse van een element uit list
     */
    <T> void showList(List<T> list,Function<T,String> listEntryPrinter);
}
