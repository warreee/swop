package be.swop.groep11.main;

/**
 * Created by warreee on 23/02/15.
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
