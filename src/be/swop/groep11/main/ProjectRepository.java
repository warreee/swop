package be.swop.groep11.main;


import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Houdt een lijst van projecten bij en heeft de verantwoordelijkheid om nieuwe projecten te maken en een project op
 * te vragen op basis van zijn identifier.
 */
public class ProjectRepository {

    /**
     * Lijst van projecten
     */
    private ArrayList<Project> projects = new ArrayList<>();

    /**
     * Geeft een immutable lijst terug van alle projecten.
     * @return Immutable list van de interne projecten lijst
     */
    public ImmutableList<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    /**
     * Maakt een nieuw project aan en voegt het toe aan de lijst van projecten.
     * @param name De naam van het project
     * @param description De beschrijving van het project
     * @param creationTime De aanmaaktijd van het project
     * @param duetime De eindtijd van het project
     * @param user De gebruiker die het project aanmaakt (de creator van het project)
     * @return Het aangemaakte project
     * @throws IllegalArgumentException De opgegeven parameters voor het project zijn ongeldig.
     */
    public Project addNewProject(String name, String description,LocalDateTime creationTime, LocalDateTime duetime, User user) throws IllegalArgumentException{
        Project proj = new Project(name, description, creationTime, duetime, user);
        this.projects.add(proj);
        return proj;
    }

    /**
     * Maakt een nieuw project aan en voegt het toe aan de lijst van projecten.
     * @param name De naam van het project
     * @param description De beschrijving van het project
     * @param creationTime De aanmaaktijd van het project
     * @param duetime De eindtijd van het project
     * @param user De gebruiker die het project aanmaakt (de creator van het project)
     */
    @Deprecated
    public void addProject(String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User user) {
        if (Project.isValidStartTimeEndTime(creationTime, duetime)){
            throw new IllegalArgumentException("Eindtijd kan niet voor starttijd liggen.");
        }
    }
}
