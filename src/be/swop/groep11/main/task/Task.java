package be.swop.groep11.main.task;

import be.swop.groep11.main.DependencyConstraint;
import be.swop.groep11.main.Project;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

/**
 * Stelt een taak voor met een beschrijving, starttijd, eindtijd, verwachte duur en een aanvaardbare marge.
 * Een taak behoort tot 1 project en heeft een lijst van dependency constraints.
 */
public class Task {

    //TODO: methodes isOverTime en getOverTimePercentage en isUnacceptablyOverTime
    /**
     * Constructor om een nieuwe taak te maken.
     *
     *
     * @param description           De omschrijving van de nieuwe taak
     * @param estimatedDuration     De verwachte duur van de nieuwe taak
     * @param acceptableDeviation   De aanvaardbare marge van de nieuwe taak
     * @param project               Het project waarbij de nieuwe taak hoort
     * @throws java.lang.IllegalArgumentException
     *                              Ongeldige taskID, ongeldige verwachte duur, ongeldige aanvaardbare marge
     *                                            of ongeldig project
     */
    public Task(String description, Duration estimatedDuration, double acceptableDeviation, Project project) throws IllegalArgumentException {
        if (! canHaveAsProject(project)) {
            throw new IllegalArgumentException("Ongeldig project");
        }
        setStatus(TaskStatus.AVAILABLE);
        setDescription(description);
        setEstimatedDuration(estimatedDuration);
        setAcceptableDeviation(acceptableDeviation);
        this.dependencyConstraints = new HashSet<>();
        this.project = project;
    }

    /**
     * Beschrijving
     */
    private String description;

    /**
     * Geeft de beschrijving van deze taak.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Wijzigt de beschrijving van de taak.
     * @throws java.lang.IllegalArgumentException De beschrijving is niet geldig.
     */
    public void setDescription(String description) throws IllegalArgumentException {
        if (! isValidDescription(description))
            throw new IllegalArgumentException("Ongeldige beschrijving");
        this.description = description;
    }

    /**
     * Controleert of een beschrijving geldig is.
     * @return true alss de beschrijving niet null en niet leeg is
     */
    public static boolean isValidDescription(String description) {
        return description != null && !description.isEmpty();
    }

    /**
     * Verwachte duur
     */
    private Duration estimatedDuration;

    /**
     * Geeft de verwachte duur van de taak.
     */
    public Duration getEstimatedDuration() {
        // Duration is immutable.
        return estimatedDuration;
    }

    /**
     * Wijzigt de verwachte duur van de taak.
     * @throws java.lang.IllegalArgumentException De verwachte duur is null of de verwachteduur is negatief.
     */
    public void setEstimatedDuration(Duration estimatedDuration) throws IllegalArgumentException {
        if (!isValidEstimatedDuration(estimatedDuration))
            throw new IllegalArgumentException("Ongeldige tijdsduur.");
        this.estimatedDuration = estimatedDuration;
    }

    /**
     * Controleert of de gegeven verwachte tijdsduur geldig is.
     *
     * @param estimatedDuration De te controleren tijdsduur.
     * @return                  Waar indien de tijdsduur niet null, niet negatief en niet gelijk aan 0 is.
     */
    public static boolean isValidEstimatedDuration(Duration estimatedDuration){
        return estimatedDuration != null && !estimatedDuration.isNegative() && !estimatedDuration.isZero();
    }

    /**
     * Aanvaardbare marge
     */
    private double acceptableDeviation;

    /**
     * Geeft de aanvaardbare marge van de taak.
     */
    public double getAcceptableDeviation() {
        // Doubles zijn immutable.
        return acceptableDeviation;
    }

    /**
     * Wijzigt de aanvaardbare marge van de taak.
     * @throws java.lang.IllegalArgumentException De aanvaardbare marge is niet geldig.
     */
    public void setAcceptableDeviation(double acceptableDeviation) throws IllegalArgumentException {
        if (! isValidAcceptableDeviation(acceptableDeviation))
            throw new IllegalArgumentException("Ongeldige aanvaardbare marge");
        this.acceptableDeviation = acceptableDeviation;
    }

    /**
     * Controleert of een aanvaardbare marge geldig is voor deze taak.
     *
     * @return  Waar indien de aanvaardbare marge geldig is (i.e. acceptableDeviation >= 0)
     */
    public static boolean isValidAcceptableDeviation(double acceptableDeviation) {
        return acceptableDeviation >= 0;
    }

    /**
     * Starttijd en eindtijd
     */
    private LocalDateTime startTime, endTime;

    /**
     * Geeft de starttijd van de taak of null als de taak geen starttijd heeft.
     */
    public LocalDateTime getStartTime() {
        // LocaleDateTime is immutable.
        return startTime;
    }

    /**
     * Wijzigt de starttijd van de taak.
     * @throws java.lang.IllegalArgumentException De starttijd is niet geldig.
     */
    public void setStartTime(LocalDateTime startTime) throws IllegalArgumentException {
        if (! canHaveAsStartTime(startTime))
            throw new IllegalArgumentException("Ongeldige starttijd");
        this.startTime = startTime;
    }

    /**
     * Geeft de eindtijd van de taak of null als de taak geen eindtijd heeft.
     */
    public LocalDateTime getEndTime() {
        // LocalDateTime is immutable.
        return endTime;
    }

    /**
     * Wijzigt de eindtijd van de taak.
     * Dit kan alleen gezet worden als er al een starttijd gezet.
     * De eindtijd moet uiteraard na de starttijd liggen.
     *
     * @param endTime De nieuwe eindtijd van deze taak
     * @throws java.lang.IllegalArgumentException De eindtijd is niet geldig.
     */
    public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
        if (! canHaveAsEndTime(endTime))
            throw new IllegalArgumentException("Ongeldige eindtijd");
        this.endTime = endTime;
        /* if (getStatus() != TaskStatus.FINISHED && status != TaskStatus.FINISHED)
            this.setStatus(TaskStatus.FINISHED); */
    }

    /**
     * Controleer of de gegeven start tijd geldig is voor deze taak.
     *
     * @param startTime De start tijd om te controleren
     * @return          Waar indien de status van deze taak AVAILABLE is, geen huidige endTime en de gegeven startTime niet null is.
     *                  Waar indien de status van deze taak AVAILABLE is, een huidige endTime heeft en de gegeven startTime voor de endTime valt.
     *
     */
    public boolean canHaveAsStartTime(LocalDateTime startTime) {
        if(this.getStatus() == TaskStatus.FAILED || this.getStatus() == TaskStatus.FINISHED){
            return false;
        }
        if(startTime == null) {
            return false;
        }
        if(hasEndTime() && startTime.isAfter(endTime)){
            return false;
        }
        Set<Task> tasks = getDependingOnTasks();
        for(Task task: tasks){
            if(task.getEndTime() == null){
                continue;
            }
            if(startTime.isBefore(task.getEndTime())){
                return false; // De gegeven starttijd ligt voor een eindtijd van een
            }
        }
        return true;
    }


    /**
     * Controleer of de gegeven eind tijd een geldig tijdstip is voor deze taak..
     *
     * @param endTime   De eindtijd om te controleren
     * @return          Waar indien de status van deze taak AVAILABLE is, een huidige starttijd heeft,
     *                  en de gegeven endTime na de start tijd van deze taak valt.
     *                  Waar indien de status van deze taak AVAILABLE is en een huidige starttijd heeft,
     *                  en de gegeven endTime niet null is en de huidige endTime
     */
    public boolean canHaveAsEndTime(LocalDateTime endTime) {
        boolean result = false;
        if(this.getStatus() == TaskStatus.FAILED || this.getStatus() == TaskStatus.FINISHED){
            result = false;
        }else if(!hasStartTime()){
            result = false;
        } else if(endTime != null){
            result = startTime.isBefore(endTime);
        }
        return result;
    }

    /**
     * @return      Waar indien deze taak een start tijd heeft.
     */
    public boolean hasStartTime(){
        return this.startTime != null;
    }

    /**
     * @return      Waar indien deze taak een eind tijd heeft.
     */
    public boolean hasEndTime(){
        return this.endTime != null;
    }

    /**
     * Project waarbij de taak hoort
     */
    private final Project project;

    /**
     * Geeft het project van de taak.
     */
    public Project getProject() {
        return project;
    }


    /**
     * Controleert of een gegeven project een geldig project is voor deze taak.
     *
     * @param project   Het gegeven project
     * @return          Waar als er nog geen associatie bestaat tussen het project deze taak.
     */
    public boolean canHaveAsProject(Project project) {
        return project != null;
    }

    /**
     * Set van alle dependency constraints van deze taak
     */
    private Set<DependencyConstraint> dependencyConstraints;

    /**
     * Geeft een immutable list van alle dependency constraints van de taak.
     */
    public ImmutableList<DependencyConstraint> getDependencyConstraints() {
        return ImmutableList.copyOf(dependencyConstraints);
    }

    /**
     * Voegt een dependency constraint toe voor deze taak.
     * Wijzigt ook de status van deze taak naar UNAVAILABLE indien nodig.
     * @param dependingOn De taak waarvan deze taak moet afhangen
     */
    public void addNewDependencyConstraint(Task dependingOn) {
        dependencyConstraints.add(new DependencyConstraint(this, dependingOn));
        if (TaskStatus.isValidNewStatus(TaskStatus.UNAVAILABLE,this)) {
            this.setStatus(TaskStatus.UNAVAILABLE);
        }
    }

    /**
     * Geeft een set van alle taken waarvan deze taak (recursief) afhankelijk is.
     */
    public Set<Task> getDependingOnTasks() {
        HashSet<Task> dependingOnTasks = new HashSet<>();
        for (DependencyConstraint dependencyConstraint : this.dependencyConstraints) {
            // voeg de dependingOn taak van de dependency constraint toe
            dependingOnTasks.add(dependencyConstraint.getDependingOn());
            // voeg alle taken toe waarvan de dependingOn taak afhankelijk is
            dependingOnTasks.addAll(dependencyConstraint.getDependingOn().getDependingOnTasks());
        }
        return dependingOnTasks;
    }

    /**
     * Geeft een set van alle taken die van deze taak afhankelijk zijn.
     */
    public Set<Task> getDependentTasks() {
        HashSet<Task> dependentTasks = new HashSet<>();
        Project project = this.getProject();
        ImmutableList<Task> tasks = project.getTasks();
        for (Task task : tasks) {
            if (task.getDependingOnTasks().contains(this))
                dependentTasks.add(task);
        }
        return dependentTasks;
    }

    /**
     * Status van de taak
     */
    private TaskStatus status;

    private TaskStatus2 status2;
    /**
     * Geeft de status van deze taak.
     */
    private TaskStatus getStatus() {
        return status;
    }

    public void execute() {
        status2.execute(this);
    }

    public void fail() {
        status2.fail(this);
    }

    public void finish() {
        status2.finish(this);
    }

    public void makeAvailable() {
        status2.makeAvailable(this);
    }

    public void makeUnAvailable() {
        status2.makeUnavailable(this);
    }

    /**
     * Wijzigt de status van deze taak.
     * @param status De nieuwe status
     * @throws java.lang.IllegalArgumentException De nieuwe status is ongeldig voor deze taak
     */
    protected void setStatus(TaskStatus status) throws IllegalArgumentException {
        if (! TaskStatus.isValidNewStatus(status, this))
            throw new IllegalArgumentException("Ongeldige status");
        this.status = status;
        if (status == TaskStatus.FINISHED) {
            this.makeDependentTasksAvailable();
        }
    }

    /**
     * Wijzigt de status van deze taak.
     * @param status De nieuwe status
     * @throws java.lang.IllegalArgumentException Kan de status alleen op FINISHED of FAILED zetten.
     */
    public void setNewStatus(TaskStatus status) throws IllegalArgumentException{
        if (legalTransition(status))
            throw new IllegalArgumentException("Kan status alleen op FINISHED of FAILED zetten");
        setStatus(status);
    }

    /**
     * Kijkt na of het een publieke toegestane overgang is
     * @param status De nieuwe status
     * @return true als het een legale overgang is, false in het andere geval
     */
    private boolean legalTransition(TaskStatus status) {
        return status != TaskStatus.FAILED &&
                status != TaskStatus.FINISHED &&
                status != TaskStatus.EXECUTING;
    }


    /**
     * Alternatieve taal (kan null zijn)
     */
    private Task alternativeTask;

    /**
     * Geeft de alternatieve taak van deze taak,
     * of null indien deze taak geen alternatieve taak heeft
     */
    public Task getAlternativeTask() {
        return alternativeTask;
    }

    /**
     * Wjizigt de alternatieve taak van deze taak.
     * Zorgt ervoor dat de dependencies van deze taak overgebracht worden naar de alternatieve taak.
     * @throws java.lang.Exception alternativeTask kan niet als alternatieve taak voor deze taak gezet worden.
     */
    public void setAlternativeTask(Task alternativeTask) throws IllegalArgumentException {
        if (! canSetAlternativeTask(this, alternativeTask))
            throw new IllegalArgumentException("Kan de alternatieve taak niet wijzigen");
        for (Task task : getDependentTasks()) {
            for (DependencyConstraint dependencyConstraint : task.getDependencyConstraints()) {
                if (dependencyConstraint.getDependingOn() == this ) {
                    dependencyConstraint.setDependingOn(alternativeTask);
                }
            }
        }
        this.alternativeTask = alternativeTask;
    }

    /**
     * Controleert of een taak als alternatieve taak voor een gegeven taak kan ingesteld worden.
     * @param task De gegeven taak
     * @param alternativeTask De alternatieve taak
     * @return true alss (alternativeTask == null)
     *                   of (task is gefaald en alternativeTask != task en alternativeTask hangt niet af van task)
     */
    public static boolean canSetAlternativeTask(Task task, Task alternativeTask) {
        return task != null
                && ( (task.getStatus() == TaskStatus.FAILED && task != alternativeTask && (! alternativeTask.dependsOn(task))) || (alternativeTask == null) );
    }

    private boolean dependsOn(Task other) {
        return this.getDependingOnTasks().contains(other);
    }

    /**
     * Controleert of deze taak geëindigd is of de eventuele alternatieve taak geëindigd is.
     * @return true als (deze taak is geëindigd) of (alternatieve taak != null en alternatieve taak is geëindigd)
     */
    public boolean getAlternativeFinished() {
        if (this.getStatus() == TaskStatus.FINISHED)
            return true;
        if (this.getAlternativeTask() != null)
            return getAlternativeTask().getAlternativeFinished();
        return false;
    }

    protected void setStatus(TaskStatus2 status) {
        this.status2 = status;
    }

    public static enum FinishedStatus {
        EARLY,
        ONTIME,
        OVERDUE,
        NOTFINISHED;
    }

    /**
     * Geeft de status waarmee de taak geëindigd is: vroeg / op tijd / te laat
     * @return FinishedStatus.NOTFINISHED als de taak nog niet geëindigd is,
     *     <br>FinishedStatus.EARLY als de taak vroeg geëindigd is,
     *     <br>FinishedStatus.ONTIME als de taak op tijd geëindigd is,
     *     <br>FinishedStatus.OVERDUE als de taak te laat geëindigd is.
     */
    public FinishedStatus getFinishedStatus() {
        if (getStatus() != TaskStatus.FINISHED)
            return FinishedStatus.NOTFINISHED;

        long durationInSeconds = getDuration().getSeconds();
        long estimatedDurationInSeconds = this.getEstimatedDuration().getSeconds();

        if (durationInSeconds < (1-acceptableDeviation)*estimatedDurationInSeconds)
            return FinishedStatus.EARLY;
        else if (durationInSeconds > (1+acceptableDeviation)*estimatedDurationInSeconds)
            return FinishedStatus.OVERDUE;
        else
            return FinishedStatus.ONTIME; //Tussen aanvaardbare afwijking
    }

    /**
     * Geeft de delay van deze taak.
     * @return De delay van deze taak,
     *         of null indien de duur van deze taak null is.
     *         (De delay van een taak is steeds positief)
     */
    public Duration getDelay() {
        if (getDuration() == null)
            return null;

        Duration delay = getDuration().minus(getEstimatedDuration());
        if (delay.isNegative()) // geen negatieve delays!
            return Duration.ofSeconds(0);
        return delay;
    }

    /**
     * Geeft de duur van deze taak, indien de starttijd en eindtijd niet null zijn.
     * @return De duur van deze taak,
     *         of null als de starttijd of eindtijd van deze taak null is.
     */
    public Duration getDuration() {
        // We nemen als duration de echte tijd die gepasseerd is.
        if (getStartTime() == null || getEndTime() == null)
            return null;


        return Duration.between(getStartTime(),getEndTime());
    }

    /**
     * Controleert of deze taak momenteel over tijd is.
     */
    public boolean isOverTime() {
        double percent = getOverTimePercentage();
        return !(percent <= getAcceptableDeviation());
    }

    /**
     * Controleert of deze taak momenteel onacceptable over tijd is. (Meer dan de accepteerbare variatie.)
     */
    public boolean isUnacceptablyOverTime(){
        double percent = getOverTimePercentage();
        return percent > getAcceptableDeviation();
    }

    /**
     * Berekent hoeveel een project overtijd is zonder rekening te houden met de acceptable deviation.
     * @return 0.1 staat voor 10%, 0.2 staat voor 20%.
     */
    public double getOverTimePercentage(){
        LocalDateTime systemTime = getProject().getProjectRepository().getTMSystem().getCurrentSystemTime();
        if(!hasStartTime()){
            return 0.0; // Project is nog niet gestart. Dus over time is 0.
        }
        double minutes;
        if(hasStartTime() && hasEndTime()) {
            minutes = startTime.until(endTime, ChronoUnit.MINUTES);
        } else {
            minutes = startTime.until(systemTime, ChronoUnit.MINUTES);
        }
        double dur = getEstimatedDuration().toMinutes();
        double percent = (minutes / dur) - 1.0;
        if(percent <= 0.0){
            return 0.0;
        } else {
            return percent;
        }
    }

    /**
     * Maakt de afhankelijke taken (if any) van deze taak AVAILABLE,
     * indien dat mogelijk is.
     * Als deze taak een alternatieve taak voor een andere taak is.
     */
    private void makeDependentTasksAvailable() {
        for (Task task : this.getDependentTasks()) {
            if (TaskStatus.isValidNewStatus(TaskStatus.AVAILABLE, task)) {
                task.setStatus(TaskStatus.AVAILABLE);
            }
        }
    }

}