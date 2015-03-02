package be.swop.groep11.main;

import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.commands.CommandOutput;
import be.swop.groep11.main.commands.IllegalCommandException;
import be.swop.groep11.main.handler.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Ronald on 22/02/2015.
 */
public class CommandLine {


    public static void main(String[] args){
        CommandLine cli = new CommandLine();
        cli.startCommandLoop();
    }

    public CommandLine() {
        this.mainHandler = new MainHandler();
        this.advanceTimeHandler = new AdvanceTimeHandler();
        this.inputParserHandler = new InputParserHandler();
        this.newProjectHandler = new NewProjectHandler();
        this.newTaskHandler = new NewTaskHandler();
        this.showProjectsHandler = new ShowProjectsHandler();
        this.updateTaskHandler = new UpdateTaskHandler();

        this.commandOutput = new CommandOutput();

        advanceTimeHandler.addObserver(commandOutput);
        inputParserHandler.addObserver(commandOutput);
        newProjectHandler.addObserver(commandOutput);
        newTaskHandler.addObserver(commandOutput);
        showProjectsHandler.addObserver(commandOutput);
        updateTaskHandler.addObserver(commandOutput);
        mainHandler.addObserver(commandOutput);

        setCurrentHandler(mainHandler);
    }

    private void setCurrentHandler(Handler handler) {
        if(canHaveAsCurrentHandler(mainHandler)){
            this.currentHandler = handler;
        }
    }

    private boolean canHaveAsCurrentHandler(MainHandler mainHandler) {
        //TODO implement
        return true;
    }

    private Handler currentHandler;


    private void startCommandLoop(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            boolean quit = false;
            String input;
            Command cmd;
            while (!quit) {
                input = in.readLine();
                try {
                    cmd = Command.getCommand(input);
//                    switch (cmd) {
//                        case SHOWPROJECTS:
//                            System.out.println("SWITCH: SHOWPROJECTS");
//                            break;
//                        case SELECTPROJECT:
//                            System.out.println(cmd.toString());
//                            cmd.printParams();
//                            break;
//                        case SELECTTASK:
//                            System.out.println(cmd.toString());
//                            cmd.printParams();
//                            break;
//                        case NEWPROJECTS:
//                            System.out.println("SWITCH: NEWPROJECTS");
//                            break;
//                        case NEWTASK:
//                            System.out.println("SWITCH: NEWTASK");
//                            break;
//                        case UPDATETASK:
//                            System.out.println("SWITCH: UPDATETASK");
//                            break;
//                        case ADVANCETIME:
//                            System.out.println("SWITCH: ADVANCETIME");
//                            break;
//                        case CANCEL:
//                            System.out.println("SWITCH: CANCEL");
//                            break;
//                        case EXIT:
//                            quit = true;
//                            break;
//                    }
                    cmd.resolve(currentHandler);
//                    foo();
                } catch (IllegalCommandException e) {
                    System.out.println("Catching exception");
                    System.out.println("Invalid input: " + e.getInput().toString());
                }finally {

                }
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private Queue<String> queue = new LinkedList<String>();

    public void addToOutputQueue(StringBuilder sb){
        if(sb != null){
            queue.add(sb.toString());
        }else{
            throw new IllegalArgumentException("Invalid StringBuilder.s");
        }
    }

    private void foo(){
        String toPrint = queue.poll();
        while(toPrint != null){
            System.out.println(toPrint);
            toPrint = queue.poll();
        }
    }

    private final AdvanceTimeHandler advanceTimeHandler;
    private final InputParserHandler inputParserHandler;
    private final NewProjectHandler newProjectHandler;
    private final NewTaskHandler newTaskHandler;
    private final ShowProjectsHandler showProjectsHandler;
    private final UpdateTaskHandler updateTaskHandler;
    private final CommandOutput commandOutput;
    private final MainHandler mainHandler;
}

