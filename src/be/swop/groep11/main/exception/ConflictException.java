package be.swop.groep11.main.exception;

import be.swop.groep11.main.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception die gegooid wordt indien taken conflicteren.
 */
public class ConflictException extends RuntimeException {

    private ArrayList<Task> conflictingTasks = new ArrayList<>();

    /**
     * Constructor voor een nieuwe ConflictException.
     * @param message   Het bericht mee te geven aan de nieuwe ConflictException.
     * @param conflictingTasks  Lijst van taken die het conflict veroorzaken.
     */
    public ConflictException(String message, List<Task> conflictingTasks) {
        super(message);
        this.conflictingTasks = new ArrayList<>(conflictingTasks);
    }

    public ArrayList<Task> getConflictingTasks() {
        return conflictingTasks;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " " + conflictingTasks.toString();
    }

    //
//    public ConflictException(String message) {
//        super(message);
//    }


}
