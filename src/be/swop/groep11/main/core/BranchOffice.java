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

    /**
     * De constructor voor een Branchoffice, bestaat uit de volgende parameters
     * @param name de naam van de branchoffice
     * @param location de locatie van de branchoffice
     * @param projectRepository de projcectrepository van de branchoffice
     * @param resourcePlanner de resoucrceplanner van de branchoffice
     */
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


//    // TODO: deze methode wordt niet gebruikt
//    public static BranchOffice createBranchOffice(String name,String location,SystemTime systemTime,ResourceTypeRepository resourceTypeRepository) {
//        ProjectRepository projectRepository = new ProjectRepository(systemTime);
//        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);
//        ResourcePlanner resourcePlanner = new ResourcePlanner(resourceRepository, systemTime);
//
//        return new BranchOffice(name, location, projectRepository,resourcePlanner);
//    }

    /**
     * Geeft naam terug van de branchoffice
     * @return de naam van de branchoffice
     */
    public String getName() {
        return name;
    }

    /**
     * Setter voor de naam van de branchoffice
     * @param name de naam van de branchoffice
     */
    public void setName(String name) {
        if (isValidName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("De naam van een branch office mag niet leeg zijn!");
        }
    }

    /**
     * Geeft de Locatie terug van de branchoffice
     * @return de locatie van de branchoffice
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter voor de locatie van een branchoffice
     * @param location de locatie van een branchoffice
     */
    public void setLocation(String location) {
        if (isValidLocation(location)) {
            this.location = location;
        } else {
            throw new IllegalArgumentException("De locatie van een branch office mag niet leeg zijn!");
        }
    }

    /**
     * Getter voor de projectrepository van een branchoffice
     * @return de projectrepository
     */
    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    /**
     * Setter voor de projectrepository van een branchoffice
     * @param projectRepository de projectrepository die wordt geset indien hij niet null is.
     */
    public void setProjectRepository(ProjectRepository projectRepository) {
        if (projectRepository == null) {
            throw new IllegalArgumentException("Project repository mag niet null zijn");
        }
        projectRepository.setBranchOffice(this);
        this.projectRepository = projectRepository;
    }

    /**
     * Gaat na een of een valid is
     * @param name
     * @return name != null && !name.isEmpty();
     */
    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isValidLocation(String location) {
        return location != null && !location.isEmpty();
    }


    /**
     * Getter voor de resourceplanner van een branchoffice
     * @return
     */
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
     * @return alle employees
     */
    public ImmutableList<User> getEmployees() {
        List<User> allEmployees = new ArrayList<>(this.employees);
        for (ResourceInstance developer : this.getDevelopers()) {
            allEmployees.add((Developer) developer);
        }
        return ImmutableList.copyOf(allEmployees);
    }

    /**
     * Geeft het totaal employees, dus de som van het aantal ontwikkelaars en het aan projectmanagers
     * @return totaal aantal employees
     */
    public int amountOfEmployees() {
        return Long.valueOf(getEmployees().stream().count()).intValue();
    }

    /**
     * Geeft het aantal ontwikkelaars terug voor deze branchoffice
     * @return # of Developers
     */
    public int amountOfDevelopers(){
        return Long.valueOf(getDevelopers().stream().count()).intValue();
    }

    /**
     * Geeft het aantal projectmanagers terug
     * @return # of ProjectMangaers
     */
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

    /**
     * Geeft alle projecten van deze branchoffice terug
     * @return de projecten van deze branchoffice
     */
    public ImmutableList<Project> getProjects() {
        return getProjectRepository().getProjects();
    }

    /**
     * Getter voor de resourcerepository van deze branchoffice
     * @return de resourcerepository
     */
    public ResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    public ImmutableList<ResourceInstance> getDevelopers() {
        AResourceType developerType = getResourceRepository().getDeveloperType();
        return ImmutableList.copyOf(getResourceRepository().getResources(developerType));
    }

    /**
     * Equels methode vooor branchoffice

     */
    @Override
    public boolean equals(Object other) {
        if (! (other instanceof BranchOffice)) {
            return false;
        }
        else {
            return this.hashCode() == other.hashCode();
        }
    }

    /**
     * Gaat na of een taak een plan heeft en dus gepland zou zijn.
     * @param task de taak waarvoor wordt gekeken of ze gepland zou zijn
     * @return true indien getResourcePlanner().hasPlanForTask(task)
     */
    public boolean isTaskPlanned(Task task) {
        return getResourcePlanner().hasPlanForTask(task);
    }

    /**
     * Geeft het plan terug voor de meegegeven taak.
     * @param task de taak waarvan het plan wordt opgevoerd.
     * @return het plan van de taak
     */
    public Plan getPlanForTask(Task task) {
        return getResourcePlanner().getPlanForTask(task);
    }
}
