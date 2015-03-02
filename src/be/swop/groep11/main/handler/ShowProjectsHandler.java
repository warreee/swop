package be.swop.groep11.main.handler;


import be.swop.groep11.main.commands.Command;

/**
 * Created by Ronald on 28/02/2015.
 */
public class ShowProjectsHandler extends Handler {

    public Handler resolveCommand(Command cmd ){
        switch (cmd) {
            case SHOWPROJECTS:
                System.out.println("show projects only");
                break;
            case SELECTPROJECT:
                String output = "Selecting project \n" + "first param: " + cmd.getParameter("PID") + " " +"second param: " + cmd.getParameter("FOO");
//                System.out.println("Selecting project");
//                System.out.println("first param: " + cmd.getParameter("PID"));
//                System.out.println("second param: " + cmd.getParameter("FOO"));
                setChanged();
                notifyObservers(output);
                break;
            case SELECTTASK:
                break;
            case CANCEL:
                break;
        }
        return null;
    }
}
