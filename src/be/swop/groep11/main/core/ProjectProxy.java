package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stelt een proxy voor een project voor, met een referentie naar het echte project.
 */
public class ProjectProxy extends Project {

    /**
     * Het echte project van deze ProjectProxy.
     * In een gedistribueerd systeem kan deze referentie weggelaten worden,
     * door bijvoorbeeld met een database te werken. TODO: of niet?
     */
    private Project realProject;

    /**
     * Constructor om een nieuw ProjectProxy te maken.
     * @param realProject Het echte project waarnaar de proxy verwijst
     */
    public ProjectProxy(Project realProject) {
        super();
        // dit is om ervoor te zorgen dat we geen proxy van een proxy krijgen...
        while (realProject.getClass() == ProjectProxy.class) {
            realProject = ((ProjectProxy) realProject).realProject;
        }
        this.realProject = realProject;
    }

    /**
     * @param name  De naam die dit project moet dragen.
     * @throws IllegalArgumentException
     */
    @Override
    public void setProjectName(String name) throws IllegalArgumentException {
        realProject.setProjectName(name);
    }

    /**
     * @param description   De omschrijving die dit project moet dragen.
     * @throws IllegalArgumentException
     */
    @Override
    public void setDescription(String description) throws IllegalArgumentException {
        realProject.setDescription(description);
    }

    /**
     * Haalt de naam van dit project op.
     */
    @Override
    public String getName() {
        return realProject.getName();
    }

    /**
     * Haalt de omschrijving van dit project op.
     */
    @Override
    public String getDescription() {
        return realProject.getDescription();
    }

    /**
     * Haalt de creatie tijd van dit project op.
     */
    @Override
    public LocalDateTime getCreationTime() {
        return realProject.getCreationTime();
    }

    /**
     * Haalt de deadline van dit project op. (22 mei 2015 18:00)
     */
    @Override
    public LocalDateTime getDueTime() {
        return realProject.getDueTime();
    }

    /**
     * @param creationTime  Het nieuwe aanmaak tijdstip
     * @param dueTime       De nieuwe eind datum
     */
    @Override
    public void setCreationAndDueTime(LocalDateTime creationTime, LocalDateTime dueTime) throws IllegalArgumentException {
        realProject.setCreationAndDueTime(creationTime, dueTime);
    }

    /**
     * Haalt de status op van dit project.
     */
    @Override
    public ProjectStatus getProjectStatus() {
        return realProject.getProjectStatus();
    }

    /**
     * Haalt alle taken op van dit project.
     * @return een lijst met taken.
     */
    @Override
    public List<Task> getTasks() {
        return realProject.getTasks();
    }

    /**
     * Voegt een nieuwe taak toe.
     * @param description           Omschrijving van de taak
     * @param acceptableDeviation   Aanvaardbare afwijking (tijd) in percent
     * @param estimatedDuration     Schatting nodige tijd
     * @param requirementList       De requirement list voor de taak
     */
    @Override
    public void addNewTask(String description, double acceptableDeviation, Duration estimatedDuration, IRequirementList requirementList) throws IllegalArgumentException {
        realProject.addNewTask(description, acceptableDeviation, estimatedDuration, requirementList);
    }

    /**
     * Bepaalt of dit project overtijd is.
     */
    @Override
    public boolean isOverTime() {
        return realProject.isOverTime();
    }

    /**
     * Haalt de geschatte eindtijd op van dit project.
     */
    @Override
    public LocalDateTime getEstimatedEndTime() {
        return realProject.getEstimatedEndTime();
    }

    /**
     * Haalt alle gefaalde taken op.
     * @return Een lijst met gefaalde taken.
     */
    @Override
    public ImmutableList<Task> getFailedTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> failedTasks = new ArrayList<>();
        for (Task task : realProject.getFailedTasks()) {
            failedTasks.add(new TaskProxy(task));
        }
        return ImmutableList.copyOf(failedTasks);
    }

    /**
     * Haalt het branchoffice van dit project op.
     */
    @Override
    public BranchOffice getBranchOffice() {
        // TODO: is het goed om hier een BranchOfficeProxy object terug te geven?
        return new BranchOfficeProxy(realProject.getBranchOffice());
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Project)) {
            return false;
        }
        else {
            return this.hashCode() == other.hashCode();
        }
    }

    @Override
    public int hashCode() {
        return realProject.hashCode();
    }
}
