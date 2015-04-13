package be.swop.groep11.main;

import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskFailed;
import be.swop.groep11.main.task.TaskStatus;
import com.google.common.collect.ImmutableList;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Stelt een project voor met een naam, beschrijving, creation time, due time en een creator.
 * Een project heeft een status, bevat een lijst van taken en behoort tot een project repository.
 */
public class Project {



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

    /**
     * Geeft naam van het project terug.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name  De naam die dit project moet dragen.
     * @throws IllegalArgumentException
     *              Deze exception wordt gegooid indien !isValidProjectName(name)
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
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description   De omschrijving die dit project moet dragen.
     * @throws IllegalArgumentException
     *                      Deze exception wordt gegooid indien !isValidDescription(description)
     */
    public void setDescription(String description) throws IllegalArgumentException {
        if(!isValidDescription(description)){
            throw new IllegalArgumentException("Geen geldige omschrijving.");
        }
        this.description = description;
    }

    private String description;

    /**
     * Geeft de datum van aanmaken terug.
     */
    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }

    /**
     * Geeft de door de gebruiker gedefinieerde eind datum terug
     */
    public LocalDateTime getDueTime() {
        return this.dueTime;
    }

    /**
     * Set creationTime en dueTime voor het project
     *
     * @param creationTime  Het nieuwe aanmaak tijdstip
     * @param dueTime       De nieuwe eind datum
     * @throws IllegalArgumentException
     *                      Indien !isValidStartTimeEndTime(creationTime,dueTime)
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
     * Een project is beëindigd indien alle taken beëindigd zijn (ook beëindigd indien alternatieve taak beëindigd is)
     * en het project minstens 1 taak heeft.
     * @return Waar indien als alle taken geëindigd zijn
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
     * @return Een ImmutableList die alle taken bevat.
     */
    public ImmutableList<Task> getTasks() {
        return ImmutableList.copyOf(tasks.iterator());
    }

    /**
     * Geeft de User terug die dit project heeft aangemaakt.
     */
    public User getCreator() {
        return this.creator;
    }

    /**
     * Zet een User als creator van dit project.
     * @param creator   De nieuwe gebruiker.
     * @throws IllegalArgumentException
     *                  Gooi indien !isValidUser(creator)
     */
    public void setCreator(User creator) throws IllegalArgumentException{
        if(!isValidUser(creator)){
            throw new IllegalArgumentException("Geen geldige user.");
        }
        this.creator = creator;
    }

    /**
     * Geeft de ProjectRepository voor dit project.
     */
    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    /**
     *
     * @param projectRepository     De nieuwe projectRepository
     * @throws IllegalArgumentException
     *                              Throws indien !canHaveAsProjectRepository(projectRepository)
     */
    public void setProjectRepository(ProjectRepository projectRepository) throws IllegalArgumentException {
        if(!canHaveAsProjectRepository(projectRepository)){
            throw new IllegalArgumentException("Invalid repository.");
        }
        this.projectRepository = projectRepository;
    }

    /**
     * Geeft aan of de gegeven projectRepository een geldig repository is of niet.
     * @param projectRepository De nieuwe projectRepository
     * @return                  Waar indien this.projectRepository == null en projectRepository != null
     */
    public boolean canHaveAsProjectRepository(ProjectRepository projectRepository){
        if(this.projectRepository == null){
            return projectRepository != null;
        }else if(projectRepository != null){
            return false;
        }
        return false;
    }

    private ProjectRepository projectRepository;



    private User creator;

    /**
     * Interne ArrayList die alle taken bijhoud.
     */
    private ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Controleer of de gegeven projectNaam een geldige projectNaam is.
     * @param projectName   een projectNaam
     * @return              Waar indien projectName niet leeg en niet null.
     */
    public static boolean isValidProjectName(String projectName) {
        return projectName != null && !projectName.isEmpty();
    }
    /**
     * Controleer of de gegeven description een geldige description is.
     * @param description   Een description
     * @return              Waar indien description niet leeg en niet null.
     */
    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }

    /**
     * Controleer of de gegeven User een geldige User kan zijn.
     * @param user  De te controleren User.
     * @return      Waar indien user geïnitialiseerd.
     */
    public static boolean isValidUser(User user) {
        return user != null;
    }


    /**
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime   De eindtijd die gecontroleerd moet worden.
     * @return          Waar indien startTime, endTime niet null zijn. En bovendien startTime.isBefore(endTime)
     */
    public static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime){
        return startTime !=null && endTime != null && startTime.isBefore(endTime);
    }


    /**
     * Voegt een taak toe aan dit project.
     * @param description           Omschrijving van de taak
     * @param acceptableDeviation   Aanvaardbare afwijking (tijd) in percent
     * @param estimatedDuration     Schatting nodige tijd
     * @throws IllegalArgumentException
     *                              Gooi indien één of meerdere van de parameters niet geldig zijn.
     */
    public void addNewTask(String description, double acceptableDeviation, Duration estimatedDuration) throws IllegalArgumentException {
        Task task = new Task(description, estimatedDuration, acceptableDeviation, this);
        tasks.add(task);
    }

    /**
     * Controleer of de gegeven taak bij dit project hoort.
     * @param task  De taak die gecontroleerd moet worden.
     * @return      Waar indien dit project de gegeven task bevat.
     */
    public boolean hasTask(Task task){
        return tasks.contains(task);
    }

    /**
     * @return  Waar indien de geschatte eind datum van het project na de verwachte eind datum valt.
     */
    public boolean isOverTime(){
        return getEstimatedEndTime().isAfter(dueTime);
    }

    /**
     * Geeft een schatting voor de effectieve eind datum van het project.
     *
     * @return  De geschatte eind datum van het project, door het aantal nodige werkdagen te berekenen.
     *          Tel het aantal werkdagen (plus nodige weekends) bij de begindatum van het project op.
     */
    public LocalDateTime getEstimatedEndTime(){
        int HOURS_PER_DAY = 8;
        Duration max = Duration.ofDays(0);
        for(Task task :getTasks()){
            Set<Task> tasks = task.getDependingOnTasks();
            tasks.add(task);
            Duration temp = calculateTotalDuration(tasks);
            if(temp.compareTo(max) > 0){
                max = temp;
            }
        }
        long hours = max.toHours();
        long workDays = (long) Math.ceil(hours / HOURS_PER_DAY);

        LocalDateTime currentWorkingDay = creationTime;
        currentWorkingDay = currentWorkingDay.plusHours((int) hours % 8);
        if (currentWorkingDay.getHour() > 18) {
            currentWorkingDay = currentWorkingDay.plusDays(1);
            currentWorkingDay = currentWorkingDay.withHour(currentWorkingDay.getHour()-9);
        }
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
            currentWorkingDay = currentWorkingDay.plusDays(add);
            workDays--;

            }
        return currentWorkingDay;
        }

    /**
     * Berekend de totaal tijd van een set van taken.
     * De duur van elke taak wordt teruggegeven adv status, en zijn bijhorende implementatie van getDuration.
     * @param tasks
     * @return
     */
    private Duration calculateTotalDuration(Set<Task> tasks){
        LocalDateTime currentSystemTime = this.getProjectRepository().getTmSystem().getCurrentSystemTime(); // TODO: zo lang voor de tijd?
        Duration total = Duration.ofHours(0);
        for(Task task :tasks){
            TaskStatus status = task.getStatus();
            total.plus(status.getDuration(task, currentSystemTime)); // TODO: Hier wordt task opnieuw doorgegeven, kan dit wel de bedoeling zijn als we eerst status uit datzelfde task object halen
        }
        return total;
    }

    /**
     * Geeft een lijst van alle gefaalde taken van dit project.
     */
    public ImmutableList<Task> getFailedTasks(){
        List<Task> tasks = new ArrayList<Task>();
        for (Task task : this.getTasks()) {
            if (task.getStatus() instanceof TaskFailed) {
                tasks.add(task);
            }
        }
        return ImmutableList.copyOf(tasks);
    }

    /**
     * Geeft een kopie van dit project met een lijst van de kopieen van de taken van dit project.
     */
    protected Project copy() {
        try {
            Project clone = (Project) this.clone();
            clone.tasks = new ArrayList();
            for (Task task : this.tasks) {
                clone.tasks.add(task.copy());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            // dit zou niet mogen gebeuren
            return null;
        }
    }
}
