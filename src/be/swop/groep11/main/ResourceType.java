package be.swop.groep11.main;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
     * @param requiredTypes
     * @param conflictingTypes
     */
    public ResourceType(String name, List<RequirementConstraint> requiredTypes, List<ConflictConstraint> conflictingTypes){
        this(name, requiredTypes, conflictingTypes, null, null);
    }

    /**
     * Maakt een nieuwe ResourceType aan met de gegeven paramters.
     *
     * @param name De naam van deze ResourceType
     * @param requiredTypes
     * @param conflictingTypes
     * @param availableFrom
     * @param availableUntil
     */
    public ResourceType(String name, List<RequirementConstraint> requiredTypes, List<ConflictConstraint> conflictingTypes, LocalDateTime availableFrom, LocalDateTime availableUntil) {
        setName(name);
        setRequiredTypes(requiredTypes);
        setConflictingTypes(conflictingTypes);
        setAvailableFrom(availableFrom);
        setAvailableUntil(availableUntil);
    }

    private ArrayList<ResourceInstance> instances = new ArrayList<>();

    /**
     * Voegt een nieuwe instantie van dit type resource toe aan deze ResourceType.
     * @param name
     */
    public void addInstance(String name){
        //TODO: implementeren.
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

    private List<RequirementConstraint> requiredTypes;

    public List<RequirementConstraint> getRequiredTypes() {
        return requiredTypes;
    }

    public void setRequiredTypes(List<RequirementConstraint> requiredTypes) {
        this.requiredTypes = requiredTypes;
    }

    private List<ConflictConstraint> conflictingTypes;

    public List<ConflictConstraint> getConflictingTypes() {
        return conflictingTypes;
    }

    public void setConflictingTypes(List<ConflictConstraint> conflictingTypes) {
        this.conflictingTypes = conflictingTypes;
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
