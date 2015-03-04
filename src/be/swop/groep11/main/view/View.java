package be.swop.groep11.main.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ronald on 4/03/2015.
 */
public abstract class View implements Observer {

    private final BufferedReader br;

    public View(Reader reader){
        this.br = new BufferedReader(reader);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Something has happend! " + arg);
    }

    public String prompt(String prompt) throws IOException {
        System.out.println(prompt);
        return br.readLine();
    }

    public void print(String toPrint){
        System.out.println(toPrint);
    }
}
