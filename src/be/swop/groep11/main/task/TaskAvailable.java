package be.swop.groep11.main.task;

import be.swop.groep11.main.task.TaskStatus2;

/**
 * Created by warreee on 4/7/15.
 */
public class TaskAvailable extends TaskStatus2 {


    @Override
    public String toString() {
        return StatusName.AVAILABLE.toString();
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
