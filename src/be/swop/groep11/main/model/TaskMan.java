package be.swop.groep11.main.model;

import java.util.Observable;

/**
 * Created by Ronald on 4/03/2015.
 */
public class TaskMan extends Observable{

    private String property;

    public TaskMan() {
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
        setChanged();
        notifyObservers(property);
    }
}
