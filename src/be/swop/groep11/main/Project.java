package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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
     * @param duetime       Due time voor het project
     * @param creator       Wie het project heeft aangemaakt
     * @throws IllegalArgumentException
     *                      | !isValidDescription(description)
     *                      | !isValidProjectName(name)
     *                      | !isValidStartTimeEndTime(creationTime,dueTime)
     *                      | !isValidUser(creator)
     *                      | !isValidProjectID(projectID)
     */
    public Project(int projectID, String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User creator) throws IllegalArgumentException{
        this(projectID, name, description, creationTime, duetime, creator, ProjectStatus.ONGOING);
    }

    private Project(int projectID,String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User creator, ProjectStatus projectStatus) throws IllegalArgumentException{
        if(!isValidProjectID(projectID)){
            throw new IllegalArgumentException("Incorrect ProjectID: " + projectID);
        }
        this.projectID = projectID;
        setProjectName(name);
        setCreationAndDueTime(creationTime,duetime);
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
        return ImmutableList.copyOf(this.tasks);
    }
    // TODO waarom protected?
    // TODO Task moet nog als een Map<Integer,Task> geïmplementeerd worden, zodat men getTaskByID(ID) kan uitvoeren.
    protected void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    private ArrayList<Task> tasks = new ArrayList<>();

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

    public int getProjectID() {
        return this.projectID;
    }

    private final int projectID;

    /**
     * Interne variabele die de volgende id van een nieuwe task bevat.
     */
    private int nextTaskId = 0;

    /**
     * Interne hashmap die alle taken bijhoud.
     */
    private HashMap<Integer, Task> newTasks = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////METHODES//////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Probeert om dit project te beëindigen.
     */
    public void finish() {
        for(Task t: newTasks.values()){
            if(t.getStatus().equals(TaskStatus.AVAILABLE) || t.getStatus().equals(TaskStatus.UNAVAILABLE)){
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
     * @param ID projectID
     * @return waar indien ID positief is.
     */
    public static boolean isValidProjectID(int ID){
        return ID < 0;
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
    public int addNewTask(String description, double acceptableDeviation, Duration estimatedDuration) {
        Task task = new Task(description, estimatedDuration, acceptableDeviation, this);
        tasks.add(task);
        newTasks.put(nextTaskId, task);
        return nextTaskId++;
    }
        /**
         * @param taskID
         * @return Geeft de taak geassocieerd met taskID terug.
         * @throws java.lang.IllegalArgumentException
         *          | Indien geen taak geassocieerd met taskID.
         */
    public Task getTaskByID(int taskID){
        if(!newTasks.containsKey(taskID)){
            throw new IllegalArgumentException("De taak met de opgegeven ID bestaat niet.");
        }
        return newTasks.get(taskID);
    }
}
