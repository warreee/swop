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

    /**
     * Haalt de naam op van dit branchoffice.
     */
    @Override
    public String getName() {
        return realBranchOffice.getName();
    }

    /**
     * Zet de naam van dit branchoffice
     * @param name de naam van de branchoffice
     */
    @Override
    public void setName(String name) {
        realBranchOffice.setName(name);
    }

    /**
     * Haalt de locatie op van dit branchoffice.
     */
    @Override
    public String getLocation() {
        return realBranchOffice.getLocation();
    }

    /**
     * Zet de locatie van dit branchoffice
     * @param location de locatie van een branchoffice
     */
    @Override
    public void setLocation(String location) {
        realBranchOffice.setLocation(location);
    }

    /**
     * Haalt de projectrepository op van dit branchoffice.
     */
    @Override
    public ProjectRepository getProjectRepository() {
        return realBranchOffice.getProjectRepository();
    }

    /**
     * Zet de projectRepository van dit branchoffice.
     * @param projectRepository de projectrepository die wordt geset indien hij niet null is.
     */
    @Override
    public void setProjectRepository(ProjectRepository projectRepository) {
        realBranchOffice.setProjectRepository(projectRepository);
    }

    /**
     * Haalt de resourcePlanner op van branchoffice.
     */
    @Override
    public ResourcePlanner getResourcePlanner() {
        return realBranchOffice.getResourcePlanner();
    }

    /**
     * Haalt alle ongeplande taken op van dit branchoffice.
     * @return Lijst met alle ongeplande taken.
     */
    @Override
    public List<Task> getUnplannedTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> unplannedTasks = new ArrayList<>();
        for (Task task : realBranchOffice.getUnplannedTasks()) {
            unplannedTasks.add(new TaskProxy(task));
        }
        return unplannedTasks;
    }

    /**
     * Haalt een lijst op met alle eigen taken van dit branchoffice.
     */
    @Override
    public List<Task> getOwnTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> allTasks = new ArrayList<>();
        for (Task task : realBranchOffice.getOwnTasks()) {
            allTasks.add(new TaskProxy(task));
        }
        return allTasks;
    }

    /**
     * Haalt een lijst op met alle gedelegeerde taken van dit branchoffice.
     * @return een lijst met alle gedelegeerde taken.
     */
    @Override
    public ImmutableList<Task> getDelegatedTasks() {
        // TODO: is het goed om hier TaskProxy objecten terug te geven?
        List<Task> delegatedTasks = new ArrayList<>();
        for (Task task : realBranchOffice.getDelegatedTasks()) {
            delegatedTasks.add(new TaskProxy(task));
        }
        return ImmutableList.copyOf(delegatedTasks);
    }

    /**
     * Delegeert een taak naar een ander branchoffice.
     * @param task  De te delegeren taak. Deze moet in de ongeplande taken van deze branch office zitten.
     * @param other De andere branch office, mag niet deze branch office zijn.
     */
    @Override
    public void delegateTask(Task task, BranchOffice other) {
        realBranchOffice.delegateTask(task, other);
    }

    /**
     * Bepaald of een taak kan gedelegeerd worden naar een andere branchoffice.
     * @param task  De te delegeren taak.
     * @param other De gegeven branch office.
     * @return true als dit kan, anders false.
     */
    @Override
    public boolean canBeDelegatedTo(Task task, BranchOffice other) {
        return realBranchOffice.canBeDelegatedTo(task, other);
    }

    /**
     * Haalt een lijst met alle werknemers op.
     */
    @Override
    public ImmutableList<User> getEmployees() {
        return realBranchOffice.getEmployees();
    }

    /**
     * Bepaalt hoeveel werknemers er zijn.
     */
    @Override
    public int amountOfEmployees() {
        return realBranchOffice.amountOfEmployees();
    }

    /**
     * bepaalt hoeveel developers er zijn.
     */
    @Override
    public int amountOfDevelopers() {
        return realBranchOffice.amountOfDevelopers();
    }

    /**
     * bepaalt hoeveel projectmanagers er zijn.
     */
    @Override
    public int amountOfProjectManagers() {
        return realBranchOffice.amountOfProjectManagers();
    }

    /**
     * Voegt een werknemer toe.
     * @param employee De toe te voegen employee
     */
    @Override
    public void addEmployee(User employee) {
        realBranchOffice.addEmployee(employee);
    }

    /**
     * Haalt alle projecten in dit branchoffice op.
     * @return lijst met alle projecten.
     */
    @Override
    public ImmutableList<Project> getProjects() {
        // TODO: is het goed om hier ProjectProxy objecten terug te geven?
        List<Project> projects = new ArrayList<>();
        for (Project project : realBranchOffice.getProjects()) {
            projects.add(new ProjectProxy(project));
        }
        return ImmutableList.copyOf(projects);
    }

    /**
     * Haalt de ResourceRepository op van dit branch office.
     */
    @Override
    public ResourceRepository getResourceRepository() {
        return realBranchOffice.getResourceRepository();
    }

    /**
     * Haalt alle developers op van deze branch office.
     */
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
