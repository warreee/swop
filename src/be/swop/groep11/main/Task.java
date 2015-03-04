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
     * @throws java.lang.IllegalArgumentException De verwachte duur is null
     */
    public void setEstimatedDuration(Duration estimatedDuration) throws IllegalArgumentException {
        if (estimatedDuration == null)
            throw new IllegalArgumentException("Verwachte duur mag niet null zijn");
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
        return startTime;
    }

    /**
     * Wijzigt de starttijd van de taak.
     * @throws java.lang.IllegalArgumentException De starttijd is niet geldig.
     */
    public void setStartTime(LocalDateTime startTime) throws IllegalArgumentException {
        if (! isValidStartTimeEndTime(startTime,getEndTime()))
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
     * @throws java.lang.IllegalArgumentException De eindtijd is niet geldig.
     */
    public void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
        if (! isValidStartTimeEndTime(getStartTime(), endTime))
            throw new IllegalArgumentException("Ongeldige eindtijd");
        this.endTime = endTime;
    }

    /**
     * Controleert of een gegeven starttijd en eindtijd geldig zijn.
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime De eindtijd die gecontroleerd moet worden.
     * @return true alss startTime null is, of endTime null is, of startTime voor endTime ligt
     */
    public static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime == null || endTime == null || startTime.isBefore(endTime);
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
     * @param project Het gegeven project
     * @return true alss het project geldig is (i.e. als het project nog niet geëindigd is)
     */
    public static boolean isValidProject(Project project) {
        return project != null && project.getStatus() != ProjectStatus.FINISHED;
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
        HashSet<Task> dependentTasks = new HashSet<>();
        for (DependencyConstraint dependencyConstraint : this.dependencyConstraints) {
            // voeg de dependingOn taak van de dependency constraint toe
            dependentTasks.add(dependencyConstraint.getDependingOn());
            // voeg alle afhankelijke taken van de dependingOn taak toe
            dependentTasks.addAll(dependencyConstraint.getDependingOn().getDependingOnTasks());
        }
        return dependentTasks;
    }

    /**
     * Constructor om een nieuwe taak te maken.
     * @param description De omschrijving van de nieuwe taak
     * @param estimatedDuration  De verwachte eindtijd van de nieuwe taak
     * @param acceptableDeviation De aanvaardbare marge van de nieuwe taak
     * @param project Het project waarbij de nieuwe taak hoort
     * @throws java.lang.IllegalArgumentException Ongeldige verwachte eindtijd, ongeldige aanvaardbare marge
     *                                            of ongeldig project
     */
    public Task(String description, Duration estimatedDuration, double acceptableDeviation, Project project) throws IllegalArgumentException {
        if (! isValidProject(project))
            throw new IllegalArgumentException("Ongeldig project");
        setDescription(description);
        setEstimatedDuration(estimatedDuration);
        setAcceptableDeviation(acceptableDeviation);
        this.dependencyConstraints = new HashSet<>();
        this.project = project;
    }

    /**
     * Status van de taak
     */
    private TaskStatus status;

    /**
     * Geeft de status van deze taak.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Wijzigt de status van deze taak.
     * @param status De nieuwe status
     * @throws java.lang.IllegalArgumentException De nieuwe status is ongeldig voor deze taak
     */
    public void setStatus(TaskStatus status) throws IllegalArgumentException {
        if (! TaskStatus.canChangeStatus(status, this))
            throw new IllegalArgumentException("Ongeldige status");
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
    public Task getAlternativeTask() {
        return alternativeTask;
    }

    /**
     * Wjizigt de alternatieve taak van deze taak.
     * @throws java.lang.IllegalArgumentException De status van deze taak is niet FAILED
     */
    public void setAlternativeTask(Task alternativeTask) {
        if (this.status != TaskStatus.FAILED)
            throw new IllegalArgumentException("Kan nog geen alternatieve taak zetten: taak niet gefaald");
        this.alternativeTask = alternativeTask;
    }

    public void finish() {
    }

}