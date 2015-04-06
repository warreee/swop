package be.swop.groep11.main;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by robin on 2/04/15.
 */
public class ResourceType {

    /**
     * Gemakkelijksheidconstructor om een ResourceType aan te maken zonder restricties op de beschikbaarheid van dit
     * ResourceType.
     *
     * @param name
     * @param requirements
     */
    public ResourceType(String name, List<ResourceTypeRequirement> requirements){
        this(name, requirements, null, null);
    }

    public ResourceType(String name, List<ResourceTypeRequirement> requirements, LocalDateTime availableFrom, LocalDateTime availableUntil) {
        setName(name);
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

    private List<ResourceTypeRequirement> requirements;

    public List<ResourceTypeRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<ResourceTypeRequirement> requirements) {
        this.requirements = requirements;
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
