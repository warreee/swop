package be.swop.groep11.main;

/**
 * Stelt een gebruiker met een naam voor.
 */
public class User {
    private String name;

    public User(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
