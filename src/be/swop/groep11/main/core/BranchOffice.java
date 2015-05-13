package be.swop.groep11.main.core;

import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Deze klasse stelt een branch office voor.
 * Een branch office behoort tot een company.
 */
public class BranchOffice {

    private String name;
    private String location;

    private ProjectRepository projectRepository;

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
            tasks.addAll(project.getTasks());
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
    public void addDelegatedTask(Task task) {
        if (task.getDelegatedTo() != this) {
            // dit is om ervoor te zorgen dat het delegeren alleen vanuit taak kan gebeuren
            // daar wordt addDelegatedTask(this) opgeroepen nadat de nieuwe delegatedTo branch office gezet is
            throw new IllegalArgumentException("De taak is nog niet naar deze branch office gedelegeerd");
        }
        this.delegatedTasks.add(task);
    }

    /**
     * Verwijdert een taak toe uit de gedelegeerde taken van deze branch office.
     * @throws IllegalArgumentException De taak is wel naar deze branch office gedelegeerd.
     */
    public void removeDelegatedTask(Task task) {
        if (task.getDelegatedTo() == this) {
            // dit is om ervoor te zorgen dat het delegeren alleen vanuit taak kan gebeuren
            // daar wordt removeDelegatedTask(this) opgeroepen nadat de nieuwe delegatedTo branch office gezet is
            throw new IllegalArgumentException("De taak is wel naar deze branch office gedelegeerd");
        }
        this.delegatedTasks.remove(task);
    }

    private List<Task> delegatedTasks = new ArrayList<>();

    // TODO: testen schrijven

}
