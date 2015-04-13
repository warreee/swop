package be.swop.groep11.main.resource;

/**
 * Created by Ronald on 9/04/2015.
 */
public class UnsatisfiableRequirementException extends RuntimeException {
    private final ResourceRequirement req;

    public UnsatisfiableRequirementException(ResourceRequirement req){
        //TODO copy of?
        //TODO onderscheid/feedback indien oorzak geen overlapping dailyAvailability
        this.req = req;
    }

    public ResourceRequirement getResourceRequirement() {
        return req;
    }
}
