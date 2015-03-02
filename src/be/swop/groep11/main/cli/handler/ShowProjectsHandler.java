package be.swop.groep11.main.cli.handler;


import be.swop.groep11.main.cli.Command;

/**
 * Created by Ronald on 28/02/2015.
 */
public class ShowProjectsHandler implements Handler{
    public static ShowProjectsHandler getHandler() {
        return handler;
    }

    /**
     * Singleton handler
     */
    private static ShowProjectsHandler handler = new ShowProjectsHandler();
    public void resolveCommand(Command cmd ){
        switch (cmd) {
            case SHOWPROJECTS:
                System.out.println("show projects only");
                break;
            case SELECTPROJECT:
                System.out.println("Selecting project");
                System.out.println("first param: " + cmd.getParameter("PID"));
                System.out.println("second param: " + cmd.getParameter("FOO"));

                break;
            case SELECTTASK:
                break;
            case CANCEL:
                break;
        }
    }
}
