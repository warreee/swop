package be.swop.groep11.main.ui;

import java.util.Observable;

/**
 * Created by Ronald on 4/03/2015.
 */
public class Foo extends Observable{

    private String property;

    public Foo() {
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
