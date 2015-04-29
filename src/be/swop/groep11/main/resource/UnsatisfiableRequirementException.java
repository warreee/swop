package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 9/04/2015.
 */
public class UnsatisfiableRequirementException extends RuntimeException {
    private final ResourceRequirement req;

    public UnsatisfiableRequirementException(AResourceType requiredType, int amount) {
        //TODO onderscheid/feedback indien oorzaak geen overlapping dailyAvailability ?
        this.req = new ResourceRequirement(requiredType,amount);
    }

    public ResourceRequirement getResourceRequirement() {
        return req;
    }
}
