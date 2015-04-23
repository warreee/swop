package be.swop.groep11.main.controllers;

import be.swop.groep11.main.actions.ActionBehaviourMapping;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.resource.ResourceManager;

/**
 * Created by Ronald on 22/04/2015.
 */
public class PlanningController extends AbstractController {

    private ProjectRepository projectRepository;
    private ResourceManager resourceManager;

    public PlanningController(ActionBehaviourMapping actionBehaviourMapping,ProjectRepository projectRepository,ResourceManager resourceManager) {
        super(actionBehaviourMapping);
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
    }

    //TODO implement resolve & plan

    public void planTask(){

    }

    public void resolveConflict(){

    }
}
