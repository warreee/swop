package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import javax.sound.midi.Sequencer;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by warreee on 23/02/15.
 */
public class Project {

    private String name, description;
    private LocalDateTime creationTime;
    private LocalDateTime dueTime;
    private User creator;
    private ProjectStatus projectStatus;
    private final int projectID;
    private ArrayList<Task> tasks = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////CONSTRUCTORS/////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Standaard constructor voor een nieuw project.
     * @param name
     * @param description
     * @param creationTime
     * @param duetime
     * @param creator
     * @throws IllegalArgumentException
     */
    public Project(int projectID, String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User creator) throws IllegalArgumentException{
        this.projectID = projectID;
        // Een project staat standaard op ONGOING
        ProjectStatus status = ProjectStatus.ONGOING;

        new Project(projectID, name, description, creationTime, duetime, creator, status);
    }

    /**
     *
     * @param name
     * @param description
     * @param creationTime
     * @param duetime
     * @param creator
     * @throws IllegalArgumentException
     */
    public Project(int projectID,String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User creator, ProjectStatus status) throws IllegalArgumentException{
        if(!isValidProjectID(projectID)){
            throw new IllegalArgumentException("Incorrect ProjectID: " + projectID);
        }
        this.projectID = projectID;
        setProjectName(name);
        setCreationAndDueTime(creationTime,duetime);
        setCreator(creator);
        setDescription(description);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////GETTERS AND SETTERS//////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addNewTask(String name, String description, double acceptableDeviation, LocalDateTime startTime, LocalDateTime endTime) {
        //TODO add new task
    }



    public void finish() {
        setProjectStatus(ProjectStatus.FINISHED);
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

    protected void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    protected void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setCreator(User creator) throws IllegalArgumentException{
        if(!isValidUser(creator)){
            throw new IllegalArgumentException("Geen geldige user.");
        }
        this.creator = creator;
    }

    /**
     *
     * @param projectName
     * @return Waar indien projectName niet leeg.
     */
    public static boolean isValidProjectName(String projectName) {
        return projectName != null &&!projectName.isEmpty();
    }

    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }

    public static boolean isValidUser(User user) {
        return user != null;
    }

    /**
     * Controleer of het gegeven projectID valid is.
     * @param ID projectID
     * @return waar indien ID positief is.
     */
    public static boolean isValidProjectID(int ID){
        if(ID < 0)
            return false;
        return true;
    }

    /**
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime De eindtijd die gecontroleerd moet worden.
     * @return false als startTime of endTime null is of als endTime voor startTime ligt.
     */
    public static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime){
        boolean result = startTime !=null && endTime != null && startTime.isBefore(endTime);
        return result;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getDueTime() {
        return this.dueTime;
    }

    public User getCreator() {
        return creator;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public int getProjectID() {
        return projectID;
    }

    /**
     * @return Een ImmutableList die alle taken in volgorde van de interne lijst bevat.
     */
    public ImmutableList<Task> getTasks() {
        return ImmutableList.copyOf(this.tasks);
    }


    public ProjectStatus getStatus() {
        return this.projectStatus;
    }

    public void addTask(String name, String description, double acceptableDeviation, LocalDateTime startTime, LocalDateTime endTime) {
    }
}
