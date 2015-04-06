package be.swop.groep11.main;

import java.util.List;

/**
 * Created by robin on 5/04/15.
 */
public abstract class ResourceTypeConstraint {

    public abstract boolean isSatisfied(List<ResourceTypeRequirement> requirements);
    public abstract void resolve(List<ResourceTypeRequirement> requirements);

}
