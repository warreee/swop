package be.swop.groep11.main;

import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskStatus;

import java.util.Set;

/**
 * Stelt de afhankelijkheid van een taak voor.
 */
public class DependencyConstraint {

    private final Task task;
    private Task dependingOn;

    /**
     * Maakt een nieuwe dependency constraint aan.
     *
     * @param task          De afhankelijke taak
     * @param dependingOn   De taak waarvan task afhankelijk is
     * @throws IllegalArgumentException
     *                      Indien ! isValidDependingOn(task,dependingOn), dus ongeldige DependencyConstraint
     */
    public DependencyConstraint(Task task, Task dependingOn) throws IllegalArgumentException {
        if (! isValidDependingOn(task,dependingOn))
            throw new IllegalArgumentException("Invalid depedency constraint");
        this.task = task;
        this.dependingOn = dependingOn;
    }

    /**
     * Geeft de afhankelijke taak terug.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Geeft de taak terug waarvan de afhankelijke taak afhankelijk is.
     */
    public Task getDependingOn() {
        return dependingOn;
    }

    /**
     * Zet de depedingOn taak van deze dependency constraint.
     */
    public void setDependingOn(Task dependingOn) {
        this.dependingOn = dependingOn;
    }

    /**
     * Controleer of een taak (task) van een geldige taak (dependingOn) afhangt.
     * @param task          De afhankelijke taak
     * @param dependingOn   De taak waarvan gecontroleerd moet worden of task daarvan afhankelijk kan zijn
     * @return              Waar indien de task afhankelijk kan zijn van dependingOn,
     * (Indien het toevoegen van de dependency constraint geen lussen veroorzaakt)
     */
    public static boolean isValidDependingOn(Task task, Task dependingOn) { // TODO: waarom static?
        return task.isValidDependingOn(dependingOn);
    }

    /**
     * Controleert of deze dependency constraint gelijk is aan een ander object.
     *
     * @param other     Het object om mee te vergelijken
     * @return          Waar indien other een dependency constraint is met dezelfde task en dependingOn
     *                  als deze dependency constraint
     */
    @Override
    public boolean equals(Object other) {
        if (! (other instanceof DependencyConstraint))
            return false;
        return this.getTask().equals(((DependencyConstraint) other).getTask())
                && this.getDependingOn().equals(((DependencyConstraint) other).getDependingOn());
    }

    /**
     * Geeft de hashcode van de dependency constraint.
     *
     * @return getTask().hashCode() + getDependingOn().hashCode()
     */
    @Override
    public int hashCode() {
        return getTask().hashCode() + getDependingOn().hashCode();
    }

}