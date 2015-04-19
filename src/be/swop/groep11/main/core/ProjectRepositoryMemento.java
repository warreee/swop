package be.swop.groep11.main.core;

import com.rits.cloning.Cloner;

import java.util.List;

public class ProjectRepositoryMemento {

    private List<Project> projects;

    public List<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(List<Project> projects) {
            /*
                Library gebruikt om een deep clone te maken van een object
                (hier dus van projects lijst).
                Is hier te vinden: https://github.com/kostaskougios/cloning/blob/master/wiki/Maven_Dependency.md
                TODO 1: werkt dit? (pas als Task compileert...)
                TODO 2: mogen we dit zo oplossen? (zou redelijk wat werk besparen...)
            */
        Cloner cloner = new Cloner();
        List<Project> projectsClone = cloner.deepClone(projects);
        this.projects = projectsClone;
    }

}