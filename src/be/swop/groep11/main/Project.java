package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by warreee on 23/02/15.
 */
public class Project {

    private String name, description;
    private LocalDateTime creationTime, dueTime;
    private User creator;
    private ProjectStatus status;
    private ArrayList<Task> tasks = new ArrayList<>();

    //TODO: Het aanmaken van eigen Exception definities? (Zoals bij OGP)

    /**
     *
     * @param name
     * @param description
     * @param creationTime
     * @param duetime
     * @param creator
     * @throws IllegalArgumentException
     */
    public Project(String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User creator) throws IllegalArgumentException{
        setName(name);
        setCreationAndDueTime(creationTime,duetime);
//        setCreationTime(creationTime);
//        setDueTime(duetime);
        setCreator(creator);
        setDescription(description);
    }


    public void addTask(String name, String description, double acceptableDeviation, LocalDateTime startTime, LocalDateTime endTime) {
    }

    /**
     * @return Een ImmutableList die alle taken in volgorde van de interne lijst bevat.
     */
    public ImmutableList<Task> getTasks() {
        return ImmutableList.copyOf(this.tasks);
    }

    public void finish() {
    }

    public static boolean isValidName(String validName) {
        return !validName.isEmpty();
    }
    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }

    public static boolean isValidUser(User user) {
        return user != null;
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

    /**
     * @param name De naam die dit project moet dragen.
     * @throws java.lang.IllegalArgumentException Deze exception wordt gegooid als het argument null is.
     */
    public void setName(String name) throws IllegalArgumentException {
        if(!isValidName(name)){
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
     * @param creationTime De starttijd die dit project moet dragen. Kan niet null zijn.
     * @throws java.lang.IllegalArgumentException Dit wordt gegooid indien
     */
    @Deprecated
    public void setCreationTime(LocalDateTime creationTime) throws IllegalArgumentException {
        if(creationTime == null){
            throw new IllegalArgumentException("Starttijd kan niet 'null' zijn.");
        }
        if(getDueTime() != null && ! isValidStartTimeEndTime(creationTime, getDueTime())){
            throw new IllegalArgumentException("Starttijd kan niet voor eindtijd liggen.");
        }
        this.creationTime = creationTime;
    }

    // TODO: deze methode faalt altijd. Fix dit.
    @Deprecated
    public void setDueTime(LocalDateTime dueTime) throws IllegalArgumentException {
        if(dueTime == null){
            throw new IllegalArgumentException("Eindtijd kan niet 'null' zijn.");
        }
        if(getCreationTime() != null && ! isValidStartTimeEndTime(getCreationTime(), dueTime)){
            throw new IllegalArgumentException("Eindtijd kan niet voor starttijd liggen.");
        }
        this.dueTime = dueTime;
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

    protected void setStatus(ProjectStatus status) {
        this.status = status;
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

    public ProjectStatus getStatus() {
        return status;
    }
}
