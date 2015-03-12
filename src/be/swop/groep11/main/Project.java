package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

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
    public Project(String name, String description, LocalDateTime creationTime, LocalDateTime dueTime, User creator, ProjectRepository repo) throws IllegalArgumentException{
        setProjectName(name);
        setCreationAndDueTime(creationTime, dueTime);
        setCreator(creator);
        setDescription(description);
        setProjectRepository(repo);
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

    /**
     * Geeft de status van dit project: ONGOING of FINISHED.
     * Een project is beëindigd als alle taken beëindigd zijn (ook beëindigd als alternatieve taak beëindigd is)
     * en het project minstens 1 taak heeft.
     * @return true als alle taken beëindigd zijn
     */
    public ProjectStatus getProjectStatus() {
        if (getTasks().size() == 0) {
            return ProjectStatus.ONGOING;
        }
        for (Task task : this.getTasks()) {
            if (! task.getAlternativeFinished()) {
                return ProjectStatus.ONGOING;
            }
        }
        return ProjectStatus.FINISHED;
    }

    /**
     * @return Een ImmutableList die alle taken bevat. Niet persee in volgorde van toevoegen.
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

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    private ProjectRepository projectRepository;



    private User creator;

    /**
     * Interne ArrayList die alle taken bijhoud.
     */
    private ArrayList<Task> tasks = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////METHODES//////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
     * @throws java.lang.IllegalArgumentException
     *                              Ongeldige parameters voor taak
     */
    public void addNewTask(String description, double acceptableDeviation, Duration estimatedDuration) throws IllegalArgumentException {
        Task task = new Task(description, estimatedDuration, acceptableDeviation, this);
        tasks.add(task);
    }

    /**
     * Controleert of de gegeven taak bij dit project hoort.
     * @param task De taak die gecontroleert moet worden.
     * @return true asa dit project deze task bevat, anders false.
     */
    public boolean hasTask(Task task){
        return tasks.contains(task);
    }

    public boolean isOverTime(){
        return getEstimatedEndTime()
    }

    /**
     * Geeft een schatting voor de effectieve eind datum van het project.
     *
     * @param currentSystemTime De huidige systeemtijd nodig bij het berekenen van totale tijdsduur gespendeerd aan de taken.
     * @return
     */
    public LocalDateTime getEstimatedEndTime(LocalDateTime currentSystemTime){
        int HOURS_PER_DAY = 8;
        Duration max = Duration.ofDays(0);
        for(Task task :getTasks()){
            Set<Task> dependingOnTasks = task.getDependingOnTasks();
            Duration temp = calculateTotalDuration(dependingOnTasks,currentSystemTime);
            if(temp.compareTo(max) > 0){
                max = temp;
            }
        }
        long hours = max.toHours();
        long workDays = (long)(hours / HOURS_PER_DAY)+1;

        LocalDateTime currentWorkingDay = creationTime;
        while(workDays > 0){
            DayOfWeek currentDay = currentWorkingDay.getDayOfWeek();
            long add = 1;
            switch (currentDay) {
//                case MONDAY:
//                    add = 1;
//                    break;
                case SATURDAY:
                    add = 3;
                    break;
                case SUNDAY:
                    add = 2;
                    break;
            }
            currentWorkingDay.plusDays(add);
            workDays--;

            }
        return currentWorkingDay;
        }

    private Duration calculateTotalDuration(Set<Task> tasks,LocalDateTime currentSystemTime){
        Duration total = Duration.ofHours(0);
        for(Task task :tasks){
            TaskStatus status = task.getStatus();
            if(status == TaskStatus.AVAILABLE ){
                Duration add = task.isOverTime(currentSystemTime) ? Duration.between(task.getStartTime(),currentSystemTime) : task.getEstimatedDuration()  ;
                total.plus(add);
            }
            else if(status == TaskStatus.UNAVAILABLE){
                total.plus(task.getEstimatedDuration());
            }
            else if(status == TaskStatus.FINISHED || status == TaskStatus.FAILED){
                total.plus(task.getDuration());
            }
        }
        return total;
    }
}
