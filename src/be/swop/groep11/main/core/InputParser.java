package be.swop.groep11.main.core;

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
    ArrayList<IResourceType> resourceTypeList = new ArrayList<>();
    ArrayList<ResourceInstance> resourceInstanceList = new ArrayList<>();
    ArrayList<DailyAvailability> dailyAvailabilityList = new ArrayList<>();
    ArrayList<Developer> developerList = new ArrayList<>();
    private ResourceManager resourceManager;
    private SystemTime systemTime;

    public InputParser(ProjectRepository projectRepository, ResourceManager resourceManager) {
        this.projectRepository = projectRepository;
        this.resourceManager = resourceManager;
    }


    public static void main(String[] args) throws FileNotFoundException {
        ProjectRepository projectRepository = new ProjectRepository(new SystemTime(LocalDateTime.now()));
        ResourceManager typeRepo = new ResourceManager();
        InputParser parser = new InputParser(projectRepository, typeRepo);
        parser.parseInputFile();
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
            addPlanning(i, (Map<String, String>) plannings.get(i));
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
            addReservation((Map<String, String>) reservation);
        }
    }

    /**
     * Leest alle Resources in.
     * @param resourceMap
     */
    private void handleResources(Map<String, Object> resourceMap){
        ArrayList resources = (ArrayList) resourceMap.get("resources");
        for (Object resource : resources) {
            addResource((Map<String, String>) resource);
        }
    }

    /**
     * Leest alle ResourceTypes in.
     * @param resourceTypeMap
     */
    private void handleResourceTypes(Map<String, Object> resourceTypeMap){
        ArrayList resourceTypes = (ArrayList) resourceTypeMap.get("resourceTypes");
        for(Object resourceType: resourceTypes){
            addResourceType((Map<String, String>) resourceType);
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
    }

    /**
     * Leest alle taken in.
     * OPGEPAST, lees eerst alle projecten in.
     * @param tasksMap
     */
    private void handleTasks(Map<String, Object> tasksMap){
        ArrayList tasks = (ArrayList) tasksMap.get("tasks");
        for(Object task: tasks){
            Map<String, String> taskMap = (Map<String, String>) task;
            addTask(taskMap);
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

    private void addDeveloper(Map<String, String> propertiesList){
        String name = propertiesList.get("name");
        IResourceType resourceType = resourceManager.getResourceTypeByName("Developer");
        resourceManager.addResourceInstance(resourceType, name);
        developerList.add((Developer) resourceType.getResourceInstances().get(resourceType.getResourceInstances().size() - 1));
    }

    private void addPlanning(int number, Map<String, String> propertiesList){
        Task task = planningTaskMap.get(number);
        task.plan(parseTime(propertiesList.get("plannedStartTime")));
        List<Developer> developers = new ArrayList<>();
        Arrays.stream(parseStringArray(propertiesList.get("developers"))).forEach(x -> developers.add(developerList.get(x)));
        // TODO: afwerken en bij aan taak toevoegen

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

        projectRepository.addNewProject(name, description, creationTime, dueTime, user);

        // Haalt het laatst toegevoegde project op.
        projectList.add(projectRepository.getProjects().get(projectRepository.getProjects().size() - 1 ));
    }

    private void addReservation(Map<String, String> propertiesList){
        ResourceInstance resourceInstance = resourceInstanceList.get(Integer.valueOf(propertiesList.get("resource")));
        Task task = taskList.get(Integer.valueOf(propertiesList.get("task")));
        LocalDateTime startTime = parseTime(propertiesList.get("startTime"));
        LocalDateTime endTime = parseTime(propertiesList.get("endTime"));
        resourceManager.makeReservation(task, resourceInstance, new TimeSpan(startTime, endTime), true);
    }

    /**
     * Voegt een nieuwe ResourceInstance toe aan een bestaand ResourceType.
     * @param propertiesList
     */
    private void addResource(Map<String, String> propertiesList){
        String name = propertiesList.get("name");
        IResourceType resourceType = resourceTypeList.get(Integer.valueOf(propertiesList.get("type")));
        resourceManager.addResourceInstance(resourceType, name);
        int size = resourceType.getResourceInstances().size();
        resourceInstanceList.add(resourceType.getResourceInstances().get(size - 1));
    }

    private void addResourceType(Map<String, String> propertiesList){
        // Lees alle info uit de map.
        String name = propertiesList.get("name");
        String requires = propertiesList.get("requires");
        String conflictsWith = propertiesList.get("conflictsWith");
        String dailyAvailability = propertiesList.get("dailyAvailability");

        // Haal de correcte IResourceTypes op die al bekend zijn. (Dit faalt indien er fouten staan in de inputfile.)
        ArrayList<IResourceType> req = new ArrayList<>();
        ArrayList<IResourceType> con = new ArrayList<>();
        Arrays.stream(parseStringArray(requires)).forEach(x -> req.add(resourceTypeList.get(x)));
        Arrays.stream(parseStringArray(conflictsWith)).forEach(x -> con.add(resourceTypeList.get(x)));

        // Voeg de IResourceType eindelijk toe.
        if(dailyAvailability.isEmpty()){
            // Er is geen dailyAvailability.
            resourceManager.addNewResourceType(name, req, con);
        } else {
            // Er is wel een dailyAvailability.
            DailyAvailability da = dailyAvailabilityList.get(Integer.valueOf(dailyAvailability));
            resourceManager.addNewResourceType(name, da, req, con);
        }
        resourceTypeList.add(resourceManager.getResourceTypeByName(name));
    }

    private void addTask(Map<String, String> propertiesList) {
        String description = propertiesList.get("description");
        Duration duration = Duration.ofMinutes(Long.valueOf(String.valueOf(propertiesList.get("estimatedDuration"))));
        Double acceptableDeviation = Double.valueOf(String.valueOf(propertiesList.get("acceptableDeviation"))) / 100;
        Project project = projectList.get(Integer.valueOf(propertiesList.get("project")));
        project.addNewTask(description, acceptableDeviation, duration);
        taskList.add(project.getTasks().get(project.getTasks().size() - 1));
        Task task = taskList.get(taskList.size() - 1);

        try{
            Integer number = Integer.valueOf(propertiesList.get("planning"));
            planningTaskMap.put(number, task);
        } catch (NumberFormatException e){
            // Doe niks, het nummer van planning kon niet omgevormd worden.
        }

        if(propertiesList.containsKey("startTime")){
            task.execute(parseTime(propertiesList.get("startTime")));
        }

        if(propertiesList.containsKey("endTime")){
            if (propertiesList.get("status").equals("finished")) {
                task.finish(parseTime(propertiesList.get("endTime")));
            } else if(propertiesList.get("status").equals("failed")) {
                task.fail(parseTime(propertiesList.get("endTime")));
            } else {
                throw new IllegalArgumentException("Onbekende status bij het zetten van een eindtijd.");
            }

        }

        if(propertiesList.containsKey("alternativeFor")){
            try {
                taskList.get(Integer.valueOf(propertiesList.get("alternativeFor"))).setAlternativeTask(task);
            } catch (NumberFormatException e){
                // Doe niks, de sleutel alternativeFor was waarschijnlijk leeg.
            }
        }

        if(propertiesList.containsKey("prerequisiteTasks")){
            for(int i: parseStringArray(propertiesList.get("prerequisiteTasks"))){
                taskList.get(i).addNewDependencyConstraint(task);
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

    /**
     * Maakt van een array in stringformaat een int[]
     *
     * @param array in stringformaat
     * @return een array van ints
     */
    private int[] parseStringArray(String array) {

        String[] stringArray = array.replace("[", "").replace("]", "").replace(" ", "").trim().split(",");

        int[] intArray = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.valueOf(stringArray[i]);
        }
        return intArray;
    }
}
