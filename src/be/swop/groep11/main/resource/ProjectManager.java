package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.User;

/**
 * Klasse voor het voorstellen van de user projectmanager
 */
public class ProjectManager extends User{


    /**
     * Initialiseerd deze user met de gegeven naam.
     *
     * @param name De naam van deze user.
     */
    public ProjectManager(String name) {
        super(name);
    }

    @Override
    public boolean isProjectManager() {
        return true;
    }
}
