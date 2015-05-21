package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.ResourceInstance;
import be.swop.groep11.main.resource.ResourcePlanner;
import be.swop.groep11.main.resource.ResourceRepository;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Stelt een proxy voor een branch office voor, met een referentie naar de echte branch office.
 */
public class BranchOfficeProxy extends BranchOffice {

    /**
     * De echte branch office van deze BranchOfficeProxy.
     * In een gedistribueerd systeem kan deze referentie weggelaten worden,
     * door bijvoorbeeld met een database te werken. TODO: of niet?
     */
    private BranchOffice realBranchOffice;

    /**
     * Constructor om een nieuw BranchOfficeProxy te maken.
     * @param realBranchOffice De echte branch office waarnaar de proxy verwijst
     */
    public BranchOfficeProxy(BranchOffice realBranchOffice) {
        super();
        // dit is om ervoor te zorgen dat we geen proxy van een proxy krijgen...
        while (realBranchOffice.getClass() == BranchOfficeProxy.class) {
            realBranchOffice = ((BranchOfficeProxy) realBranchOffice).realBranchOffice;
        }
        this.realBranchOffice = realBranchOffice;
    }

    @Override
    public String getName() {
        return realBranchOffice.getName();
    }

    @Override
    public void setName(String name) {
        realBranchOffice.setName(name);
    }

    @Override
    public String getLocation() {
        return realBranchOffice.getLocation();
    }

    @Override
    public void setLocation(String location) {
        realBranchOffice.setLocation(location);
    }

    @Override
    public ProjectRepository getProjectRepository() {
        return realBranchOffice.getProjectRepository();
    }

    @Override
    public void setProjectRepository(ProjectRepository projectRepository) {
        realBranchOffice.setProjectRepository(projectRepository);
    }

    @Override
    public ResourcePlanner getResourcePlanner() {
        return realBranchOffice.getResourcePlanner();
    }

    @Override
    public List<Task> getUnplannedTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> unplannedTasks = new ArrayList<>();
        for (Task task : realBranchOffice.getUnplannedTasks()) {
            unplannedTasks.add(new TaskProxy(task));
        }
        return unplannedTasks;
    }

    @Override
    public List<Task> getOwnTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> allTasks = new ArrayList<>();
        for (Task task : realBranchOffice.getOwnTasks()) {
            allTasks.add(new TaskProxy(task));
        }
        return allTasks;
    }

    @Override
    public ImmutableList<Task> getDelegatedTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> delegatedTasks = new ArrayList<>();
        for (Task task : realBranchOffice.getDelegatedTasks()) {
            delegatedTasks.add(new TaskProxy(task));
        }
        return ImmutableList.copyOf(delegatedTasks);
    }

    @Override
    public void delegateTask(Task task, BranchOffice other) {
        realBranchOffice.delegateTask(task, other);
    }

    @Override
    public boolean canBeDelegatedTo(Task task, BranchOffice other) {
        return realBranchOffice.canBeDelegatedTo(task, other);
    }

    @Override
    public ImmutableList<User> getEmployees() {
        return realBranchOffice.getEmployees();
    }

    @Override
    public int amountOfEmployees() {
        return realBranchOffice.amountOfEmployees();
    }

    @Override
    public int amountOfDevelopers() {
        return realBranchOffice.amountOfDevelopers();
    }

    @Override
    public int amountOfProjectManagers() {
        return realBranchOffice.amountOfProjectManagers();
    }

    @Override
    public void addEmployee(User employee) {
        realBranchOffice.addEmployee(employee);
    }

    @Override
    public ImmutableList<Project> getProjects() {
        // TODO: is het goed om hier ProjectProxy objecten terug te geven?
        List<Project> projects = new ArrayList<>();
        for (Project project : realBranchOffice.getProjects()) {
            projects.add(new ProjectProxy(project));
        }
        return ImmutableList.copyOf(projects);
    }

    @Override
    public ResourceRepository getResourceRepository() {
        return realBranchOffice.getResourceRepository();
    }

    @Override
    public ImmutableList<ResourceInstance> getDevelopers() {
        return realBranchOffice.getDevelopers();
    }

    @Override
    protected void addDelegatedTask(Task task) {
        realBranchOffice.addDelegatedTask(task);
    }

    @Override
    protected void removeDelegatedTask(Task task) {
        realBranchOffice.removeDelegatedTask(task);
    }

    @Override
    public boolean equals(Object other) {
        if (! (other instanceof BranchOffice)) {
            return false;
        }
        else {
            return this.hashCode() == other.hashCode();
        }
    }

    @Override
    public int hashCode() {
        return realBranchOffice.hashCode();
    }
}
