package be.swop.groep11.main;


import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by warreee on 23/02/15.
 */
public class ProjectManager {

    private ArrayList<Project> projects = new ArrayList<Project>();

    public ImmutableList<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    public void addProject(String name, String description, LocalDateTime creationTime, LocalDateTime duetime, User user) {
        if (Project.isValidStartTimeEndTime(creationTime, duetime)){
            throw new IllegalArgumentException("Eindtijd kan niet voor starttijd liggen.");
        }
    }
}
