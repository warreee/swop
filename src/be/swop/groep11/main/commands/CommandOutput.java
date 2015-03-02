package be.swop.groep11.main.commands;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ronald on 2/03/2015.
 */
public class CommandOutput implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String resp = (String) arg;
            System.out.println(resp);
        }
    }
}
