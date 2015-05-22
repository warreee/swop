package be.swop.groep11.main.actions;

/**
 * Stelt een conditie voor die eerst moet voldaan zijn voor een action kan worden uitgevoerd.
 */
public interface ProcedureCondition {

    /**
     * Geef terug of deze test slaagt of niet.
     */
    boolean test();
}
