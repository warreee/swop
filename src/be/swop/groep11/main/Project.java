package be.swop.groep11.main;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;

/**
 * Created by warreee on 23/02/15.
 */
public class Project {
    public String getName() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public LocalDateTime getCreationTime() {
        return null;
    }

    public LocalDateTime getDueTime() {
        return null;
    }

    public User getCreator() {
        return null;
    }

    public ProjectStatus getStatus() {
        return null;
    }

    public void addTask(String name, String description, double acceptableDeviation, LocalDateTime startTime, LocalDateTime endTime) {
    }

    public ImmutableList<Task> getTasks() {
        return null;
    }

    public void finish() {
    }

    public static boolean isValidName(String validName) {
        return false;
    }
}
