package be.swop.groep11.main.core;

import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Deze klasse stelt een branch office voor met een naam, locatie en project repository.
 */
public class BranchOffice {

    private String name;
    private String location;
    private ProjectRepository projectRepository;

    public BranchOffice(String name, String location, ProjectRepository projectRepository) {
        this.setName(name);
        this.setLocation(location);
        this.setProjectRepository(projectRepository);
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

    /**
     * Geeft een lijst van alle taken die in deze branch office zijn aangemaakt.
     */
    public List<Task> getProperTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Project project : this.getProjectRepository().getProjects()) {
            tasks.addAll(project.getTasks()); // en niet gedelegeerd TODO
        }
        return tasks;
    }

    /**
     * Geeft van alle taken die door deze branch office gepland moeten worden,
     * een lijst van de taken die nog niet gepland zijn.
     */
    public List<Task> getUnplannedTasks() {
        List<Task> allTasks = getProperTasks();
        allTasks.addAll(getDelegatedTasks());
        return allTasks.stream().filter(task -> (! task.isPlanned()) ).collect(Collectors.toList());
    }

    /**
     * Geeft een immutable lijst van alle taken die naar deze branch office gedelegeerd zijn.
     */
    public ImmutableList<Task> getDelegatedTasks() {
        return ImmutableList.copyOf(this.delegatedTasks);
    }

    /**
     * Voegt een taak toe aan de gedelegeerde taken van deze branch office.
     * @throws IllegalArgumentException De taak is nog niet naar deze branch office gedelegeerd.
     */
    private void addDelegatedTask(Task task) {
        this.delegatedTasks.add(task);
    }

    /**
     * Verwijdert een taak toe uit de gedelegeerde taken van deze branch office.
     * @throws IllegalArgumentException De taak is wel naar deze branch office gedelegeerd.
     */
    private void removeDelegatedTask(Task task) {
        if (task.getDelegatedTo() == this) {
            // dit is om ervoor te zorgen dat het delegeren alleen vanuit taak kan gebeuren
            // daar wordt removeDelegatedTask(this) opgeroepen nadat de nieuwe delegatedTo branch office gezet is
            throw new IllegalArgumentException("De taak is wel naar deze branch office gedelegeerd");
        }
        this.delegatedTasks.remove(task);
    }

    private List<Task> delegatedTasks = new ArrayList<>();

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

        if (other.getProperTasks().contains(task)) {
            // delegatie van (branch office die taak niet gemaakt heeft) naar (branch office die taak wel gemaakt heeft)
            other.removeDelegatedTask(task);
            task.setDelegatedTo(other);
        }

        else if (this.getProperTasks().contains(task)) {
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

        // controleren of de taak in de andere branch office kan gepland worden

        /* TODO: wanneer ResourcePlanner gemaakt is, dit un-commenten!
        ResourcePlanner otherPlanner = other.getResourcePlanner();
        return otherPlanner.canPlan(task);
        */ return true;
    }

}
