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

/**
 * Klasse voor het inlezen van de de input file, deze moet staan in /input met de naam input.tman
 */
public class InputParser {
    User user = new User("InputParser");
    ProjectRepository projectRepository;
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<Task> taskList = new ArrayList<>();
    Map<Integer, Task> planningTaskMap = new HashMap<>();
    ArrayList<AResourceType> resourceTypeList = new ArrayList<>();
    ArrayList<ResourceInstance> resourceInstanceList = new ArrayList<>();
    ArrayList<DailyAvailability> dailyAvailabilityList = new ArrayList<>();
    ArrayList<ResourceInstance> developerList = new ArrayList<>();
    private ResourceManager resourceManager;
    private SystemTime systemTime;

    /**
     * Initialiseerd deze inputparser.
     * @param projectRepository De ProjectRepository waaraan alle projecten moeten worden toegevoegd.
     * @param resourceManager De ResourceManager waaraan alle Resources en reservaties moeten worden toegevoegd.
     */
    public InputParser(ProjectRepository projectRepository, ResourceManager resourceManager,SystemTime systemTime) {
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
        this.systemTime = systemTime;
    }


    public static void main(String[] args) throws FileNotFoundException {
        ResourceManager typeRepo = new ResourceManager();
        SystemTime systemTime = new SystemTime();
        ProjectRepository projectRepository = new ProjectRepository(systemTime);

        InputParser parser = new InputParser(projectRepository, typeRepo,systemTime);
        parser.parseInputFile();
        System.out.println("Finished :)");
    }

    public ProjectRepository getProjectRepository(){
        return this.projectRepository;
    }

    public ResourceManager getResourceManager(){
        return this.getResourceManager();
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
        handleDailyAvailability(values);
        handleSystemTime(values);
        handleResourceTypes(values);
        handleResources(values);
        handleDevelopers(values);
        handleProjects(values);
        handleTasks(values);
        handlePlannings(values);
        handleReservations(values);
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
            addPlanning(i, (Map<String, Object>) plannings.get(i));
        }
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
            addReservation((Map<String, Object>) reservation);
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
        systemTime =  new SystemTime(parseTime(time));
        // TODO: systeemtijd zetten.
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
        String name = propertiesList.get("name");
        AResourceType resourceType = resourceManager.getDeveloperType();
        resourceManager.addResourceInstance(resourceType, name);
        developerList.add(resourceManager.getDeveloperType().getResourceInstances().get(resourceManager.getDeveloperType().getResourceInstances().size() - 1));
    }

    /**
     * TODO: Documentatie schrijven als methode af is.
     * @param number
     * @param propertiesList
     */
    private void addPlanning(int number, Map<String, Object> propertiesList){
        Task task = planningTaskMap.get(number);
        IPlan plan = resourceManager.getNextPlans(1, task, parseTime((String) propertiesList.get("plannedStartTime"))).get(0);
        task.plan(plan);
        List<Developer> developers = new ArrayList<>();
        //((ArrayList) propertiesList.get("developers")).forEach(x -> developers.add((Developer) developerList.get((Integer) x)));
        // TODO: afwerken en bij aan taak toevoegen
        plan.addReservations(developerList);

        // TODO: requirement list toevoegen
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
     * Maakt een nieuwe reservatie in de ResourceManager.
     * @param propertiesList
     */
    private void addReservation(Map<String, Object> propertiesList){
        ResourceInstance resourceInstance = resourceInstanceList.get((Integer) propertiesList.get("resource"));
        Task task = taskList.get((Integer) propertiesList.get("task"));
        LocalDateTime startTime = parseTime((String) propertiesList.get("startTime"));
        LocalDateTime endTime = parseTime((String) propertiesList.get("endTime"));
        resourceManager.makeReservation(task, resourceInstance, new TimeSpan(startTime, endTime), true);
        // TODO add reservations to plan
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
    }

    /**
     * Voegt een nieuwe taak toe.
     * @param propertiesList
     */
    private void addTask(Map<String, Object> propertiesList) {
        String description = (String) propertiesList.get("description");
        Duration duration = Duration.ofMinutes(Long.valueOf(String.valueOf(propertiesList.get("estimatedDuration"))));
        Double acceptableDeviation = Double.valueOf(String.valueOf(propertiesList.get("acceptableDeviation"))) / 100;
        Project project = projectList.get((Integer) propertiesList.get("project"));
        project.addNewTask(description, acceptableDeviation, duration);
        taskList.add(project.getTasks().get(project.getTasks().size() - 1));
        Task task = taskList.get(taskList.size() - 1);

        try{
            Integer number = (Integer) propertiesList.get("planning");
            planningTaskMap.put(number, task);
        } catch (NumberFormatException e){
            // Doe niks, het nummer van planning kon niet omgevormd worden.
        }

        if(propertiesList.containsKey("prerequisiteTasks") && propertiesList.get("prerequisiteTasks") != null) {
            for (int i : (ArrayList<Integer>) propertiesList.get("prerequisiteTasks")) {
                task.addNewDependencyConstraint(taskList.get(i));
            }
        }

        if(propertiesList.containsKey("startTime")){
            task.execute(parseTime((String) propertiesList.get("startTime")));
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
