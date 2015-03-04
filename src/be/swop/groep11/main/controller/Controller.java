package be.swop.groep11.main.controller;

import be.swop.groep11.main.commands.Command;
import be.swop.groep11.main.commands.IllegalCommandException;
import be.swop.groep11.main.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Controller{
    protected final View view;
    protected Command escapeCommand;

    private List<Command> acceptedCommands;
    private boolean wantsToQuit;

    protected Controller(View view) {
        this.view = view;
        this.escapeCommand = Command.EXIT;
        this.acceptedCommands = new ArrayList<>();
        this.wantsToQuit = false;
        addAcceptedCommands(Command.EXIT,Command.CANCEL,Command.HELP);
    }

    public boolean resolveCommand(Command cmd) throws IOException {
        if (!acceptedCommands.contains(cmd)) throw new IllegalCommandException("Ongeldig command.");
        if (cmd == escapeCommand) {
            setWantsToQuit(true);
        }
        switch (cmd) {
            case EXIT:
                System.exit(0);
                break;
            case HELP:
                view.print(getControllerInfo());
                view.print(printAcceptedCommands());
                break;
        }
        return wantsToQuit;
    }

    protected void addAcceptedCommands(Command...cmds){
        if(cmds != null)
            Collections.addAll(this.acceptedCommands, cmds);
    }

    public void run(){
        Command cmd;
        boolean returnedFromController = false;
        view.print(printAcceptedCommands());
        try {
            while(!wantsToQuit ){
                if(returnedFromController){
                    view.print("Je bent terug in de " + getControllerInfo());
                }
                try {
                    cmd = Command.getCommand(view.prompt(""));
                    returnedFromController = resolveCommand(cmd);

                } catch (IllegalCommandException e) {
                    view.print("Er ging iets mis: " + e.getInput());
                }
            }
        } catch (IOException e) {
            //TODO correct error handeling?
            e.printStackTrace();
        }
    }
    //TODO ondersteuning voor intro bericht wanneer nieuwe controller actief wordt.
    protected String printAcceptedCommands() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dit zijn de mogelijke commands:" + "\n");
        for(Command cmd: acceptedCommands){
            sb.append(" | ");
            sb.append(cmd.toString());
        }
        sb.append(" | ");
        return sb.toString();
    }

    protected abstract String getControllerInfo();

    public void setWantsToQuit(boolean wantsToQuit) {
        this.wantsToQuit = wantsToQuit;
    }
}
