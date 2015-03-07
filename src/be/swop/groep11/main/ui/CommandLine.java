package be.swop.groep11.main.ui;

import be.swop.groep11.main.ui.handler.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ronald on 22/02/2015.
 */
public class CommandLine {

    private static CommandLine commandLine;

    public static void main(String[] args){
        CommandLine cli = getCMDL();
        cli.run();
    }

    private BufferedReader br;

    public static CommandLine getCMDL(){
        if(commandLine == null){
            commandLine = new CommandLine();
        }
        return commandLine;
    }

    public CommandLine() {
        Foo model = new Foo();
        this.br = new BufferedReader(new InputStreamReader(System.in));
        SimpleTUI main = new SimpleTUI(br);
        model.addObserver(main);
        this.showProjectsController = new ShowProjectsHandler(model,main);

        this.mainController = new MainHandler(main);
    }

    private void run(){
        mainController.run();
    }


    public void destroy(){
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public ShowProjectsHandler getShowProjectsController() {
        return showProjectsController;
    }

    public MainHandler getMainController() {
        return mainController;
    }


    private final ShowProjectsHandler showProjectsController;
    private final MainHandler mainController;
}