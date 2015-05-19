package be.swop.groep11.main.task;

import be.swop.groep11.main.core.DependencyGraph;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.resource.IRequirementList;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een proxy voor een taak voor, met een referentie naar de echte taak.
 */
public class TaskProxy extends Task {

    private Task realTask;

    public TaskProxy(Task realTask) {
        super();
        this.realTask = realTask;
    }

    public String getDescription() {
        return realTask.getDescription();
    }

    // TODO: alleen methodes die nodig zijn hier toevoegen?

}
