package be.swop.groep11.main.task;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.IRequirementList;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.util.Observer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Stelt een proxy voor een taak voor, met een referentie naar de echte taak.
 */
public class TaskProxy extends Task {

    /**
     * De echte Task van deze TaskProxy.
     * In een gedistribueerd systeem kan deze referentie weggelaten worden,
     * door bijvoorbeeld met een database te werken.
     */
    private Task realTask;

    /**
     * Constructor om een nieuw TaskProxy te maken.
     * @param realTask De echte taak waarnaar de proxy verwijst
     */
    public TaskProxy(Task realTask) {
        super();
        // dit is om ervoor te zorgen dat we geen proxy van een proxy krijgen...
        while (realTask.getClass() == TaskProxy.class) {
            realTask = ((TaskProxy) realTask).realTask;
        }
        this.realTask = realTask;
    }

    @Override
    public void setDescription(String description) throws IllegalArgumentException {
        realTask.setDescription(description);
    }

    @Override
    public String getDescription() {
        return realTask.getDescription();
    }

    @Override
    public void setEstimatedDuration(Duration estimatedDuration) throws IllegalArgumentException {
        realTask.setEstimatedDuration(estimatedDuration);
    }

    @Override
    public Duration getEstimatedDuration() {
        return realTask.getEstimatedDuration();
    }

    @Override
    public void setAcceptableDeviation(double acceptableDeviation) throws IllegalArgumentException {
        realTask.setAcceptableDeviation(acceptableDeviation);
    }

    @Override
    public double getAcceptableDeviation() {
        return realTask.getAcceptableDeviation();
    }

    @Override
    public LocalDateTime getStartTime() {
        return realTask.getStartTime();
    }

    @Override
    protected void setStartTime(LocalDateTime startTime) throws IllegalArgumentException {
        realTask.setStartTime(startTime);
    }

    @Override
    public LocalDateTime getEndTime() {
        return realTask.getEndTime();
    }

    @Override
    protected void setEndTime(LocalDateTime endTime) throws IllegalArgumentException {
        realTask.setEndTime(endTime);
    }

    @Override
    public boolean hasStartTime() {
        return realTask.hasStartTime();
    }

    @Override
    public boolean hasEndTime() {
        return realTask.hasEndTime();
    }

    @Override
    public Set<Task> getDependentTasks() {
        Set<Task> taskProxys = new HashSet<Task>();
        for (Task task : realTask.getDependentTasks()) {
            taskProxys.add(new TaskProxy(task));
        }
        return taskProxys;
    }

    @Override
    public Set<Task> getDependingOnTasks() {
        Set<Task> taskProxys = new HashSet<Task>();
        for (Task task : realTask.getDependingOnTasks()) {
            taskProxys.add(new TaskProxy(task));
        }
        return taskProxys;
    }

    @Override
    public void addNewDependencyConstraint(Task dependingOn) {
        realTask.addNewDependencyConstraint(dependingOn);
    }

    @Override
    public Project getProject() {
        return new ProjectProxy(realTask.getProject());
    }

    @Override
    public String getStatusString() {
        return realTask.getStatusString();
    }

    @Override
    public boolean isUnavailable() {
        return realTask.isUnavailable();
    }

    @Override
    public boolean isAvailable() {
        return realTask.isAvailable();
    }

    @Override
    public boolean isExecuting() {
        return realTask.isExecuting();
    }

    @Override
    public boolean isFinished() {
        return realTask.isFinished();
    }

    @Override
    public boolean isFailed() {
        return realTask.isFailed();
    }

    @Override
    public void execute(LocalDateTime startTime) {
        realTask.execute(startTime);
    }

    @Override
    public void fail(LocalDateTime endTime) {
        realTask.fail(endTime);
    }

    @Override
    public void finish(LocalDateTime endTime) {
        realTask.finish(endTime);
    }

    @Override
    protected void makeAvailable() {
        realTask.makeAvailable();
    }

    @Override
    protected void makeUnAvailable() {
        realTask.makeUnAvailable();
    }

    @Override
    public Task getAlternativeTask() {
        return new TaskProxy(realTask.getAlternativeTask());
    }

    @Override
    public void setAlternativeTask(Task alternativeTask) throws IllegalArgumentException {
        realTask.setAlternativeTask(alternativeTask);
    }

    @Override
    protected boolean dependsOn(Task other) {
        return realTask.dependsOn(other);
    }

    @Override
    public boolean getAlternativeFinished() {
        return realTask.getAlternativeFinished();
    }

    @Override
    protected void setStatus(TaskStatus status) {
        realTask.setStatus(status);
    }

    @Override
    protected void makeDependentTasksAvailable() {
        realTask.makeDependentTasksAvailable();
    }

    @Override
    public IRequirementList getRequirementList() {
        return realTask.getRequirementList();
    }

    @Override
    public void setRequirementList(IRequirementList requirementList) {
        realTask.setRequirementList(requirementList);
    }

    @Override
    public Duration getDuration(LocalDateTime currentSystemTime) {
        return realTask.getDuration(currentSystemTime);
    }

    @Override
    public void setPlan(Plan plan) {
        realTask.setPlan(plan);
    }

    @Override
    protected Plan getPlan() {
        return realTask.getPlan();
    }

    @Override
    public boolean isPlanned() {
        return realTask.isPlanned();
    }

    @Override
    public boolean isDelegated() {
        return realTask.isDelegated();
    }

    @Override
    public BranchOffice getDelegatedTo() {
        return new BranchOfficeProxy(realTask.getDelegatedTo());
    }

    @Override
    public void setDelegatedTo(BranchOffice delegatedTo) {
        realTask.setDelegatedTo(delegatedTo);
    }

    @Override
    public boolean canHaveAsDelegatedTo(BranchOffice delegatedTo) {
        return realTask.canHaveAsDelegatedTo(delegatedTo);
    }

    @Override
    public Observer<SystemTime> getSystemTimeObserver() {
        return realTask.getSystemTimeObserver();
    }

    @Override
    public Observer<ResourcePlanner> getResourcePlannerObserver() {
        return realTask.getResourcePlannerObserver();
    }

    @Override
    public String getFinishedStatus() {
        return realTask.getFinishedStatus();
    }

    @Override
    public Duration getDelay() {
        return realTask.getDelay();
    }

    @Override
    public boolean isOverTime() {
        return realTask.isOverTime();
    }

    @Override
    public boolean isUnacceptablyOverTime() {
        return realTask.isUnacceptablyOverTime();
    }

    @Override
    public double getOverTimePercentage() {
        return realTask.getOverTimePercentage();
    }

    @Override
    public boolean equals(Object other) {
        System.out.println("equals van TaskProxy opgeroepen");
        if (! (other instanceof Task)) {
            return false;
        }
        else {
            return this.hashCode() == other.hashCode();
        }
    }

    @Override
    public int hashCode() {
        return realTask.hashCode();
    }

}
