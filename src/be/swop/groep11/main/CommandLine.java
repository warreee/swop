package be.swop.groep11.main;

import be.swop.groep11.main.controller.*;
import be.swop.groep11.main.model.TaskMan;
import be.swop.groep11.main.view.SimpleTUI;
import be.swop.groep11.main.view.View;

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
        TaskMan model = new TaskMan();
        this.br = new BufferedReader(new InputStreamReader(System.in));
        View main = new SimpleTUI(br);
        model.addObserver(main);
        //TODO iedere controller hoort zijn eigen view te hebben ...
        this.advanceTimeController = new AdvanceTimeController(main);
        this.inputParserHandler = new InputParserController(main);
        this.newProjectController = new NewProjectController(main);
        this.newTaskController = new NewTaskController(main);
        this.showProjectsController = new ShowProjectsController(model,main);
        this.updateTaskController = new UpdateTaskController(main);

        this.mainController = new MainController(main);
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

    public AdvanceTimeController getAdvanceTimeController() {
        return advanceTimeController;
    }

    public InputParserController getInputParserHandler() {
        return inputParserHandler;
    }

    public NewProjectController getNewProjectController() {
        return newProjectController;
    }

    public NewTaskController getNewTaskController() {
        return newTaskController;
    }

    public UpdateTaskController getUpdateTaskController() {
        return updateTaskController;
    }

    public ShowProjectsController getShowProjectsController() {
        return showProjectsController;
    }

    public MainController getMainController() {
        return mainController;
    }

    private final AdvanceTimeController advanceTimeController;
    private final InputParserController inputParserHandler;
    private final NewProjectController newProjectController;
    private final NewTaskController newTaskController;
    private final ShowProjectsController showProjectsController;
    private final UpdateTaskController updateTaskController;
    private final MainController mainController;
}