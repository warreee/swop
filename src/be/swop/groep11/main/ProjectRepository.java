package be.swop.groep11.main;


import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Houdt een lijst van projecten bij en heeft de verantwoordelijkheid om nieuwe projecten te maken en een project op
 * te vragen op basis van zijn identifier.
 */
public class ProjectRepository {

    private Set<Project> projects;

    public ProjectRepository() {
        this.projects = new HashSet<>();
    }

    /**
     * Geeft een immutable lijst terug van alle projecten.
     * @return Immutable list van de interne projecten lijst
     */
    public ImmutableList<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    /**
     * Maakt een nieuw project aan en voegt het toe aan de Set van projecten.
     *
     * @param name          De naam van het project
     * @param description   De beschrijving van het project
     * @param creationTime  De aanmaaktijd van het project
     * @param duetime       De eindtijd van het project
     * @param user          De gebruiker die het project aanmaakt (de creator van het project)*
     * @throws IllegalArgumentException De opgegeven parameters voor het project zijn ongeldig.
     */
    public void addNewProject(String name, String description,LocalDateTime creationTime, LocalDateTime duetime, User user) throws IllegalArgumentException{
        Project proj = new Project(name, description, creationTime, duetime, user);
        projects.add(proj);
    }

}
