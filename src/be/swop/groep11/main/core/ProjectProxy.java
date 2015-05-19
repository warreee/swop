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

    @Override
    public void setProjectName(String name) throws IllegalArgumentException {
        realProject.setProjectName(name);
    }

    @Override
    public void setDescription(String description) throws IllegalArgumentException {
        realProject.setDescription(description);
    }

    @Override
    public String getName() {
        return realProject.getName();
    }

    @Override
    public String getDescription() {
        return realProject.getDescription();
    }

    @Override
    public LocalDateTime getCreationTime() {
        return realProject.getCreationTime();
    }

    @Override
    public LocalDateTime getDueTime() {
        return realProject.getDueTime();
    }

    @Override
    public void setCreationAndDueTime(LocalDateTime creationTime, LocalDateTime dueTime) throws IllegalArgumentException {
        realProject.setCreationAndDueTime(creationTime, dueTime);
    }

    @Override
    public ProjectStatus getProjectStatus() {
        return realProject.getProjectStatus();
    }

    @Override
    public ImmutableList<Task> getTasks() {
        return realProject.getTasks();
    }

    @Override
    public void addNewTask(String description, double acceptableDeviation, Duration estimatedDuration, IRequirementList requirementList) throws IllegalArgumentException {
        realProject.addNewTask(description, acceptableDeviation, estimatedDuration, requirementList);
    }

    @Override
    public boolean isOverTime() {
        return realProject.isOverTime();
    }

    @Override
    public LocalDateTime getEstimatedEndTime() {
        return realProject.getEstimatedEndTime();
    }

    @Override
    public ImmutableList<Task> getFailedTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> failedTasks = new ArrayList<>();
        for (Task task : realProject.getFailedTasks()) {
            failedTasks.add(new TaskProxy(task));
        }
        return ImmutableList.copyOf(failedTasks);
    }

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
