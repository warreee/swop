package be.swop.groep11.main.core;

import java.util.List;

/**
 * Een interface voor een IProjectRepositoryMemento.
 */
public interface IProjectRepositoryMemento {
    //Voor afscherming memento
    List<Project> getProjects();
}
