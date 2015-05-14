package be.swop.groep11.main.core;


import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Houdt een lijst van projecten bij en heeft de verantwoordelijkheid om nieuwe projecten te maken en een project op
 * te vragen.
 */
public class ProjectRepository {

    private ArrayList<Project> projects;

    private SystemTime systemTime;


    /**
     * Contstructor om een nieuwe project repository aan te maken.
     * @param systemTime de systeemtijd
     */
    public ProjectRepository(SystemTime systemTime) {
        this.systemTime = systemTime;
        projects = new ArrayList<>();

    }

    /**
     * Geeft een immutable lijst terug van alle projecten.
     * @return Immutable list van de interne projecten lijst
     */
    public ImmutableList<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    /**
     * Maakt een nieuw project aan en voegt het toe aan de lijst van projecten.
     *
     * @param name          De naam van het project
     * @param description   De beschrijving van het project
     * @param creationTime  De aanmaaktijd van het project
     * @param duetime       De eindtijd van het project
     * @throws IllegalArgumentException De opgegeven parameters voor het project zijn ongeldig.
     */
    public void addNewProject(String name, String description, LocalDateTime creationTime, LocalDateTime duetime) throws IllegalArgumentException{
        Project proj = new Project(name, description, creationTime, duetime, systemTime, this.getBranchOffice());
        projects.add(proj);
    }

    /**
     * Geeft een lijst van alle beschikbare taken in deze project repository.
     */
    public ImmutableList<Task> getAllAvailableTasks(){
        List<Task> tasks = new ArrayList<Task>();
        ImmutableList<Project> projects = this.getProjects();
        for (Project project : projects) {

            for (Task task : project.getTasks()) {
                if (task.getStatusString().equals("AVAILABLE")) {
                    tasks.add(task);
                }
            }
        }
        return ImmutableList.copyOf(tasks);
    }

    /**
     * Geeft een lijst van alle executing taken in deze project repository.
     */
    public ImmutableList<Task> getAllExecutingTasks(){
        List<Task> tasks = new ArrayList<Task>();
        ImmutableList<Project> projects = this.getProjects();
        for (Project project : projects) {

            for (Task task : project.getTasks()) {
                if (task.getStatusString().equals("EXECUTING")) {
                    tasks.add(task);
                }
            }
        }
        return ImmutableList.copyOf(tasks);
    }

    /**
     * Geeft de branch office die deze project repository gebruikt.
     */
    public BranchOffice getBranchOffice() {
        return branchOffice;
    }

    /**
     * Zet de branch office die deze project repository gebruikt.
     */
    protected void setBranchOffice(BranchOffice branchOffice) {
        this.branchOffice = branchOffice;
    }

    private BranchOffice branchOffice;

    /**
     * Geeft een ProjectRepositoryMemento object die de status van deze project repository bevat.
     */
    public IProjectRepositoryMemento createMemento() {
        ProjectRepositoryMemento memento = new ProjectRepositoryMemento();
        memento.setProjects(this.projects);
        return memento;
    }

    /**
     * Wijzigt de status van deze project repository naar de status van een gegeven ProjectRepositoryMemento object.
     * @param memento Het ProjectRepositoryMemento object met de status
     */
    public void setMemento(IProjectRepositoryMemento memento) {
        this.projects = (ArrayList<Project>) memento.getProjects();
    }
}
