package be.swop.groep11.main.task;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.exception.IllegalStateTransitionException;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.util.Observer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Stelt een taak voor met een beschrijving, starttijd, eindtijd, verwachte duur en een aanvaardbare marge.
 * Een taak heeft ook een dependency graph en een lijst van resource requirements.
 */
public class Task{

    /**
     * Constructor om een nieuwe taak te maken.
     *
     * @param description           De omschrijving van de nieuwe taak
     * @param estimatedDuration     De verwachte duur van de nieuwe taak
     * @param acceptableDeviation   De aanvaardbare marge van de nieuwe taak
     * @param dependencyGraph       De dependency graph die de nieuwe taak moet gebruiken
     * @param requirementList       De lijst van resource requirements voor de nieuwe taak
     * @throws IllegalArgumentException Ongeldige beschrijving, ongeldige verwachte duur, ongeldige aanvaardbare marge,
     *                                            ongeldige requirement lijst of ongeldig project
     */
    public Task(String description, Duration estimatedDuration, double acceptableDeviation, DependencyGraph dependencyGraph, IRequirementList requirementList, Project project) throws IllegalArgumentException {
        if (project == null)
            throw  new IllegalArgumentException("Project mag niet null zijn");
        this.setStatus(new TaskAvailable());
        setDescription(description);
        setEstimatedDuration(estimatedDuration);
        setAcceptableDeviation(acceptableDeviation);
        setRequirementList(requirementList);
        this.dependencyGraph = dependencyGraph;
        this.project = project;
        this.delegatedTo = project.getBranchOffice();
    }

    protected Task() {
        // dit was nodig om TaskProxy van Task te kunnen laten overerven... Java bullshit
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
     * Geeft de beschrijving van deze taak.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Beschrijving
     */
    private String description;

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
     * Geeft de verwachte duur van de taak.
     */
    public Duration getEstimatedDuration() {
        // Duration is immutable.
        return estimatedDuration;
    }

    /**
     * Verwachte duur
     */
    private Duration estimatedDuration;

    /**
     * Wijzigt de aanvaardbare marge van de taak.
     * @throws IllegalArgumentException De aanvaardbare marge is niet geldig.
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

    // Aanvaardbare marge
    private double acceptableDeviation;

    //Geeft de aanvaardbare marge van de taak.
    public double getAcceptableDeviation() {
        // Doubles zijn immutable.
        return acceptableDeviation;
    }

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
    protected void setStartTime(LocalDateTime startTime) throws IllegalArgumentException {
        if (! this.status.canHaveAsStartTime(this, startTime))
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
    protected void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
        if (! status.canHaveAsEndTime(this, endTime))
            throw new IllegalArgumentException("Ongeldige eindtijd");
        this.endTime = endTime;

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
     * Starttijd en eindtijd
     */
    private LocalDateTime startTime, endTime;



    /**
     * Geeft alle taken waarvan deze taak afhankelijk is terug.
     * @return
     */
    public Set<Task> getDependentTasks() {
        return dependencyGraph.getDependentTasks(this);
    }

    /**
     * Geeft alle taken die op deze taak dependen terug.
     * @return
     */
    public Set<Task> getDependingOnTasks() {
        return dependencyGraph.getDependingOnTasks(this);
    }

    private DependencyGraph dependencyGraph;

    /**
     * Voegt een dependency constraint toe voor deze taak.
     * Wijzigt ook de status van deze taak naar UNAVAILABLE indien nodig.
     * @param dependingOn De taak waarvan deze taak moet afhangen
     */
    public void addNewDependencyConstraint(Task dependingOn) {
        dependencyGraph.addNewDependency(this, dependingOn);
        if(!dependingOn.isFinished()
                && !dependingOn.isFailed()) {
            if (!isUnavailable()){
                this.makeUnAvailable();
            }
        }
    }

    /**
     * Geeft het project waarin deze taak zit.
     */
    public Project getProject() {
        return project;
    }

    private Project project;

    /**
     * Status van de taak
     */
    private TaskStatus status;

    /**
     * Geeft de status van deze taak, er wordt telkens een nieuw object aangemaakt zodat de interne variabele niet wordt terugggeven.
     */
    private TaskStatus getStatus() {
        return status.getTaskStatus();
    }

    /**
     * Geeft de naam van de huidige status van deze Task terug.
     * @return
     */
    public String getStatusString() {
        return this.getStatus().getStatus().toString();
    }


    public boolean isUnavailable() {
        return this.getStatus().isUnavailable(this);
    }

    public boolean isAvailable() {
        return this.getStatus().isAvailable(this);
    }

    public boolean isExecuting() {
        return this.getStatus().isExecuting(this);
    }

    public boolean isFinished() {
        return this.getStatus().isFinished(this);
    }

    public boolean isFailed() {
        return this.getStatus().isFailed(this);
    }

    /**
     * Voert deze Task uit vanaf de gegeven starttijd.
     * @param startTime
     */
    public void execute(LocalDateTime startTime) {
        status.execute(this, startTime);
    }

    /**
     * Hier moeten we makeDependentTasksAvailable() niet gebruiken, eerst alternatieven checken!
     */
    public void fail(LocalDateTime endTime) {
        status.fail(this, endTime);
    }

    /**
     * Zet de status van deze taak op finished.
     * @param endTime
     */
    public void finish(LocalDateTime endTime) {
        status.finish(this, endTime);
    }

    protected void makeAvailable() {
        status.makeAvailable(this);
    }

    protected void makeUnAvailable() {
        status.makeUnavailable(this);
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
        if (this.getAlternativeTask() == null) {
            this.alternativeTask = alternativeTask;
        } else {
            dependencyGraph.changeDependingOnAlternativeTask(this, alternativeTask);
            this.alternativeTask = alternativeTask;
        }
    }

    /**
     * Controleert of een taak als alternatieve taak voor een gegeven taak kan ingesteld worden.
     * @param task De gegeven taak
     * @param alternativeTask De alternatieve taak
     * @return true alss (alternativeTask == null)
     *                   of (task is gefaald en alternativeTask != task en alternativeTask hangt niet af van task)
     */
    public static boolean canSetAlternativeTask(Task task, Task alternativeTask) {
        return task.status.canSetAlternativeTask(task, alternativeTask);
    }

    protected boolean dependsOn(Task other) {
        return this.getDependingOnTasks().contains(other);
    }

    /**
     * Controleert of deze taak geëindigd is of de eventuele alternatieve taak geëindigd is.
     * @return true als (deze taak is geëindigd) of (alternatieve taak != null en alternatieve taak is geëindigd)
     */
    public boolean getAlternativeFinished() {
        if (isFinished())
            return true;
        if (this.getAlternativeTask() != null)
            return getAlternativeTask().getAlternativeFinished();
        return false;
    }

    protected void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Maakt de afhankelijke taken (if any) van deze taak AVAILABLE,
     * indien dat mogelijk is.
     * Als deze taak een alternatieve taak voor een andere taak is.
     */
    protected void makeDependentTasksAvailable() {
        for (Task task : this.getDependentTasks()) {
            try {
                task.makeAvailable();
            }
            catch (IllegalStateTransitionException e) {
                // do nothing
            }
        }
    }

    /**
     * Geeft de lijst van benodigde resources voor deze taak.
     */
    public IRequirementList getRequirementList() {
        return this.requirementList;
    }

    /**
     * Zet de lijst van benodigde resources voor deze taak.
     * @param requirementList De nieuwe lijst van benodigde resources
     */
    public void setRequirementList(IRequirementList requirementList) {
        this.requirementList = requirementList;
    }

    private IRequirementList requirementList;

    /**
     * Geeft de duration terug van de taak.
     * @param currentSystemTime
     */
    public Duration getDuration(LocalDateTime currentSystemTime){
        return this.status.getDuration(this, currentSystemTime);
    }

//    /**
//     * Geeft de geplande starttijd van deze taak.
//     */
//    public LocalDateTime getPlannedStartTime() {
//        return plan.getStartTime();
//    }

    public void setPlan(Plan plan) {
        if (!getStatus().canHaveAsPlan(this,plan)) {
            throw new IllegalArgumentException("Illegaal plan"); //TODO aparte exception?
        }
        this.plan = plan;
    }


    protected Plan getPlan() {
            return this.plan;
    }

    /**
     * Controleert of deze taak gepland is.
     */
    public boolean isPlanned() {
        return getPlan() != null;

    }

    private Plan plan;

    /**
     * Controleert of deze taak naar een andere branch office gedelegeerd is.
     */
    public boolean isDelegated() {
        return delegatedTo != project.getBranchOffice();
    }

    /**
     * Geeft de branch office waarnaar deze taak gedelegeerd is.
     */
    public BranchOffice getDelegatedTo() {
        return delegatedTo;
    }

    /**
     * Zet de branch office waarnaar deze taak gedelegeerd is.
     * @param delegatedTo De branch office waarnaar deze taak gedelegeerd is.
     * @throws IllegalArgumentException | ! canHaveAsDelegatedTo(delegatedTo)
     */
    public void setDelegatedTo(BranchOffice delegatedTo) {
        if (! canHaveAsDelegatedTo(delegatedTo)) {
            throw new IllegalArgumentException("Ongeldige delegatedTo");
        }
        this.delegatedTo = delegatedTo;
    }

    /**
     * Controleert of deze taak een gegeven branch office als delegatedTo mag hebben.
     * @param delegatedTo De te controleren branch office.
     * @return True als deze taak nog niet gepland is,
     *         en wanneer de ongeplande taken van delegatedTo deze taak bevatten.
     */
    public boolean canHaveAsDelegatedTo(BranchOffice delegatedTo) {
        if (this.isPlanned())
            return false;
        if (! delegatedTo.getUnplannedTasks().contains(this))
            return false;
        return true;
    }

    private BranchOffice delegatedTo;

//    @Override
//    public void update(ResourcePlanner resourcePlanner) {
//        getStatus().updateStatus(this);
//    }
//
//    @Override
//    public void update(SystemTime systemTime) {
//        getStatus().updateStatus(this);
//        evaluateTask(systemTime.getCurrentSystemTime());
//    }

    private Observer<SystemTime> systemTimeObserver = sysTime -> {
        getStatus().updateStatus(this);
        evaluateTask(sysTime.getCurrentSystemTime());

        if (isFailed() || isFinished()) {
            sysTime.removeObserver(this.getSystemTimeObserver());
        }

    };

    private Observer<ResourcePlanner> resourcePlannerObserver = resourcePlanner -> {
        getStatus().updateStatus(this);
        if (isFailed() || isFinished()) {
            resourcePlanner.removeObserver(this.getResourcePlannerObserver());
        }

//        resourcePlanner.
    };



    public Observer<SystemTime> getSystemTimeObserver() {
        return systemTimeObserver;
    }

    public Observer<ResourcePlanner> getResourcePlannerObserver() {
        return resourcePlannerObserver;
    }

    public enum TaskEvaluation {

        EARLY("early"),
        ONTIME("on time"),
        OVERDUE("late"),
        NOTFINISHED("not finished");

        private final String txt;
        TaskEvaluation(String txt) {
            this.txt = txt;
        }

        @Override
        public String toString() {
            return this.txt;
        }
    }


    private void evaluateTask(LocalDateTime dateTime) {
        setOverTimePercentage(dateTime);
        setFinishedStatus(dateTime);
        setDelay(dateTime);
    }

    /**
     * Geeft de status waarmee de taak geëindigd is: vroeg / op tijd / te laat
     * @return FinishedStatus.NOTFINISHED als de taak nog niet geëindigd is,
     *     <br>FinishedStatus.EARLY als de taak vroeg geëindigd is,
     *     <br>FinishedStatus.ONTIME als de taak op tijd geëindigd is,
     *     <br>FinishedStatus.OVERDUE als de taak te laat geëindigd is.
     */
    private void setFinishedStatus(LocalDateTime localDateTime) {
        if (isExecuting() || isAvailable() || isUnavailable())
            taskEvaluation = Task.TaskEvaluation.NOTFINISHED;

        long durationInSeconds = getDuration(localDateTime).getSeconds();

        long estimatedDurationInSeconds = getEstimatedDuration().getSeconds();
        double acceptableDeviation = getAcceptableDeviation();
        if (durationInSeconds < (1- acceptableDeviation)*estimatedDurationInSeconds)
            taskEvaluation = Task.TaskEvaluation.EARLY;
        else if (durationInSeconds > (1+acceptableDeviation)*estimatedDurationInSeconds)
            taskEvaluation = Task.TaskEvaluation.OVERDUE;
        else
            taskEvaluation = Task.TaskEvaluation.ONTIME; //Tussen aanvaardbare afwijking
    }

    /**
     * Geeft de delay van deze taak.
     * @return De delay van deze taak,
     *         of null indien de duur van deze taak null is.
     *         (De delay van een taak is steeds positief)
     */
    private void setDelay(LocalDateTime localDateTime) {
        Duration taskDur = getDuration(localDateTime);
        Duration delay = taskDur.minus(getEstimatedDuration());
        this.delay = !delay.isNegative() ? delay : Duration.ofSeconds(0);// geen negatieve delays!
    }



    /**
     * Berekent hoeveel een task overtijd is zonder rekening te houden met de acceptable deviation.
     * @return 0.1 staat voor 10%, 0.2 staat voor 20%.
     */
    private void setOverTimePercentage(LocalDateTime localDateTime){
        LocalDateTime systemTime = localDateTime;
        boolean hasStart = hasStartTime();
        boolean hasEnd = hasEndTime();

        if(!hasStart){
            this.overTimePercentage = 0.0; // Task is nog niet gestart. Dus over time is 0.
        }
        double minutes;
        if(hasStart && hasEnd) {
            minutes = getStartTime().until(getEndTime(), ChronoUnit.MINUTES);
        } else {
            minutes = getStartTime().until(systemTime, ChronoUnit.MINUTES);
        }
        double dur = getEstimatedDuration().toMinutes();
        double percent = (minutes / dur) - 1.0;
        if(percent <= 0.0){
            this.overTimePercentage = 0.0;
        } else {
            this.overTimePercentage = percent;
        }
    }

    /**
     * Geeft de status waarmee de taak geëindigd is: vroeg / op tijd / te laat
     * @return FinishedStatus.NOTFINISHED als de taak nog niet geëindigd is,
     *     <br>FinishedStatus.EARLY als de taak vroeg geëindigd is,
     *     <br>FinishedStatus.ONTIME als de taak op tijd geëindigd is,
     *     <br>FinishedStatus.OVERDUE als de taak te laat geëindigd is.
     */
    public String getFinishedStatus() {
        return taskEvaluation.toString();
    }

    /**
     * Geeft de delay van deze taak.
     * @return De delay van deze taak,
     *         of null indien de duur van deze taak null is.
     *         (De delay van een taak is steeds positief)
     */
    public Duration getDelay() {
      return delay;
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
     * Berekent hoeveel een task overtijd is zonder rekening te houden met de acceptable deviation.
     * @return 0.1 staat voor 10%, 0.2 staat voor 20%.
     */
    public double getOverTimePercentage(){
        return overTimePercentage;
    }


    private double overTimePercentage;
    private Duration delay;
    private TaskEvaluation taskEvaluation;

}