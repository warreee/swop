package be.swop.groep11.main;

import java.util.List;

/**
 * Created by Ronald on 3/04/2015.
 */
public abstract class ResourceTypeConstraint {


    /**
     * Controleer dat deze ResourceTypeConstraint voldoet a.d.h.v. gegeven lijst ResourceTypeRequirement
     *
     * @param requirements   De lijst ResourceTypeRequirements
     * @return
     */
    public abstract boolean isSatisfied(List<ResourceTypeRequirement> requirements);

    public abstract List<ResourceTypeRequirement> resolve(List<ResourceTypeRequirement> requirements);
}
