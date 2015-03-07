package be.swop.groep11.main.ui.handler;

import be.swop.groep11.main.ui.commands.Command;
import be.swop.groep11.main.ui.commands.IllegalCommandException;
import be.swop.groep11.main.ui.SimpleTUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Handler {
    protected final SimpleTUI simpleTUI;
    protected Command escapeCommand;

    private List<Command> acceptedCommands;
    private boolean wantsToQuit;

    protected Handler(SimpleTUI simpleTUI) {
        this.simpleTUI = simpleTUI;
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
                simpleTUI.print(getControllerInfo());
                simpleTUI.print(printAcceptedCommands());
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
        simpleTUI.print(printAcceptedCommands());
        try {
            while(!wantsToQuit ){
                if(returnedFromController){
                    simpleTUI.print("Je bent terug in de " + getControllerInfo());
                }
                try {
                    cmd = Command.getCommand(simpleTUI.prompt(""));
                    returnedFromController = resolveCommand(cmd);

                } catch (IllegalCommandException e) {
                    simpleTUI.print("Er ging iets mis: " + e.getInput());
                }
            }
        } catch (IOException e) {
            //TODO correct error handeling?
            e.printStackTrace();
        }
    }
    //TODO ondersteuning voor intro bericht wanneer nieuwe handler actief wordt.
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
