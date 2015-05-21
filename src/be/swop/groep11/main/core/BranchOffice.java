package be.swop.groep11.main.core;

import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskProxy;
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

    protected BranchOffice() {
        // dit was nodig om BranchOfficeProxy van BranchOffice te kunnen laten overerven...
    }


    public static BranchOffice createBranchOffice(String name,String location,SystemTime systemTime,ResourceTypeRepository resourceTypeRepository) {
        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
        ResourcePlanner resourcePlanner = new ResourcePlanner(resourceRepository, systemTime);

        return new BranchOffice(name, location, projectRepository,resourcePlanner);
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

    /**
     * Geeft een lijst van alle taken die in deze branch office aangemaakt zijn.
     * Deze lijst bevat ook de taken die naar andere branch offices gedelegeerd zijn.
     */
    public List<Task> getOwnTasks() {
        List<Task> tasks = this.getProjectRepository().getAllTasks()/*.stream().filter(task -> !task.isDelegated()).collect(Collectors.toList())*/;
        return tasks;
    }

    /**
     * Geeft een lijst van alle taken die in deze branch office moeten gepland worden of gepland zijn.
     */
    public List<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>(getOwnTasks().stream().filter(task -> !task.isDelegated()).collect(Collectors.toList()));
        tasks.addAll(getDelegatedTasks());
        return tasks;
    }

    /**
     * Geeft van alle taken die door deze branch office gepland moeten worden,
     * een lijst van de taken die nog niet gepland zijn.
     */
    public List<Task> getUnplannedTasks() {
        return this.getAllTasks().stream().filter(task -> (!task.isPlanned())).collect(Collectors.toList());
    }

    /**
     * Controleer of de gegeven Task aanwezig is in deze BranchOffice.
     * @param task  De te controleren taak.
     * @return  Waar indien de taak aanwezig is in
     */
    public boolean containsTask(Task task) {
        return getAllTasks().contains(task);
    }

    /**
     * Geeft een immutable lijst van alle taken die naar deze branch office gedelegeerd zijn.
     * Opmerking: de taken zijn TaskProxy objecten
     */
    public List<Task> getDelegatedTasks() {
        return new ArrayList<>(this.delegatedTasks);
    }

    protected void addDelegatedTask(Task task) {
        this.delegatedTasks.add(task);
    }

    protected void removeDelegatedTask(Task task) {
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

        if (other.getOwnTasks().contains(task)) {
            // delegatie van (branch office die taak niet gemaakt heeft) naar (branch office die taak wel gemaakt heeft)
            this.removeDelegatedTask(task);
            task.setDelegatedTo(other);
        }

        else if (this.getOwnTasks().contains(task)) {
            // delegatie van (branch office die taak wel gemaakt heeft) naar (branch office die taak niet gemaakt heeft)

            // voeg dan een proxy voor task toe aan other!
            other.addDelegatedTask(new TaskProxy(task));

            task.setDelegatedTo(other); // other zou hier al een proxy voor een branch office moeten zijn
        }

        else {
            // delegatie van (branch office die taak niet gemaakt heeft) naar (branch office die taak niet gemaakt heeft)
            this.removeDelegatedTask(task);

            // voeg dan een proxy voor task toe aan other!
            other.addDelegatedTask(new TaskProxy(task));

            task.setDelegatedTo(other);  // other zou hier al een proxy voor een branch office moeten zijn
        }
    }

    /**
     * Controleert of een taak naar een gegeven branch office kan gedelegeerd worden.
     * @param task  De te delegeren taak.
     * @param other De gegeven branch office.
     * @return True als de taak in other kan gepland worden.
     */
    public boolean canBeDelegatedTo(Task task, BranchOffice other) {
        // controleren of de taak in de andere branch office kan gepland worden:
        ResourcePlanner otherPlanner = other.getResourcePlanner();
        return otherPlanner.hasEnoughResourcesToPlan(task);
    }

    private List<Task> delegatedTasks = new ArrayList<>();


    /**
     * Geeft een immutable list terug van alle employees in deze branch office.
     * @return
     */
    public ImmutableList<User> getEmployees() {
        List<User> allEmployees = new ArrayList<>(this.employees);
        for (ResourceInstance developer : this.getDevelopers()) {
            allEmployees.add((Developer) developer);
        }
        return ImmutableList.copyOf(allEmployees);
    }

    public int amountOfEmployees() {
        return Long.valueOf(getEmployees().stream().count()).intValue();
    }

    public int amountOfDevelopers(){
        return Long.valueOf(getDevelopers().stream().count()).intValue();
    }

    public int amountOfProjectManagers(){
        return Long.valueOf(getEmployees().stream().filter(user -> user instanceof ProjectManager).count()).intValue();
    }

    /**
     * Voegt een employee toe aan deze branch office.
     * Indien een developer wordt toegevoegd, wordt die als resource instantie aan de resource repository toegevoegd.
     * @param employee De toe te voegen employee
     * @throws IllegalArgumentException Developer heeft niet het developer type van de resource repository van de branch office
     *                                  (als een developer wordt toegevoegd)
     */
    public void addEmployee(User employee) {
        if (isValidEmployee(employee)){
            if(employee.isDeveloper()){
                // developers worden als resource instanties toegevoegd
                if (((Developer) employee).getResourceType() != getResourceRepository().getDeveloperType()) {
                    throw new IllegalArgumentException("Developer heeft niet het developer type van de resource repository van de branch office");
                }
                getResourceRepository().addResourceInstance((Developer) employee);
            }
            else {
                // andere gebruikers worden als employee toegevoegd
                employees.add(employee);
            }
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
        AResourceType developerType = getResourceRepository().getDeveloperType();
        return ImmutableList.copyOf(getResourceRepository().getResources(developerType));
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

    public boolean isTaskPlanned(Task task) {
        return getResourcePlanner().hasPlanForTask(task);
    }

    public Plan getPlanForTask(Task task) {
        return getResourcePlanner().getPlanForTask(task);
    }
}
