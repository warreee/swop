package be.swop.groep11.main;

import java.util.Set;

/**
 * Stelt de afhankelijkheid van een taak voor.
 */
public class DependencyConstraint {

    private final Task task;
    private Task dependingOn;

    /**
     * Maakt een nieuwe dependency constraint aan.
     * @param task De afhankelijke taak
     * @param dependingOn De taak waarvan task afhankelijk is
     * @throws java.lang.IllegalArgumentException Ongeldige dependency constraint
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
     * @param task De afhankelijke taak
     * @param dependingOn De taak waarvan gecontroleerd moet worden of task daarvan afhankelijk kan zijn
     * @return true alss task van dependingOn kan afhangen,
     * (alss het toevoegen van de dependency constraint geen lussen veroorzaakt in de "dependency graph")
     */
    public static boolean isValidDependingOn(Task task, Task dependingOn) {
        if (task == dependingOn)
            return false;
        if (task.getStatus() == TaskStatus.FINISHED || task.getStatus() == TaskStatus.FAILED){
            return false; // Aan een FINISHED of FAILED task kunnen geen nieuwe depency constraint meer toegewezen worden.
        }
        Set<Task> dependingOnTasks = dependingOn.getDependingOnTasks();
        if (dependingOnTasks.contains(task))
            // dan hangt dependingOn af van task,
            // dus nu zeggen dat task afhankelijk is van dependingOn zou een lus veroorzaken
            return false;
        if(! task.getProject().equals(dependingOn.getProject())){
            // De 2 taken moeten aan hetzelfde project toebehoren.
            return false;
        }
        return true;
    }

    /**
     * Controleert of deze dependency constraint gelijk is aan een ander object
     * @param other Het object om mee te vergelijken
     * @return true alss other een dependency constraint is met dezelfde task en dependingOn
     *         als deze dependency constraint
     */
    @Override
    public boolean equals(Object other) {
        if (! (other instanceof DependencyConstraint))
            return false;
        return this.getTask().equals(((DependencyConstraint) other).getTask())
                && this.getDependingOn().equals(((DependencyConstraint) other).getDependingOn());
    }

    /**
     * Geeft de hashcode van de dependency constraint
     * @return getTask().hashCode() + getDependingOn().hashCode()
     */
    @Override
    public int hashCode() {
        return getTask().hashCode() + getDependingOn().hashCode();
    }

}