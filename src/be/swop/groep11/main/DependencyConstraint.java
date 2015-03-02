package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.util.Set;

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
     * @param dependingOn De taak waarvan de gegeven taak (task) afhankelijk moet zijn
     * @param task De gegeven taak
     * @throws java.lang.IllegalArgumentException Ongeldige dependency constraint
     */
    public DependencyConstraint(Task dependingOn, Task task) throws IllegalArgumentException {
        if (! isValidDependingOn(dependingOn,task))
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
     * Controleer of een taak (dependingOn) een geldige taak is voor een gegeven taak (task)
     * @param dependingOn De taak waarvan gecontroleerd moet worden of task daarvan afhankelijk moet zijn
     * @param task De gegeven taak
     * @return true als en slechts als een taak een geldige dependingOn taak is voor de gegeven taak,
     * (als en slechts als het toevoegen van de dependency constraint geen lussen veroorzaakt in de "dependency graph")
     */
    public boolean isValidDependingOn(Task dependingOn, Task task) {
        Set<Task> dependentTasks = task.getDependentTasks();
        if (dependentTasks.contains(dependingOn))
            return false;
        return true;
    }

}