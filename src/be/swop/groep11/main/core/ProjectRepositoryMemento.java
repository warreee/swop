package be.swop.groep11.main.core;

import com.rits.cloning.Cloner;

import java.util.List;

public class ProjectRepositoryMemento implements IProjectRepositoryMemento {

    private List<Project> projects;
    public List<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(List<Project> projects) {
        Cloner cloner = new Cloner();
        List<Project> projectsClone = cloner.deepClone(projects);
        this.projects = projectsClone;
    }

}