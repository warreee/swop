package be.swop.groep11.test;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Company;
import be.swop.groep11.main.core.ProjectRepository;
import be.swop.groep11.main.core.SystemTime;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ronald on 18/05/2015.
 */
public class temp {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testName() throws Exception {

        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();

        String stypeA = "TypeA";
        String stypeB = "TypeB";
        String stypeC = "TypeC";


        resourceTypeRepository.addNewResourceType(stypeA);
        resourceTypeRepository.addNewResourceType(stypeB);
        resourceTypeRepository.addNewResourceType(stypeC);

        AResourceType typeA = resourceTypeRepository.getResourceTypeByName(stypeA);
        AResourceType typeB = resourceTypeRepository.getResourceTypeByName(stypeB);
        AResourceType typeC = resourceTypeRepository.getResourceTypeByName(stypeC);

        SystemTime systemTime = new SystemTime();

        Company company = new Company("company", resourceTypeRepository, systemTime);

        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        ResourceRepository resourceRepository = new ResourceRepository(resourceTypeRepository);

        resourceRepository.addResourceInstance(new Resource("resA1",typeA));
//        resourceRepository.addResourceInstance(new Resource("resA2",typeA));
//        resourceRepository.addResourceInstance(new Resource("resA3",typeA));

        resourceRepository.addResourceInstance(new Resource("resB1",typeB));
//        resourceRepository.addResourceInstance(new Resource("resB2",typeB));
//        resourceRepository.addResourceInstance(new Resource("resB3",typeB));

        resourceRepository.addResourceInstance(new Resource("resC1",typeC));
//        resourceRepository.addResourceInstance(new Resource("resC2",typeC));
//        resourceRepository.addResourceInstance(new Resource("resC3",typeC));

        ResourcePlanner resourcePlanner = new ResourcePlanner(resourceRepository, systemTime);

        BranchOffice branchOffice = new BranchOffice("swop", "leuven", projectRepository, resourcePlanner);
        AResourceType devType = resourceTypeRepository.getDeveloperType();
        branchOffice.addEmployee(new Developer("DEVA",devType));
        branchOffice.addEmployee(new Developer("DEVB",devType));
        branchOffice.addEmployee(new Developer("DEVC",devType));

        branchOffice.addEmployee(new ProjectManager("PM1"));

        company.addBranchOffice(branchOffice);

        LocalDateTime projectStart =  LocalDateTime.of(2016,1,1,12,0);
        LocalDateTime projectEnd = LocalDateTime.of(2016,12,31,12,0);

        String projectDescript = "projectOmschrijving";
        String projectName = "projectNaam";
        branchOffice.getProjectRepository().addNewProject(projectName, projectDescript, projectStart, projectEnd);

        Duration estDur = Duration.ofDays(10);
        double acceptDevia = 10;
        String taskDescript = "TaskOmschrijving";
        RequirementListBuilder requirementListBuilder = new RequirementListBuilder(resourceRepository);

        requirementListBuilder.addNewRequirement(typeA, 1);
        requirementListBuilder.addNewRequirement(typeB, 1);
        requirementListBuilder.addNewRequirement(typeC, 1);

        IRequirementList reqList = requirementListBuilder.getRequirements();

        branchOffice.getProjectRepository().getProjects().get(0).addNewTask(taskDescript, acceptDevia, estDur, reqList);
        Task task = branchOffice.getProjectRepository().getProjects().get(0).getTasks().get(0);

        assertTrue(resourcePlanner.hasEnoughResourcesToPlan(task));

        List<LocalDateTime> list = resourcePlanner.getNextPossibleStartTimes(task.getRequirementList(), task.getEstimatedDuration(), 3);

        PlanBuilder planBuilder = new PlanBuilder(branchOffice, task, list.get(0));
        planBuilder.proposeResources();
        Plan plan = planBuilder.getPlan();

//        System.out.println(plan.getReservations());

        resourcePlanner.addPlan(plan);


        //TASK B
        Duration estDurB = Duration.ofDays(25);
        double acceptDeviaB = 10;
        String taskDescriptB = "TaskOmschrijvingB";

        branchOffice.getProjectRepository().getProjects().get(0).addNewTask(taskDescriptB, acceptDeviaB, estDurB, reqList);
        Task taskB = branchOffice.getProjectRepository().getProjects().get(0).getTasks().get(1);


        List<LocalDateTime> listB = resourcePlanner.getNextPossibleStartTimes(taskB.getRequirementList(), taskB.getEstimatedDuration(), 3);


        PlanBuilder planBuilderB = new PlanBuilder(branchOffice, taskB, listB.get(0));
        planBuilderB.proposeResources();
        Plan planB = planBuilderB.getPlan();

        resourcePlanner.addPlan(planB);



    }
}
