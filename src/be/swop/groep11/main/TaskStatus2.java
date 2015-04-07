package be.swop.groep11.main;

/**
 * Created by warreee on 4/7/15.
 */
public abstract class TaskStatus2 {

    protected enum StatusName {
        AVAILABLE,
        EXECUTING,
        UNAVAILABLE,
        FINISHED,
        FAILED;
    }

    public abstract String toString();

    public abstract boolean execute();

    public abstract boolean finish();

    public abstract boolean fail();

    public boolean checkDependencies(){
        return true;
    }

    public static boolean isValidNewStatus(TaskStatus2 status, Task task) {
        //TaskStatus2 currentStatus = task.getStatus();
        return true;
    }
}
