package be.swop.groep11.main;


import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Houdt een lijst van projecten bij en heeft de verantwoordelijkheid om nieuwe projecten te maken en een project op
 * te vragen op basis van zijn identifier.
 */
public class ProjectRepository {

    private int nextProjectID;
    private HashMap<Integer,Project> projectsMap;

    public ProjectRepository() {
        this.nextProjectID = 0;
        this.projectsMap = new HashMap<>();
    }

    /**
     * Geeft een immutable lijst terug van alle projecten.
     * @return Immutable list van de interne projecten lijst
     */
    public ImmutableList<Project> getProjects() {
        return ImmutableList.copyOf(projectsMap.values());
    }

    /**
     * Maakt een nieuw project aan en voegt het toe aan de Map van projecten.
     * Met als sleutel het projectID en als waarde het project.
     *
     * @param name De naam van het project
     * @param description De beschrijving van het project
     * @param creationTime De aanmaaktijd van het project
     * @param duetime De eindtijd van het project
     * @param user De gebruiker die het project aanmaakt (de creator van het project)
     * @return int ProjectID of new project
     * @throws IllegalArgumentException De opgegeven parameters voor het project zijn ongeldig.
     */
    public int addNewProject(String name, String description,LocalDateTime creationTime, LocalDateTime duetime, User user) throws IllegalArgumentException{
        Project proj = new Project(nextProjectID,name, description, creationTime, duetime, user);
        addToProjectsMap(proj.getProjectID(),proj);
        int result = nextProjectID;
        nextProjectID += 1;
        return result;
    }

    /**
     * Voeg het project toe aan de Map projectsMap
     * @param projectID projectID als sleutel voor de Map projectsMap
     * @param project   project als waarde geassocieerd met de sleutel projectID
     */
    private void addToProjectsMap(int projectID,Project project){
        projectsMap.put(projectID,project);
    }

    /**
     * @param projectID
     * @return Geeft het project geassocieerd met projectID terug.
     * @throws java.lang.IllegalArgumentException
     *          | Indien geen project geassocieerd met projectID
     */
    public Project getProjectByID(int projectID) throws IllegalArgumentException{
        Project result = projectsMap.get(projectID);
        if(result == null){
            throw new IllegalArgumentException("Geen project gevonden voor het ID: " + projectID);
        }
        return result;
    }
}
