package be.swop.groep11.main;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskUnavailable extends TaskStatus2 {
    @Override
    public String toString() {
        return StatusName.UNAVAILABLE.toString();
    }

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public boolean finish() {
        return false;
    }

    @Override
    public boolean fail() {
        return false;
    }
}
