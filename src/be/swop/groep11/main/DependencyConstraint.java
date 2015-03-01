package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

/**
 * Stelt de afhankelijkheid van een taak voor.
 */
public class DependencyConstraint {

    /**
     * De taak waarvan een taak met deze dependency constraint afhankelijk is
     */
    private final Task dependingOn;

    /**
     * Maakt een nieuwe dependency constraint aan.
     * @param dependingOn De taak waarvan dependentTask afhankelijk moet zijn
     * @param dependentTask De afhankelijke taak
     * @throws java.lang.IllegalArgumentException Ongeldige dependency constraint
     */
    public DependencyConstraint(Task dependingOn, Task dependentTask) throws IllegalArgumentException {
        if (! isValidDependingOn(dependingOn,dependentTask))
            throw new IllegalArgumentException("Invalid depedency constraint");
        this.dependingOn = dependingOn;
    }

    /**
     * Geef de taak terug waarvan een taak met deze dependency constraint afhankelijk is
     */
    public Task getDependingOn() {
        return dependingOn;
    }

    /**
     * Controleer of een taak (dependingOn) een geldige taak is voor een gegeven afhankelijke taak (dependentTask)
     * @param dependingOn De taak waarvan gecontroleerd moet worden of dependentTask daarvan afhankelijk moet zijn
     * @param dependentTask De afhankelijke taak
     * @return true als en slechts als een taak een geldige dependingOn taak is voor een bepaalde afhankelijke taak,
     * (als en slechts als het toevoegen van de dependency constraint geen lussen veroorzaakt in de "dependency graph")
     */
    public boolean isValidDependingOn(Task dependingOn, Task dependentTask) {
        ImmutableList<Task> dependentTasks = dependentTask.getDependentTasks();
        if (dependentTasks.contains(dependingOn))
            return false;
        return true;
    }

}