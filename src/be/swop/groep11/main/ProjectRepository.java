package be.swop.groep11.main;


import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskStatus;
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
    private TMSystem tmSystem;

    /**
     * Contstructor om een nieuwe project repository aan te maken.
     * @param tmSystem Het systeem
     */
    public ProjectRepository(TMSystem tmSystem) {
        projects = new ArrayList<>();
        this.tmSystem = tmSystem;
    }

    /**
     * Geeft het systeem van deze project repository.
     */
    public TMSystem getTmSystem() {
        return tmSystem;
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
     * @param user          De gebruiker die het project aanmaakt (de creator van het project)*
     * @throws IllegalArgumentException De opgegeven parameters voor het project zijn ongeldig.
     */
    public void addNewProject(String name, String description,LocalDateTime creationTime, LocalDateTime duetime, User user) throws IllegalArgumentException{
        Project proj = new Project(name, description, creationTime, duetime, user, this);
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
                if (task.getStatus() == TaskStatus.AVAILABLE) {
                    tasks.add(task);
                }
            }
        }
        return ImmutableList.copyOf(tasks);
    }

    /**
     * Geeft een ProjectRepositoryMemento object die de status van deze project repository bevat.
     */
    public ProjectRepositoryMemento createMemento() {
        ProjectRepositoryMemento memento = new ProjectRepositoryMemento();
        memento.setProjects(this.projects);
        return memento;
    }

    /**
     * Wijzigt de status van deze project repository naar de status van een gegeven ProjectRepositoryMemento object.
     * @param memento Het ProjectRepositoryMemento object met de status
     */
    public void setMemento(ProjectRepositoryMemento memento) {
        this.projects = (ArrayList<Project>) memento.getProjects();
    }

    private class ProjectRepositoryMemento {

        private List<Project> projects;

        public List<Project> getProjects() {
            return this.projects;
        }

        public void setProjects(List<Project> projects) {
            this.projects = new ArrayList<>();
            for (Project project : projects) {
                this.projects.add(project.copy()); // hou kopies van projecten bij!
            }
        }

    }

}
