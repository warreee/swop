package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by warreee on 23/02/15.
 */
public class Project {

    String name, description;
    LocalDateTime creationTime, dueTime;
    User creator;
    ProjectStatus status;
    ArrayList<Task> tasks = new ArrayList<>();

    public Project(String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User user){

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
        return false;
    }

    /**
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime De eindtijd die gecontroleerd moet worden.
     * @return false als startTime of endTime null is of als endTime voor startTime ligt.
     */
    public static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime){
        if(startTime == null || endTime == null){
            return false;
        }
        return startTime.isBefore(endTime);
    }

    public String getName() {
        return name;
    }

    /**
     * @param name De naam die dit project moet dragen.
     * @throws java.lang.IllegalArgumentException Deze exception wordt gegooid als het argument null is.
     */
    public void setName(String name) {
        if(name == null){
            throw new IllegalArgumentException("Projectnaam kan niet 'null' zijn.");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * @param description De omschrijving die dit project moet dragen.
     * @throws java.lang.IllegalArgumentException Deze exception wordt gegooid als het argument null is.
     */
    public void setDescription(String description) {
        if(description == null){
            throw new IllegalArgumentException("Omschrijving kan niet 'null' zijn.");
        }
        this.description = description;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime De starttijd die dit project moet dragen. Kan niet null zijn.
     * @throws java.lang.IllegalArgumentException Dit wordt gegooid indien
     */
    public void setCreationTime(LocalDateTime creationTime) {
        if(creationTime == null){
            throw new IllegalArgumentException("Starttijd kan niet 'null' zijn.");
        }
        if(getDueTime() != null && ! isValidStartTimeEndTime(creationTime, getDueTime())){
            throw new IllegalArgumentException("Starttijd kan niet voor eindtijd liggen.");
        }
        this.creationTime = creationTime;
    }

    public LocalDateTime getDueTime() {
        return this.dueTime;
    }

    // TODO: deze methode faalt altijd. Fix dit.
    public void setDueTime(LocalDateTime dueTime) {
        if(dueTime == null){
            throw new IllegalArgumentException("Eindtijd kan niet 'null' zijn.");
        }
        if(getCreationTime() != null && ! isValidStartTimeEndTime(getCreationTime(), dueTime)){
            throw new IllegalArgumentException("Eindtijd kan niet voor starttijd liggen.");
        }
        this.dueTime = dueTime;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    protected void setStatus(ProjectStatus status) {
        this.status = status;
    }

    protected void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
