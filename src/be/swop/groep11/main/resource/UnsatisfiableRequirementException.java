package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 9/04/2015.
 */
public class UnsatisfiableRequirementException extends RuntimeException {
    private final ResourceRequirement req;

    public UnsatisfiableRequirementException(IResourceType requiredType, int amount) {
        //TODO onderscheid/feedback indien oorzak geen overlapping dailyAvailability ?
        this.req = new ResourceRequirement(requiredType,amount);
    }

    public ResourceRequirement getResourceRequirement() {
        return req;
    }
}
