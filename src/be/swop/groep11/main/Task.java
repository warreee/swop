package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Stelt een taak voor met een beschrijving, starttijd, eindtijd, verwachte duur en een aanvaardbare marge.
 * Een taak behoort tot 1 project en heeft een lijst van dependency constraints.
 */
public class Task {

    /**
     * Constructor om een nieuwe taak te maken.
     *
     * @param taskID                De taskID van de nieuwe taak
     * @param description           De omschrijving van de nieuwe taak
     * @param estimatedDuration     De verwachte duur van de nieuwe taak
     * @param acceptableDeviation   De aanvaardbare marge van de nieuwe taak
     * @param project               Het project waarbij de nieuwe taak hoort
     * @throws java.lang.IllegalArgumentException
     *                              Ongeldige taskID, ongeldige verwachte duur, ongeldige aanvaardbare marge
     *                                            of ongeldig project
     */
    public Task(int taskID, String description, Duration estimatedDuration, double acceptableDeviation, Project project) throws IllegalArgumentException {
        this(taskID,description,estimatedDuration,acceptableDeviation,project,null);
    }

    /**
     * Constructor om een nieuwe taak te maken.
     *
     *
     * @param taskID                De taskID van de nieuwe taak
     * @param description           De omschrijving van de nieuwe taak
     * @param estimatedDuration     De verwachte duur van de nieuwe taak
     * @param acceptableDeviation   De aanvaardbare marge van de nieuwe taak
     * @param project               Het project waarbij de nieuwe taak hoort
     * @param dependencies          De taskIDs van taken waarvan deze nieuwe taak afhankelijk is.
     * @throws java.lang.IllegalArgumentException
     *                              Ongeldige taskID, ongeldige verwachte duur, ongeldige aanvaardbare marge
     *                                            of ongeldig project
     */
    public Task(int taskID, String description, Duration estimatedDuration, double acceptableDeviation, Project project,int[] dependencies)throws IllegalArgumentException {
        if(! isValidTaskID(taskID)){
            throw new IllegalArgumentException("Ongeldig taak ID: " + taskID);
        }
        this.taskID = taskID;

        if (! canHaveAsProject(project))
            throw new IllegalArgumentException("Ongeldig project");

        setDescription(description);
        setEstimatedDuration(estimatedDuration);
        setAcceptableDeviation(acceptableDeviation);
        this.dependencyConstraints = new HashSet<>();
        this.project = project;

        addnewDependencies(dependencies);
        //TODO set init TaskStatus
    }



    /**
     * Beschrijving
     */
    private String description;

    /**
     * Geeft de beschrijving van de taak.
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
        return estimatedDuration;
    }
    /**
     * Wijzigt de verwachte duur van de taak.
     * @throws java.lang.IllegalArgumentException De verwachte duur is null of de verwachteduur is negatief.
     */
    public void setEstimatedDuration(Duration estimatedDuration) throws IllegalArgumentException {
        if (estimatedDuration == null)
            throw new IllegalArgumentException("Verwachte duur mag niet null zijn");
        if (estimatedDuration.isNegative()){
            throw new IllegalArgumentException("Verwachte duur kan niet negatief zijn.");
        }
        this.estimatedDuration = estimatedDuration;
    }

    /**
     * Aanvaardbare marge
     */
    private double acceptableDeviation;
    /**
     * Geeft de aanvaardbare marge van de taak.
     */
    public double getAcceptableDeviation() {
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
     * @return true alss de vaardbare marge geldig is (i.e. acceptableDeviation >= 0)
     */
    public static boolean isValidAcceptableDeviation(double acceptableDeviation) {
        return acceptableDeviation >= 0 && acceptableDeviation <= 1;
    }

    /**
     * Starttijd en eindtijd
     */
    private LocalDateTime startTime, endTime;

    /**
     * Geeft de starttijd van de taak of null als de taak geen starttijd heeft.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }
    /**
     * Wijzigt de starttijd van de taak.
     * @throws java.lang.IllegalArgumentException De starttijd is niet geldig.
     */
    public void setStartTime(LocalDateTime startTime) throws IllegalArgumentException {
        if (! isValidStartTime(startTime, getEndTime()))
            throw new IllegalArgumentException("Ongeldige starttijd");
        this.startTime = startTime;
    }
    /**
     * Geeft de eindtijd van de taak of null als de taak geen eindtijd heeft.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }
    /**
     * Wijzigt de eindtijd van de taak.
     * @param endTime De nieuwe eindtijd van deze taak
     * @throws java.lang.IllegalArgumentException De eindtijd is niet geldig.
     */
    public void setEndTime(LocalDateTime endTime /*, LocalDateTime systemTime */) throws IllegalArgumentException {
        if (! isValidEndTime(getStartTime(), endTime /*, systemTime */))
            throw new IllegalArgumentException("Ongeldige eindtijd");
        this.endTime = endTime;
        /* if (getStatus() != TaskStatus.FINISHED && status != TaskStatus.FINISHED)
            this.setStatus(TaskStatus.FINISHED); */
    }

    /**
     * Controleert of een starttijd geldig is voor een bepaalde eindtijd.
     * @param startTime De starttijd om te controleren
     * @param endTime De eindttijd
     * @return true alss startTime null is, of endTime null is, of startTime voor endTime ligt
     */
    // TODO: nu kan een starttijd null zijn. Is dit wel gewenst?
    public static boolean isValidStartTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime == null || endTime == null || startTime.isBefore(endTime);
    }

    /**
     * Controleert of een starttijd geldig is voor een bepaalde eindtijd en de huidige systeemtijd.
     * @param startTime De starttijd
     * @param endTime De eindttijd om te controleren
     * @return true als endTime == null,
     *    <br> true als startTime == null of startTime ligt voor endTime,
     *    <br> false in andere gevallen
     */
    public static boolean isValidEndTime(LocalDateTime startTime, LocalDateTime endTime /*, LocalDateTime systemTime */) {
        return endTime == null
                || ( (startTime == null || startTime.isBefore(endTime)) /* && (endTime.isBefore(systemTime)) */ );
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
     * @return          Waar als het project geldig is (i.e. als het project nog niet geëindigd is) en
     *                  nog associatie bestaat tussen het project en een taak met hetzelfde taskID als deze taak.
     */
    public boolean canHaveAsProject(Project project) {
        return project != null &&
                project.getProjectStatus() != ProjectStatus.FINISHED &&
                !project.hasTask(this.taskID);
    }

    private final int taskID;

    /**
     * Geeft aan de gegeven taskID een mogelijk id kan zijn voor een taak.
     */
    public static boolean isValidTaskID(int taskID){
        return taskID >= 0;
    }

    /**
     * Geeft de taskID van deze taak.
     */
    public int getTaskID() {
        return this.taskID;
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
     * @param dependingOn De taak waarvan deze taak moet afhangen
     */
    public void addNewDependencyConstraint(Task dependingOn) {
        dependencyConstraints.add(new DependencyConstraint(this, dependingOn));
    }
    /**
     * Verwijdert een dependency constraint van deze taak.
     * @param dependingOn De dependingOn taak van de te verwijderen dependency constraint
     */
    public void removeDependencyConstraint(Task dependingOn) {
        dependencyConstraints.remove(new DependencyConstraint(this, dependingOn));
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

    private void addnewDependencies(int[] dependencies) {
        //TODO implementeer, gegeven een reeks taskIDs waarvan deze taak afhankelijk van is
    }

    /**
     * Geeft een lijst van alle taken die van deze taak afhankelijk zijn.
     */
    public Set<Task> getDependentTasks() {
        // TODO: implement method
        return null;
    }

    /**
     * Status van de taak
     */
    private TaskStatus status;

    /**
     * Geeft de status van deze taak.
     */
    // TODO: kan een methode van buiten nu de interne var niet wijzigen?
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Wijzigt de status van deze taak.
     * @param status De nieuwe status
     * @throws java.lang.IllegalArgumentException De nieuwe status is ongeldig voor deze taak
     */
    public void setStatus(TaskStatus status) throws IllegalArgumentException {
        if (! TaskStatus.isValidNewStatus(status, this))
            throw new IllegalArgumentException("Ongeldige status");
        this.status = status;
    }

    // TODO gewoon om de testen van tasktest te kunnen runnen, moet nog worden aangepast
    public void setStatus2(TaskStatus status) throws IllegalArgumentException {

        this.status = status;
    }

    /**
     * Alternatieve taal (kan null zijn)
     */
    private Task alternativeTask;

    /**
     * Geeft de alternatieve taak van deze taak,
     * of null indien deze taak geen alternatieve taak heeft
     */
    // TODO: kan een methode van buiten nu de interne var niet wijzigen?
    public Task getAlternativeTask() {
        return alternativeTask;
    }

    /**
     * Wjizigt de alternatieve taak van deze taak.
     * @throws java.lang.Exception alternativeTask kan niet als alternatieve taak voor deze taak gezet worden.
     */
    public void setAlternativeTask(Task alternativeTask) throws Exception {
        if (! canSetAlternativeTask(this, alternativeTask))
            throw new Exception("Kan de alternatieve taak niet wijzigen");
        this.alternativeTask = alternativeTask;
    }

    /**
     * Controleert of een taak als alternatieve taak voor een gegeven taak kan ingesteld worden.
     * @param task De gegeven taak
     * @param alternativeTask De alternatieve taak
     * @return true alss (task is gefaald en alternativeTask != task) of (alternativeTask == null)
     */
    public static boolean canSetAlternativeTask(Task task, Task alternativeTask) {
        return task != null
                && ( (task.getStatus() == TaskStatus.FAILED && task != alternativeTask) || (alternativeTask == null) );
    }

    /**
     * Geeft de status waarmee de taak geëindigd is: vroeg / op tijd / te laat
     * @return -2 als de taak nog niet geëindigd is,
     *     <br>-1 als de taak vroeg geëindigd is,
     *     <br> 0 als de taak op tijd geëindigd is,
     *     <br> 1 als de taak te laat geëindigd is.
     */
    public int getFinishedStatus() {
        if (getStatus() != TaskStatus.FINISHED)
            return -2;

        long durationInSeconds = getDuration().getSeconds();
        long estimatedDurationInSeconds = this.getEstimatedDuration().getSeconds();

        if (durationInSeconds < (1-acceptableDeviation)*estimatedDurationInSeconds)
            return -1;
        else if (durationInSeconds > (1+acceptableDeviation)*estimatedDurationInSeconds)
            return 1;
        else
            return 0;
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
        if (getStartTime() == null || getEndTime() == null)
            return null;
        return Duration.between(getStartTime(),getEndTime());
    }

    /**
     * Maakt de afhankelijke taken (if any) van deze taak beschikbaar.
     */
    //TODO: een taak kan van meerdere taken afhangen. Dit is hier niet geïmplementeerd.
    private void makeDependentTasksAvailable() {
        Set<Task> dependentTasks = this.getDependentTasks();
        for (Task task : dependentTasks)
            task.setStatus(TaskStatus.AVAILABLE);
    }

}