package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by warreee on 23/02/15.
 */
public class Project {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////CONSTRUCTORS/////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Standaard constructor voor een nieuw project.
     * @param name          Naam van het project
     * @param description   Omschrijving van het project
     * @param creationTime  Creation time voor het project
     * @param dueTime       Due time voor het project
     * @param creator       Wie het project heeft aangemaakt
     * @throws IllegalArgumentException
     *                      | !isValidDescription(description)
     *                      | !isValidProjectName(name)
     *                      | !isValidStartTimeEndTime(creationTime,dueTime)
     *                      | !isValidUser(creator)
     *                      | !isValidProjectID(projectID)
     */
    public Project(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime, User creator) throws IllegalArgumentException{
        this(name, description, creationTime, dueTime, creator, ProjectStatus.ONGOING);
    }

    private Project(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime, User creator, ProjectStatus projectStatus) throws IllegalArgumentException{
        setProjectName(name);
        setCreationAndDueTime(creationTime, dueTime);
        setCreator(creator);
        setDescription(description);
        setProjectStatus(projectStatus);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////GETTERS AND SETTERS//////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return this.name;
    }
    /**
     * @param name De naam die dit project moet dragen.
     * @throws java.lang.IllegalArgumentException Deze exception wordt gegooid als het argument null is.
     */
    public void setProjectName(String name) throws IllegalArgumentException {
        if(!isValidProjectName(name)){
            throw new IllegalArgumentException("Projectnaam kan niet 'null' zijn.");
        }
        this.name = name;
    }

    private String name;

    /**
     * Geeft de beschrijving terug van dit project.
     * @return de beschrijving van dit project.
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * @param description De omschrijving die dit project moet dragen.
     * @throws java.lang.IllegalArgumentException Deze exception wordt gegooid als het argument null is.
     */
    public void setDescription(String description) throws IllegalArgumentException {
        if(!isValidDescription(description)){
            throw new IllegalArgumentException("Geen geldige omschrijving.");
        }
        this.description = description;
    }

    private String description;

    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }
    public LocalDateTime getDueTime() {
        return this.dueTime;
    }
    /**
     * Set creationTime en dueTime voor het project
     * @param creationTime
     * @param dueTime
     * @throws IllegalArgumentException
     */
    public void setCreationAndDueTime(LocalDateTime creationTime,LocalDateTime dueTime) throws IllegalArgumentException{
        if(!isValidStartTimeEndTime(creationTime,dueTime)){
            throw new IllegalArgumentException("Geen geldige start- en/of eindtijdstip.");
        }
        this.creationTime = creationTime;
        this.dueTime = dueTime;
    }

    private LocalDateTime creationTime;
    private LocalDateTime dueTime;

    public ProjectStatus getProjectStatus() {
        return this.projectStatus;
    }
    private void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    private ProjectStatus projectStatus;

    /**
     * @return Een ImmutableList die alle taken in volgorde van de interne lijst bevat.
     */
    public ImmutableList<Task> getTasks() {
        return ImmutableList.copyOf(tasks.iterator());
    }

    public User getCreator() {
        return this.creator;
    }
    public void setCreator(User creator) throws IllegalArgumentException{
        if(!isValidUser(creator)){
            throw new IllegalArgumentException("Geen geldige user.");
        }
        this.creator = creator;
    }

    private User creator;

    /**
     * Interne hashSet die alle taken bijhoud.
     */
    private HashSet<Task> tasks = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////METHODES//////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Probeert om dit project te beëindigen.
     */
    public void finish() {
        // TODO: exception gooien als het niet kan gefinished worden?
        for (Task t : tasks) {
            if (t.getStatus().equals(TaskStatus.AVAILABLE) || t.getStatus().equals(TaskStatus.UNAVAILABLE)) {
                // Er is een taak die nog uitgevoerd moet worden. De projectStatus kan dus niet finished zijn.
                return;
            }
        }
        setProjectStatus(ProjectStatus.FINISHED);
    }

    /**
     *
     * @param projectName
     * @return Waar indien projectName niet leeg.
     */
    public static boolean isValidProjectName(String projectName) {
        return projectName != null && !projectName.isEmpty();
    }

    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }

    public static boolean isValidUser(User user) {
        return user != null;
    }

    /**
     * Controleer of het gegeven projectID valid is.
     * @param projectID projectID
     * @return waar indien ID positief is.
     */
    public static boolean isValidProjectID(int projectID){
        return projectID >= 0;
    }

    /**
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime De eindtijd die gecontroleerd moet worden.
     * @return false als startTime of endTime null is of als endTime voor startTime ligt.
     */
    public static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime){
        return startTime !=null && endTime != null && startTime.isBefore(endTime);
    }


    /**
     * Voegt een taak toe aan dit project.
     * @param description           Omschrijving van de taak
     * @param acceptableDeviation   Aanvaardbare afwijking (tijd) in percent
     * @param estimatedDuration     Schatting nodige tijd
     * @return                      TaskID van de net aangemaakte Task
     */
    // TODO: exception gooien als nieuwe taak faalt.
    public void addNewTask(String description, double acceptableDeviation, Duration estimatedDuration) {
        Task task = new Task(description, estimatedDuration, acceptableDeviation, this);
        tasks.add(task);
    }
}
