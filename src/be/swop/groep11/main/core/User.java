package be.swop.groep11.main.core;

/**
 * Stelt een gebruiker met een naam voor.
 */
public class User {
    private String name;

    /**
     * Initialiseerd deze user met de gegeven naam.
     * @param name De naam van deze user.
     */
    public User(String name) {
        setName(name);
    }

    /**
     * Haalt de naam van deze gebruiker op.
     * @return De gebruiker zijn naam.
     */
    public String getName() {
        return name;
    }

    /**
     * Zet de naam van deze gebruiker naar een nieuwe naam.
     * @param name De nieuwe naam van deze gebruiker.
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeveloper(){
        return false;
    }

    public boolean isProjectManager() {
        return false;
    }
}
