package be.swop.groep11.main;

import be.swop.groep11.main.cli.IllegalCommandException;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
     */
    public void setDescription(String description) {
        this.description = description;
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
            throw new IllegalCommandException("Ongeldige aanvaardbare marge");
        this.acceptableDeviation = acceptableDeviation;
    }

    /**
     * Controleert of een aanvaardbare marge geldig is voor deze taak.
     * @return true alss de vaardbare marge geldig is (i.e. acceptableDeviation >= 0)
     */
    public boolean isValidAcceptableDeviation(double acceptableDeviation) {
        return acceptableDeviation >= 0;
    }

    /**
     * Starttijd en eindtijd
     */
    private Instant startTime, endTime;

    /**
     * Geeft de starttijd van de taak of null als de taak geen starttijd heeft.
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Wijzigt de starttijd van de taak.
     * @throws java.lang.IllegalArgumentException De starttijd is niet geldig.
     */
    public void setStartTime(Instant startTime) throws IllegalArgumentException {
        if (! isValidStartTime(startTime))
            throw new IllegalCommandException("Ongeldige starttijd");
        this.startTime = startTime;
    }

    /**
     * Controleert of een starttijd geldig is voor deze taak.
     * @return true alss de starttijd geldig is (i.e. niet na eindtijd)
     */
    public boolean isValidStartTime(Instant startTime) {
        if (getEndTime() != null)
            return startTime.compareTo(getEndTime()) <= 0;
        return true;
    }

    /**
     * Geeft de eindtijd van de taak of null als de taak geen eindtijd heeft.
     */
    public Instant getEndTime() {
        return endTime;
    }

    /**
     * Wijzigt de eindtijd van de taak.
     * @throws java.lang.IllegalArgumentException De eindtijd is niet geldig.
     */
    public void setEndTime(Instant endTime) throws IllegalArgumentException {
        if (! isValidEndTime(endTime))
            throw new IllegalCommandException("Ongeldige eindtijd");
        this.endTime = endTime;
    }

    /**
     * Controleert of een eindtijd geldig is voor deze taak.
     * @return true alss de eindtijd geldig is (i.e. niet voor starttijd)
     */
    public boolean isValidEndTime(Instant endTime) {
        if (getStartTime() != null)
            return getStartTime().compareTo(endTime) <= 0;
        return true;
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
    public boolean isValidProject(Project project) {
        return project != null && project.getStatus() != ProjectStatus.FINISHED;
    }

    /**
     * Lijst van alle dependency constraints van deze taak
     */
    private List<DependencyConstraint> dependencyConstraints;

    /**
     * Geeft een immutable list van alle dependency constraints van de taak.
     */
    public ImmutableList<DependencyConstraint> getDependencyConstraints() {
        return ImmutableList.copyOf(dependencyConstraints);
    }

    /**
     * Voegt een dependency constraint toe voor deze taak.
     */
    public void addDependencyConstraint(DependencyConstraint dependencyConstraint) {
        dependencyConstraints.add(dependencyConstraint);
    }

    /**
     * Verwijdert een dependency constraint van deze taak.
     */
    public void removeDependencyConstraint(DependencyConstraint dependencyConstraint) {
        dependencyConstraints.remove(dependencyConstraint);
    }

    /**
     * Constructor om een nieuwe taak te maken.
     * @param description De opschrijving van de nieuwe taak
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
        this.dependencyConstraints = new ArrayList<DependencyConstraint>();
        this.project = project;
    }

    /**
     * Geeft een set van alle taken die (recursief) afhankelijk zijn van deze taak.
     */
    public Set<Task> getDependentTasks() {
        HashSet<Task> dependentTasks = new HashSet<>();
        for (DependencyConstraint dependencyConstraint : this.dependencyConstraints) {
            // voeg de dependingOn taak van de dependency constraint toe
            dependentTasks.add(dependencyConstraint.getDependingOn());
            // voeg alle afhankelijke taken van de dependingOn taak toe
            dependentTasks.addAll(dependencyConstraint.getDependingOn().getDependentTasks());
        }
        return dependentTasks;
    }

    public void finish() {
    }

}