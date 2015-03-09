package be.swop.groep11.main.ui;

import be.swop.groep11.main.Project;
import be.swop.groep11.main.Task;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Map;

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
     */
    public int selectProjectFromList(ImmutableList<Project> projects) throws IOException;

    /**
     * Toont een formulier en geeft de ingevulde waardes van de velden terug.
     * @param fields Namen van de velden van het formulier
     * @return Een map waarbij de veldnamen aan de ingevulde waarden gekoppeld worden: (veldnaam,waarde)
     */
    public Map<String,String> showForm(String... fields);

    /**
     * Toont een lijst van taken.
     * @param tasks Lijst van taken
     */
    public void showTaskList(ImmutableList<Task> tasks);

    /**
     * Selecteert een taak uit een lijst van taken.
     * @param tasks Lijst van taken
     * @return Nummer van geselecteerde taak in lijst
     */
    public int selectTaskFromList(ImmutableList<Task> tasks);

    /**
     * Toont de details van een project.
     */
    public void showProjectDetails(Project project);

    /**
     * Toont de details van een taak.
     */
    public void showTaskDetails(Task task);

}
