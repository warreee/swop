package be.swop.groep11.main;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by robin on 2/04/15.
 */
public class ResourceType {

    /**
     * Gemakkelijksconstructor om een ResourceType aan te maken zonder restricties op de beschikbaarheid van dit
     * ResourceType.
     *
     * @param name
     * @param conflictsWith
     * @param requires
     */
    public ResourceType(String name, List<ResourceType> conflictsWith, List<ResourceType> requires){
        this(name, conflictsWith, requires, null, null);
    }

    public ResourceType(String name, List<ResourceType> conflictsWith, List<ResourceType> requires, LocalDateTime availableFrom, LocalDateTime availableUntil) {
        setName(name);
        setConflictsWidth(conflictsWith);
        setRequires(requires);
        setAvailableFrom(availableFrom);
        setAvailableUntil(availableUntil);

    }

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isValidName(String name){
        return name != null && !name.isEmpty();
    }

    // TODO: Constraints enz.
    public void setConflictsWidth(List<ResourceType> conflictsWidth) {
        this.conflictsWidth = conflictsWidth;
    }

    public void setRequires(List<ResourceType> requires) {
        this.requires = requires;
    }

    private LocalDateTime availableFrom;

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    private LocalDateTime availableUntil;

    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(LocalDateTime availableUntil) {
        this.availableUntil = availableUntil;
    }
}
