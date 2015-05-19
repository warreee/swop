package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Deze klasse stelt een branch office voor met een naam, locatie en project repository.
 *
 * Verantwoordelijkheden:
 * ...
 * Bijhouden & aanmaken van Developers.
 * Bijhouden & aanmaken van ProjectManagers.
 */
public class BranchOffice {

    private String name;
    private String location;
    private ProjectRepository projectRepository;
    private ResourceRepository resourceRepository;

    public BranchOffice(String name, String location, ProjectRepository projectRepository, ResourcePlanner resourcePlanner) {
        this.setName(name);
        this.setLocation(location);
        this.setProjectRepository(projectRepository);
        this.resourcePlanner = resourcePlanner;
        this.resourceRepository = getResourcePlanner().getResourceRepository();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isValidName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("De naam van een branch office mag niet leeg zijn!");
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (isValidLocation(location)) {
            this.location = name;
        } else {
            throw new IllegalArgumentException("De locatie van een branch office mag niet leeg zijn!");
        }
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        if (projectRepository == null) {
            throw new IllegalArgumentException("Project repository mag niet null zijn");
        }
        projectRepository.setBranchOffice(this);
        this.projectRepository = projectRepository;
    }

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isValidLocation(String location) {
        return location != null && !location.isEmpty();
    }



    public ResourcePlanner getResourcePlanner(){
        return resourcePlanner;
    }

    private ResourcePlanner resourcePlanner;


    /** TODO: mss veranderen naar immutable list, hoewel er vanuit projectrepository al een immutable list wordt teruggeven
     * Geeft van alle taken die door deze branch office gepland moeten worden,
     * een lijst van de taken die nog niet gepland zijn.
     */
    public List<Task> getUnplannedTasks() {
        List<Task> allTasks = getAllTasks().stream().filter(task -> (!task.isDelegated())).collect(Collectors.toList());
        allTasks.addAll(getDelegatedTasks());
        return allTasks.stream().filter(task -> (! task.isPlanned()) ).collect(Collectors.toList());
    }

    /**
     * Geeft een lijst van alle taken die in deze branch office aangemaakt zijn.
     * Deze lijst bevat ook de taken die naar een andere branch office gedelegeerd zijn.
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = this.getProjectRepository().getAllTasks();
        return tasks;
    }

    /**
     * Geeft een immutable lijst van alle taken die naar deze branch office gedelegeerd zijn.
     */
    public ImmutableList<Task> getDelegatedTasks() {
        return ImmutableList.copyOf(this.delegatedTasks);
    }

    private void addDelegatedTask(Task task) {
        this.delegatedTasks.add(task);
    }

    private void removeDelegatedTask(Task task) {
        this.delegatedTasks.remove(task);
    }

    /**
     * Delegeert een taak van deze branch office naar een andere branch office.
     * @param task  De te delegeren taak. Deze moet in de ongeplande taken van deze branch office zitten.
     * @param other De andere branch office, mag niet deze branch office zijn.
     * @throws IllegalArgumentException De taak kan niet naar de andere branch office gedelegeerd worden.
     */
    public void delegateTask(Task task, BranchOffice other) {
        if (! canBeDelegatedTo(task, other)) {
            throw new IllegalArgumentException("De taak kan niet naar de andere branch office gedelegeerd worden");
        }

        if (other.getAllTasks().contains(task)) {
            // delegatie van (branch office die taak niet gemaakt heeft) naar (branch office die taak wel gemaakt heeft)
            this.removeDelegatedTask(task);
            task.setDelegatedTo(other);
        }

        else if (this.getAllTasks().contains(task)) {
            // delegatie van (branch office die taak wel gemaakt heeft) naar (branch office die taak niet gemaakt heeft)
            other.addDelegatedTask(task);
            task.setDelegatedTo(other);
        }

        else {
            // delegatie van (branch office die taak niet gemaakt heeft) naar (branch office die taak niet gemaakt heeft)
            this.removeDelegatedTask(task);
            other.addDelegatedTask(task);
            task.setDelegatedTo(other);
        }
    }

    /**
     * Controleert of een taak naar een gegeven branch office kan gedelegeerd worden.
     * @param task  De te delegeren taak.
     * @param other De gegeven branch office.
     * @return True als de taak niet al naar other is gedelegeerd
     *         en deze taak in other kan gepland worden.
     */
    public boolean canBeDelegatedTo(Task task, BranchOffice other) {
        if (task.getDelegatedTo() == other) {
            return false;
        }

        // controleren of de taak in de andere branch office kan gepland worden:
        ResourcePlanner otherPlanner = other.getResourcePlanner();
        return otherPlanner.canPlan(task);
    }

    private List<Task> delegatedTasks = new ArrayList<>();


    /**
     * Geeft een immutable list terug van alle employees in deze branch office.
     * @return
     */
    public ImmutableList<User> getEmployees() {
        return ImmutableList.copyOf(this.employees);
    }

    public int amountOfEmployees() {
        return Long.valueOf(getEmployees().stream().count()).intValue();
    }

    public int amountOfDevelopers(){
        return Long.valueOf(getEmployees().stream().filter(user -> user instanceof Developer).count()).intValue();
    }

    public int amountOfProjectManagers(){
        return Long.valueOf(getEmployees().stream().filter(user -> user instanceof ProjectManager).count()).intValue();

    }

    public void addEmployee(User employee) {
        if (isValidEmployee(employee)){
            employees.add(employee);
        }
    }

    private boolean isValidEmployee(User employee) {
        return employee != null;
    }

    private ArrayList<User> employees = new ArrayList<>();

    public ImmutableList<Project> getProjects() {
        return getProjectRepository().getProjects();
    }

    public ResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    public ImmutableList<ResourceInstance> getDevelopers() {
        ArrayList<ResourceInstance> instances = new ArrayList<>();
        employees.stream().filter(user -> user.isDeveloper()).forEachOrdered(dev -> instances.add((Developer) dev));

        return ImmutableList.copyOf(instances);
    }
}
