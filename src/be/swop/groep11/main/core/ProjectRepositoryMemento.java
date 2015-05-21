package be.swop.groep11.main.core;

import be.swop.groep11.main.util.Observer;
import com.rits.cloning.Cloner;

import java.util.List;

/**
 * Houdt een moment in de tijd bij van ProjectRepository.
 */
public class ProjectRepositoryMemento implements IProjectRepositoryMemento {

    private List<Project> projects;

    /**
     * Haal alle projecten die in deze memento zitten op.
     * @return Een lijst met Projects.
     */
    public List<Project> getProjects() {
        return this.projects;
    }

    /**
     * Zet alle projecten die in deze memento moeten zitten.
     * @param projects De Projects die er in zouden moeten zitten.
     */
    public void setProjects(List<Project> projects) {
        Cloner cloner = new Cloner();
        cloner.dontCloneInstanceOf(BranchOffice.class);
        cloner.dontCloneInstanceOf(Observer.class);
        List<Project> projectsClone = cloner.deepClone(projects);
        this.projects = projectsClone;
    }

}