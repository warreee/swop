package be.swop.groep11.main.task;

/**
 * Created by warreee on 4/7/15.
 */
public abstract class TaskStatus2 {

    public abstract void execute(Task task);

    public abstract void finish(Task task);

    public abstract void fail(Task task);

    public abstract void makeAvailable(Task task);

    public abstract void makeUnavailable(Task task);
}
