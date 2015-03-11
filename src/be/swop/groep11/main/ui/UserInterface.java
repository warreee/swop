package be.swop.groep11.main.ui;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Interface die de methodes bevat die een gebruikersinterface moet implementeren
 */
public interface UserInterface {

    /**
     * Toont een lijst van projecten.
     * @param projects Lijst van projecten
     */
//    overbodig?
    public void showProjectList(ImmutableList<Project> projects);

    /**
     * Selecteert een project uit een lijst van projecten.
     * @param projects Lijst van projecten
     * @return Nummer van geselecteerde project in lijst
     * @throws java.io.IOException Fout met kiezen van project
     */
    public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException;

//    /**
//     * Toont een formulier en geeft de ingevulde waardes van de velden terug.
//     * @param fields Namen van de velden van het formulier
//     * @return Een map waarbij de veldnamen aan de ingevulde waarden gekoppeld worden: (veldnaam,waarde)
//     */
//    public Map<String,String> showForm(String... fields);

    //Voorstel user input
    public String requestString(String request)throws IllegalInputException,CancelException;
    public int requestNumber(String request) throws IllegalInputException,CancelException;
    public LocalDateTime requestDatum(String request) throws DateTimeParseException;
    public void printMessage(String message);
    public void printException(Exception e);

    /**
     * Toont een lijst van taken.
     * @param tasks Lijst van taken
     */
    //overbodig?
    public void showTaskList(ImmutableList<Task> tasks);

    /**
     * Selecteert een taak uit een lijst van taken.
     * @param tasks Lijst van taken
     * @return Nummer van geselecteerde taak in lijst
     */
    public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException;

    /**
     * Toont de details van een project.
     */
    public void showProjectDetails(Project project);

    /**
     * Toont de details van een taak.
     */
    public void showTaskDetails(Task task);


}
