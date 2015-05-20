package be.swop.groep11.main.util;

import be.swop.groep11.main.core.*;
import be.swop.groep11.main.planning.Plan;
import be.swop.groep11.main.planning.PlanBuilder;
import be.swop.groep11.main.resource.*;
import be.swop.groep11.main.task.Task;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

/**
 * Klasse voor het inlezen van de de input file, deze moet staan in /input met de naam input.tman
 */
public class InputParser {
    private ProjectRepository projectRepository;
    private ArrayList<BranchOffice> branchOfficeList = new ArrayList<>();
    private ArrayList<Project> projectList = new ArrayList<>();
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<AResourceType> resourceTypeList = new ArrayList<>();
    private ArrayList<ResourceInstance> resourceInstanceList = new ArrayList<>();
    private ArrayList<DailyAvailability> dailyAvailabilityList = new ArrayList<>();
    private ArrayList<ResourceInstance> developerList = new ArrayList<>();
    private ArrayList<Map<String, Object>> planningsList = new ArrayList<>();
    private Map<Integer, Map<Integer, Map<String, LocalDateTime>>> reservationsMap = new HashMap<>();
    private SystemTime systemTime;
    private Company company;
    private ResourceTypeRepository resourceTypeRepository;

    /**
     * Initialiseerd deze inputparser.
     * @param projectRepository De ProjectRepository waaraan alle projecten moeten worden toegevoegd.
     */
    public InputParser(ProjectRepository projectRepository, SystemTime systemTime, ResourceTypeRepository resourceTypeRepository) {
        this.projectRepository = projectRepository;
        this.systemTime = systemTime;
        this.resourceTypeRepository = resourceTypeRepository;
    }


    public static void main(String[] args) throws FileNotFoundException {
        SystemTime systemTime = new SystemTime(LocalDateTime.MIN);
        ProjectRepository projectRepository = new ProjectRepository(systemTime);
        ResourceTypeRepository resourceTypeRepository = new ResourceTypeRepository();

        InputParser parser = new InputParser(projectRepository, systemTime, resourceTypeRepository);
        parser.parseInputFile();
        System.out.println("Finished :)");
    }

    /**
     * Leest input.tman in, parset de file en maakt de objecten aan in de meegegeven projectRepository
     *
     * @throws FileNotFoundException indien het input.tman niet op de juiste plaats staat.
     */
    public void parseInputFile() throws FileNotFoundException {

        Yaml yaml = new Yaml();
        String path = Paths.get("input/input_1.yaml").toAbsolutePath().toString();

        Map<String, Object> values;

        try {
            values = (Map<String, Object>) yaml.load(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return; // Return omdat de inputfile niet gelezen kon worden.
        }

        // De volgorde is van belang.
        handleSystemTime(values);
        handleCompany(values);
        handleBranchOffiches(values);
        handleDevelopers(values);
        handleProjectManagers(values);
        handleDailyAvailability(values);
        handleResourceTypes(values);
        handleResources(values);
        handleProjects(values);
        handleTasks(values);
        handlePlans(values);
    }

    private void handleBranchOffiches(Map<String, Object> branchOfficeMap){
        ArrayList branchOffiches = (ArrayList) branchOfficeMap.get("branchOffice");
        for(Object branchOffice: branchOffiches){
            addBranchOffice((Map<String, String>) branchOffice);
        }
    }

    public void handleCompany(Map<String, Object> companyMap){
        String name = (String) companyMap.get("company");
        company = new Company(name, new ResourceTypeRepository(), getSystemTime());
    }

    /**
     * Leest alle DailyAvailabilities in.
     * Dit moet opgeroepen worden voor handleResourceTypes aangezien anders een benodigde lijst nog niet beschikbaar is.
     * @param dailyAvailabilityMap
     */
    private void handleDailyAvailability(Map<String, Object> dailyAvailabilityMap){
        ArrayList dailyAvailabilitys = (ArrayList) dailyAvailabilityMap.get("dailyAvailability");
        for(Object dailyAvailability: dailyAvailabilitys){
            addDailyAvailability((Map<String, String>) dailyAvailability);
        }
    }

    /**
     * Leest alle developers in.
     * @param developersMap
     */
    private void handleDevelopers(Map<String, Object> developersMap){
        ArrayList developers = (ArrayList) developersMap.get("developers");
        for(Object developer: developers){
            addDeveloper((Map<String, Object>) developer);
        }
    }

    /**
     * Leest alle plannings in.
     * Dit moet opgeroepen worden na handleTasks (Anders is de lijst met wat aan welke taak moet worden gelinked niet
     * beschikbaar).
     * @param planningsMap
     */
    private void handlePlans(Map<String, Object> planningsMap){
        ArrayList<Map<String, Object>> plannings = (ArrayList) planningsMap.get("plans");
        for(Map<String, Object> plan: plannings){
            addPlan(plan);
        }
    }

    private void handleProjectManagers(Map<String, Object> projectManagersMap){
        // TODO
    }

    /**
     * Leest alle projecten in.
     * @param projectsMap
     */
    private void handleProjects(Map<String, Object> projectsMap){
        ArrayList projects = (ArrayList) projectsMap.get("projects");
        for (Object project : projects) {
            addProject((Map<String, Object>) project);
        }
    }

    /**
     * Leest alle Resources in.
     * @param resourceMap
     */
    private void handleResources(Map<String, Object> resourceMap){
        ArrayList resources = (ArrayList) resourceMap.get("resources");
        for (Object resource : resources) {
            addResource((Map<String, Object>) resource);
        }
    }

    /**
     * Leest alle ResourceTypes in.
     * @param resourceTypeMap
     */
    private void handleResourceTypes(Map<String, Object> resourceTypeMap){
        ArrayList resourceTypes = (ArrayList) resourceTypeMap.get("resourceTypes");
        for(int i = 0; i < resourceTypes.size(); i++){
            addResourceType((Map<String, Object>) resourceTypes.get(i));
        }
    }

    /**
     * Leest de SystemTime uit input.tman en returnt een correct SystemTime object indien het goed kon geparsed worden.
     * @param systemTimeMap
     * @return
     */
    private void handleSystemTime(Map<String, Object> systemTimeMap){
        String time = (String) systemTimeMap.get("systemTime");
        systemTime.updateSystemTime(parseTime(time));
    }

    /**
     * Leest alle taken in.
     * Voor deze methode wordt opgeroepen moeten alle projecten al ingelezen zijn.
     * @param tasksMap
     */
    private void handleTasks(Map<String, Object> tasksMap){
        ArrayList tasks = (ArrayList) tasksMap.get("tasks");
        for(Object task: tasks){
            addTask((Map<String, Object>) task);
        }
    }

    private void addBranchOffice(Map<String, String> propertiesList){
        String name = propertiesList.get("name");
        String location = propertiesList.get("location");
        ResourcePlanner planner = new ResourcePlanner(new ResourceRepository(resourceTypeRepository), getSystemTime());
        BranchOffice branchOffice = new BranchOffice(name, location, new ProjectRepository(getSystemTime()), planner); // TODO: juiste objecten meegeven
        branchOfficeList.add(branchOffice);
        company.addBranchOffice(branchOffice);
    }

    /**
     * Maakt een nieuwe DailyAvailability aan. Deze wordt bijgehouden in een interne lijst om te gebruiken bij het
     * toevoegen van nieuwe ResourceTypes aan ResourceTypeRepository.
     * @param propertiesList
     */
    private void addDailyAvailability(Map<String, String> propertiesList){
        String startTime = propertiesList.get("startTime");
        String endTime = propertiesList.get("endTime");
        DailyAvailability dailyAvailability = new DailyAvailability(LocalTime.parse(startTime), LocalTime.parse(endTime));
        dailyAvailabilityList.add(dailyAvailability);
    }

    /**
     * Voegt een nieuwe Developer toe aan de gegeven ResourceManager.
     * @param propertiesList
     */
    private void addDeveloper(Map<String, Object> propertiesList){
        String name = (String) propertiesList.get("name");
        int branch = (int) propertiesList.get("branchOffice");
        Developer developer = new Developer(name);
        branchOfficeList.get(branch).addEmployee(developer);
        developerList.add(developer);
    }

    private void addPlan(Map<String, Object> propertiesList){
        BranchOffice branchOffice = branchOfficeList.get((Integer) propertiesList.get("branchOffice"));
        Task task = taskList.get((Integer) propertiesList.get("task"));
        List<ResourceInstance> specific = ((List<Integer>) propertiesList.get("specificResources")).stream()
                .map(resourceInstanceList::get).collect(Collectors.toList());
        List<ResourceInstance> nonSpecific = ((List<Integer>) propertiesList.get("nonSpecificResources")).stream()
                .map(resourceInstanceList::get).collect(Collectors.toList());
        List<ResourceInstance> developers = ((List<Integer>) propertiesList.get("developers")).stream()
                .map(developerList::get).collect(Collectors.toList());
        LocalDateTime startTime = parseTime((String) propertiesList.get("startTime"));
        LocalDateTime endTime = parseTime((String) propertiesList.get("endTime"));

        PlanBuilder builder = new PlanBuilder(branchOffice, task, startTime);
        specific.forEach(builder::addResourceInstance);
        developers.forEach(builder::addResourceInstance);

        Plan plan = builder.getPlan(endTime, nonSpecific);
        branchOffice.getResourcePlanner().addPlan(plan);
        // TODO: Is alles hiervan klaar?
    }

    /**
     * Gebruikt de projectrepostory om een project toe te voegen aan het systeem
     *
     * @param propertiesList de eigenschappen van het project ingelezen vanuit de tman file.
     */
    private void addProject(Map<String, Object> propertiesList) {
        String name = (String) propertiesList.get("name");
        String description = (String) propertiesList.get("description");
        LocalDateTime creationTime = parseTime((String) propertiesList.get("creationTime"));
        LocalDateTime dueTime = parseTime((String) propertiesList.get("dueTime"));
        int branch = (int) propertiesList.get("branchOffice");

        ProjectRepository repo = branchOfficeList.get(branch).getProjectRepository();
        repo.addNewProject(name, description, creationTime, dueTime);

        // Haalt het laatst toegevoegde project op.
        projectList.add(repo.getProjects().get(repo.getProjects().size() - 1));
    }

    private void addProjectManager(Map<String, Object> propertiesList){
        String name = (String) propertiesList.get("name");
        int branch = (int) propertiesList.get("branchOffice");
        ProjectManager manager = new ProjectManager(name);
        branchOfficeList.get(branch).addEmployee(manager);
    }

    /**
     * Voegt een nieuwe ResourceInstance toe aan een bestaand ResourceType.
     * @param propertiesList
     */
    private void addResource(Map<String, Object> propertiesList){
        String name = (String) propertiesList.get("name");
        AResourceType resourceType = resourceTypeList.get((Integer) propertiesList.get("type"));
        BranchOffice branchOffice = branchOfficeList.get((int) propertiesList.get("branchOffice"));
        Resource resource = new Resource(name, resourceType);
        branchOffice.getResourceRepository().addResourceInstance(resource);
        resourceInstanceList.add(resource);
    }

    /**
     * Voegt een nieuw ResourceType toe.
     * @param propertiesList
     */
    private void addResourceType(Map<String, Object> propertiesList){
        // Lees alle info uit de map.
        String name = (String) propertiesList.get("name");
        ArrayList requires = (ArrayList) propertiesList.get("requires");
        ArrayList conflictsWith = (ArrayList) propertiesList.get("conflictsWith");
        Integer dailyAvailability = (Integer) propertiesList.get("dailyAvailability");

        if(dailyAvailability != null){
            resourceTypeRepository.addNewResourceType(name,dailyAvailabilityList.get(dailyAvailability));
        } else {
            resourceTypeRepository.addNewResourceType(name);
        }
        AResourceType type = resourceTypeRepository.getResourceTypeByName(name);
        resourceTypeList.add(type);

        requires.forEach(x -> resourceTypeRepository.withRequirementConstraint(type, resourceTypeList.get((Integer) x)));
        conflictsWith.forEach(x -> resourceTypeRepository.withConflictConstraint(type, resourceTypeList.get((Integer) x)));
    }

    /**
     * Voegt een nieuwe taak toe.
     * Er wordt voor gezorgd dat de toegevoegde taak 1 developer als requirement heeft,
     * indien er geen plan in de input file stond voor die taak.
     * @param propertiesList
     */
    private void addTask(Map<String, Object> propertiesList) {
        // TODO dit werkt niet meer, maar de input parser moet toch aangepast worden...
        String description = (String) propertiesList.get("description");
        Duration duration = Duration.ofMinutes(Long.valueOf(String.valueOf(propertiesList.get("estimatedDuration"))));
        Double acceptableDeviation = Double.valueOf(String.valueOf(propertiesList.get("acceptableDeviation"))) / 100;
        Project project = projectList.get((Integer) propertiesList.get("project"));
        Integer alternative = (Integer) propertiesList.get("alternativeFor");
        ArrayList<Integer> prerequisiteTasks = (ArrayList<Integer>) propertiesList.get("prerequisiteTasks");
        String status = (String) propertiesList.get("status");
        ArrayList<Map<String, Integer>> resourceRequirement = (ArrayList<Map<String, Integer>>) propertiesList.get("resourceRequirements");
        Integer numberDevelopers = (Integer) propertiesList.get("numberDevelopers");
        Integer delegatedTo = (Integer) propertiesList.get("delegatedTo");

        // Maak een RequirementListBuilder en voeg alle requirements daar aan toe.
        RequirementListBuilder builder = new RequirementListBuilder(project.getBranchOffice().getResourceRepository());
        //builder.addNewRequirement(resourceTypeRepository.getDeveloperType(), numberDevelopers); TODO: terug aanzetten wanneer dit terug werkt.
        for(Map<String, Integer> requirement: resourceRequirement){
            AResourceType type = resourceTypeList.get(requirement.get("type"));
            Integer amount = requirement.get("amount");
            builder.addNewRequirement(type, amount);
        }

        project.addNewTask(description, acceptableDeviation, duration, builder.getRequirements());
        taskList.add(project.getTasks().get(project.getTasks().size() - 1));
        Task task = taskList.get(taskList.size() - 1);

        // Voeg alle taken toe op welke deze taak depend.
        prerequisiteTasks.forEach(x -> task.addNewDependencyConstraint(taskList.get(x)));

        // Indien dit een alternatieve taak is voor een andere taak. Zet dan deze taak als alternatief.
        if(alternative != null){
            taskList.get(alternative).setAlternativeTask(task);
        }

        // Indien deze taak geledegeerd is naar een andere branchOffice, delegeer dan deze taak.
        if(delegatedTo != null){
            project.getBranchOffice().delegateTask(task, branchOfficeList.get(delegatedTo));
        }

        // TODO: afwerken
    }

    /**
     * Geeft de SystemTime terug die uit input.tman werd gehaald. Dit kan null zijn indien er geen aanwezig was.
     */
    public SystemTime getSystemTime(){
        return this.systemTime;
    }

    public Company getCompany(){
        return this.company;
    }

    /**
     * Zet een string tijd om in een LocalDateTime object
     *
     * @param date, in stringvorm
     * @return de LocalDateTime
     */
    private LocalDateTime parseTime(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(date, dateTimeFormatter);

        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
