package be.swop.groep11.main.util;

import be.swop.groep11.main.core.*;
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

import static org.mockito.Mockito.mock;

/**
 * Klasse voor het inlezen van de de input file, deze moet staan in /input met de naam input.tman
 */
public class InputParser {
    private ProjectRepository projectRepository;
    private ArrayList<Project> projectList = new ArrayList<>();
    private ArrayList<Task> taskList = new ArrayList<>();
    private ArrayList<AResourceType> resourceTypeList = new ArrayList<>();
    private ArrayList<ResourceInstance> resourceInstanceList = new ArrayList<>();
    private ArrayList<DailyAvailability> dailyAvailabilityList = new ArrayList<>();
    private ArrayList<ResourceInstance> developerList = new ArrayList<>();
    private ArrayList<Map<String, Object>> planningsList = new ArrayList<>();
    private Map<Integer, Map<Integer, Map<String, LocalDateTime>>> reservationsMap = new HashMap<>();
    private ResourceManager resourceManager;
    private SystemTime systemTime;

    /**
     * Initialiseerd deze inputparser.
     * @param projectRepository De ProjectRepository waaraan alle projecten moeten worden toegevoegd.
     * @param resourceManager De ResourceManager waaraan alle Resources en reservaties moeten worden toegevoegd.
     */
    public InputParser(ProjectRepository projectRepository, ResourceManager resourceManager, SystemTime systemTime) {
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
        this.systemTime = systemTime;
    }


    public static void main(String[] args) throws FileNotFoundException {
        SystemTime systemTime = new SystemTime(LocalDateTime.MIN);
        ResourceManager typeRepo = new ResourceManager();
        BranchOffice branchOffice = mock(BranchOffice.class); // TODO: echte branchoffice erinsteken!
        ProjectRepository projectRepository = new ProjectRepository(systemTime, branchOffice);

        InputParser parser = new InputParser(projectRepository, typeRepo, systemTime);
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
        String path = Paths.get("input/input.tman").toAbsolutePath().toString();

        Map<String, Object> values;

        try {
            values = (Map<String, Object>) yaml.load(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return; // Return omdat de inputfile niet gelezen kon worden.
        }

        // De volgorde is van belang.
        handleDevelopers(values);
        handleDailyAvailability(values);
        handleSystemTime(values);
        handleCompany(values);
        handleReservations(values);
        handlePlannings(values);
        handleResourceTypes(values);
        handleResources(values);
        handleProjects(values);
        handleTasks(values);
    }

    private void handleCompany(Map<String, Object> companyMap){
        String name = (String) companyMap.get("company");
        // TODO: in company object steken.
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
            addDeveloper((Map<String, String>) developer);
        }
    }

    /**
     * Leest alle plannings in.
     * Dit moet opgeroepen worden na handleTasks (Anders is de lijst met wat aan welke taak moet worden gelinked niet
     * beschikbaar).
     * @param planningsMap
     */
    private void handlePlannings(Map<String, Object> planningsMap){
        ArrayList plannings = (ArrayList) planningsMap.get("plannings");
        for (int i = 0; i < plannings.size(); i++){
            planningsList.add(new HashMap<>());
            Map<String, Object> planningMap = (Map<String, Object>) plannings.get(i);
            Map<String, Object> planningListMap = planningsList.get(planningsList.size() - 1);

            planningListMap.put("plannedStartTime", parseTime((String) planningMap.get("plannedStartTime")));
            planningListMap.put("developers", planningMap.get("developers"));
            HashMap<Integer, Integer> resourcesMap = new HashMap<>();
            planningListMap.put("resources", resourcesMap);
            for(Map<Integer, Integer> rMap: (ArrayList<Map<Integer, Integer>>) planningMap.get("resources")){
                resourcesMap.put(rMap.get("type"), rMap.get("quantity"));
            }
        }
        return;
    }

    /**
     * Leest alle projecten in.
     * @param projectsMap
     */
    private void handleProjects(Map<String, Object> projectsMap){
        ArrayList projects = (ArrayList) projectsMap.get("projects");
        for (Object project : projects) {
            addProject((Map<String, String>) project);
        }
    }

    /**
     * Leest alle reservaties in.
     * @param reservationsMap
     */
    private void handleReservations(Map<String, Object> reservationsMap){
        ArrayList reservations = (ArrayList) reservationsMap.get("reservations");
        for(Object reservation: reservations){
            Map<String, Object> reservationMap = (Map<String, Object>) reservation;
            Integer taskId = (Integer) reservationMap.get("task");
            Integer resourceID = (Integer) reservationMap.get("resource");
            this.reservationsMap.putIfAbsent(taskId, new HashMap<>());
            this.reservationsMap.get(taskId).putIfAbsent(resourceID, new HashMap<>());
            this.reservationsMap.get(taskId).get(resourceID).put("startTime", parseTime((String) reservationMap.get("startTime")));
            this.reservationsMap.get(taskId).get(resourceID).put("endTime", parseTime((String) reservationMap.get("endTime")));
        }
        return;
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
            addResourceType(i, (Map<String, Object>) resourceTypes.get(i));
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
    private void addDeveloper(Map<String, String> propertiesList){
        /* TODO dit werkt niet meer, maar de input parser moet toch aangepast worden...
        String name = propertiesList.get("name");
        AResourceType resourceType = resourceManager.getDeveloperType();
        resourceManager.addResourceInstance(resourceType, name);
        developerList.add(resourceManager.getDeveloperType().getResourceInstances().get(resourceManager.getDeveloperType().getResourceInstances().size() - 1));
        */
    }

    /**
     * Gebruikt de projectrepostory om een project toe te voegen aan het systeem
     *
     * @param propertiesList de eigenschappen van het project ingelezen vanuit de tman file.
     */
    private void addProject(Map<String, String> propertiesList) {

        String name = propertiesList.get("name");
        String description = propertiesList.get("description");
        LocalDateTime creationTime = parseTime(propertiesList.get("creationTime"));
        LocalDateTime dueTime = parseTime(propertiesList.get("dueTime"));

        projectRepository.addNewProject(name, description, creationTime, dueTime);

        // Haalt het laatst toegevoegde project op.
        projectList.add(projectRepository.getProjects().get(projectRepository.getProjects().size() - 1));
    }

    /**
     * Voegt een nieuwe ResourceInstance toe aan een bestaand ResourceType.
     * @param propertiesList
     */
    private void addResource(Map<String, Object> propertiesList){
        String name = (String) propertiesList.get("name");
        AResourceType resourceType = resourceTypeList.get((Integer) propertiesList.get("type"));
        resourceManager.addResourceInstance(resourceType, name);
        int size = resourceType.getResourceInstances().size();
        resourceInstanceList.add(resourceType.getResourceInstances().get(size - 1));
    }

    /**
     * Voegt een nieuw ResourceType toe.
     * @param propertiesList
     */
    private void addResourceType(int number, Map<String, Object> propertiesList){
        /* TODO dit werkt niet meer, maar de input parser moet toch aangepast worden...
        // Lees alle info uit de map.
        String name = (String) propertiesList.get("name");
        ArrayList requires = (ArrayList) propertiesList.get("requires");
        ArrayList conflictsWith = (ArrayList) propertiesList.get("conflictsWith");
        Integer dailyAvailability = (Integer) propertiesList.get("dailyAvailability");

        // Haal de correcte IResourceTypes op die al bekend zijn. (Dit faalt indien er fouten staan in de inputfile.)
        ArrayList<AResourceType> req = new ArrayList<>();
        ArrayList<AResourceType> con = new ArrayList<>();
        requires.forEach(x -> req.add(resourceTypeList.get((Integer) x)));
        conflictsWith.forEach(x -> {
            if ((Integer) x != number) {
                con.add(resourceTypeList.get((Integer) x));
            }
        });

        // Voeg de IResourceType eindelijk toe.
        if(dailyAvailability == null){
            // Er is geen dailyAvailability.
            resourceManager.addNewResourceType(name, req, con);
        } else {
            // Er is wel een dailyAvailability.
            DailyAvailability da = dailyAvailabilityList.get(Integer.valueOf(dailyAvailability));
            resourceManager.addNewResourceType(name, da, req, con);
        }

        // Indien het IResourceType conflicteerd met zichzelf, voeg dit dan toe.
        AResourceType type = resourceManager.getResourceTypeByName(name);
        if(conflictsWith.contains(number)){
            resourceManager.withConflictConstraint(type, type);
        }

        resourceTypeList.add(resourceManager.getResourceTypeByName(name));
        */
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
        RequirementListBuilder requirementListBuilder = new RequirementListBuilder();
        requirementListBuilder.addNewRequirement(resourceManager.getDeveloperType(), 1);
        project.addNewTask(description, acceptableDeviation, duration, requirementListBuilder.getRequirements());
        taskList.add(project.getTasks().get(project.getTasks().size() - 1));
        Task task = taskList.get(taskList.size() - 1);
        ArrayList<Developer> taskDevelopers = new ArrayList<>();
        LocalDateTime plannedStartTime = null;
        /*
        try{
            Integer number = (Integer) propertiesList.get("planning");
            if(number != null) {
                Map<String, Object> planning = planningsList.get(number);
                RequirementListBuilder builder = new RequirementListBuilder();
                for (Integer i : ((Map<Integer, Integer>) planning.get("resources")).keySet()) {
                    builder.addNewRequirement(resourceTypeList.get(i), ((Map<Integer, Integer>) planning.get("resources")).get(i));
                }
                for (Integer i : (ArrayList<Integer>) planning.get("developers")) {
                    taskDevelopers.add((Developer) developerList.get(i));
                }
                builder.addNewRequirement(resourceManager.getDeveloperType(), taskDevelopers.size());
                task.setRequirementList(builder.getRequirements());
                plannedStartTime = (LocalDateTime) planning.get("plannedStartTime");
            }
        } catch (NumberFormatException e){
            // Doe niks, het nummer van planning kon niet omgevormd worden.
        }

        if(reservationsMap.containsKey(taskList.size() - 1)){
            // Als er reservaties zijn voor deze taak voeren we die uit.
            if(plannedStartTime == null){
                throw new IllegalArgumentException("Om reservaties uit te voeren is een geplande starttijd nodig.");
            }
            OldPlan plan = resourceManager.getNextPlans(1, task, plannedStartTime).get(0);
            plan.changeReservations(new ArrayList<>());
            Map<Integer, Map<String, LocalDateTime>> reservations = reservationsMap.get(taskList.size() - 1);
            for(Integer i: reservations.keySet()){
                plan.addReservation(resourceInstanceList.get(i));
            }
            for(Developer developer: taskDevelopers){
                plan.addReservation(developer);
            }
//            task.plan(plan);
            plan.applyReservations();
        }

        if(propertiesList.containsKey("prerequisiteTasks") && propertiesList.get("prerequisiteTasks") != null) {
            for (int i : (ArrayList<Integer>) propertiesList.get("prerequisiteTasks")) {
                task.addNewDependencyConstraint(taskList.get(i));
            }
        }

        if(propertiesList.containsKey("startTime")){
            task.executeAction(parseTime((String) propertiesList.get("startTime")));
        }

        if(propertiesList.containsKey("endTime")){
            if (propertiesList.get("status").equals("finished")) {
                task.finish(parseTime((String) propertiesList.get("endTime")));
            } else if(propertiesList.get("status").equals("failed")) {
                task.fail(parseTime((String) propertiesList.get("endTime")));
            } else {
                throw new IllegalArgumentException("Onbekende status bij het zetten van een eindtijd.");
            }

        }

        if(propertiesList.containsKey("alternativeFor") && propertiesList.get("alternativeFor") != null){
            try {
                taskList.get((Integer) propertiesList.get("alternativeFor")).setAlternativeTask(task);
            } catch (NumberFormatException e){
                // Doe niks, de sleutel alternativeFor was waarschijnlijk leeg.
            } catch (IllegalDependencyException e){
                //debug
            }
        }
        */
    }

    /**
     * Geeft de SystemTime terug die uit input.tman werd gehaald. Dit kan null zijn indien er geen aanwezig was.
     */
    public SystemTime getSystemTime(){
        return this.systemTime;
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
