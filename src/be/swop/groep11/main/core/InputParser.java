package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.DailyAvailability;
import be.swop.groep11.main.resource.IResourceType;
import be.swop.groep11.main.resource.ResourceManager;;
import be.swop.groep11.main.task.Task;
import be.swop.groep11.main.task.TaskStatus;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Klasse voor het inlezen van de de input file, deze moet staan in /input met de naam input.tman
 */
public class InputParser {
    // TODO: nieuwe implementatie ResourceTypeRepo afwachten. (Overschakelen naar ResourceManager?)
    User user = new User("InputParser");
    ProjectRepository projectRepository;
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<Task> taskList = new ArrayList<>();
    ArrayList<IResourceType> resourceTypeList = new ArrayList<>();
    ArrayList<DailyAvailability> dailyAvailabilityList = new ArrayList<>();
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

        handleDailyAvailability(values);
        handleSystemTime(values);
        handleProjects(values);
        handleTasks(values);
        handleResourceTypes(values);


        // Voegt eerst de projecten toe, daarna de taken.
        for (String key : values.keySet()) {
            ArrayList subList;

//            if (key.equals("tasks")) {
//                subList = (ArrayList) values.get(key);
//                for (int i = 0; i < subList.size(); i++) {
//                    Map<String, String> mapTask = (Map<String, String>) subList.get(i);
//                    int projectIndex = Integer.valueOf(String.valueOf(mapTask.get("project")));
//                    Project projectX = projectRepository.getProjects().get(projectIndex);
//                    addTaskToProject(mapTask, projectX); //De taak wordt in project aangemaakt
//                    addOtherDetails(mapTask, projectX.getTasks().get(projectX.getTasks().size() - 1)); //het laatst toegevoegde
//
//                }
//            }

            if (key.equals("systemTime")) {
                String sysTime = String.valueOf(values.get(key));
                LocalDateTime sytemTime = parseTime(sysTime);
            }

            if (key.equals("dailyAvailability")) {
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapAvailability = (Map<String, String>) subList.get(i);
                }
            }

            if (key.equals("resourceTypes")) {
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapResourceTypes = (Map<String, String>) subList.get(i);
                }
            }


            if (key.equals("resources")) {
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapResources = (Map<String, String>) subList.get(i);
                }
            }

            if (key.equals("developers")) {
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapDevelopers = (Map<String, String>) subList.get(i);
                }
            }

            if (key.equals("plannings")) {
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapPlannings = (Map<String, String>) subList.get(i);
                }
            }

            if (key.equals("reservations")) {
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapReservations = (Map<String, String>) subList.get(i);
                }
            }

        }

    }

    /**
     * Leest alle projecten in.
     * @param projectsMap
     */
    private void handleProjects(Map<String, Object> projectsMap){
        ArrayList projects = (ArrayList) projectsMap.get("projects");
        for(Object project: projects){
            addProject((Map<String, String>) project);
        }
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
            int projectIndex = Integer.valueOf(String.valueOf(taskMap.get("project")));
            // TODO: Complete
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
     * Leest alle DailyAvailabilities in.
     * @param dailyAvailabilityMap
     */
    private void handleDailyAvailability(Map<String, Object> dailyAvailabilityMap){
        ArrayList dailyAvailabilitys = (ArrayList) dailyAvailabilityMap.get("dailyAvailability");
        for(Object dailyAvailability: dailyAvailabilitys){
            addDailyAvailability((Map<String, String>) dailyAvailability);
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
     * TODO
     * @return
     */
    public SystemTime getSystemTime(){
        return this.systemTime;
    }

//    private void addType(Map<String, String> typeMap){
//        typeMap.get("name");
//        int[] ATArray = parseStringArray(String.valueOf(typeMap.get("requires")));
//        typeMap.get("requires");
//        typeMap.get("conflictsWith");
//        typeMap.get("dailyAvailability");
//    }

    /**
     * Init IResourceTypes a.d.h.v. hun namen.
     */
    private void withTypes(List<String> types) throws IllegalArgumentException {
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("Eerst types bepalen met");
        }

        for (String typeName : types) {
            resourceManager.addNewResourceType(typeName);

        }
    }

    private void defineResourceType(String typeName, DailyAvailability availability, List<String> requires, List<String> conflicts, List<String> instances) throws IllegalArgumentException {
        if (typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("Ongeldige naam voor type.");
        }
        IResourceType ownerType = resourceManager.getResourceTypeByName(typeName);

        resourceManager.withDailyAvailability(ownerType, availability);

        //add require constraints
        for (String req : requires) {
            IResourceType reqType = resourceManager.getResourceTypeByName(req);
            resourceManager.withRequirementConstraint(ownerType, reqType);
        }
        //Add conflicting constraints
        for (String con : conflicts) {
            IResourceType conflictType = resourceManager.getResourceTypeByName(con);
            resourceManager.withConflictConstraint(ownerType, conflictType);
        }

        //Add instances
        for (String inst : instances) {
            resourceManager.addResourceInstance(ownerType, inst);
        }
    }

    /**
     * addOtherDetails voegt details toe aan de taken die niet worden gezet bij de constructie van een taak in project.
     *
     * @param mapTask de ingelezen details die moeten worden toegevoegd.
     * @param task    de taak waar de details aan worden toegevoegd.
     */
    private void addOtherDetails(Map<String, String> mapTask, Task task) {

        /*
        Zet de meegegeven taak als alternatieve taak.
         */
        if (mapTask.get("alternativeFor") != null) {
            int alternativeTask = Integer.valueOf(String.valueOf(mapTask.get("alternativeFor")));
            taskList.get(alternativeTask).setAlternativeTask(task);
        }

        /*
        Voegt aan de meegegeven taak taken die toe die prerequisite zijn.
         */
        if (mapTask.get("prerequisiteTasks") != null) {
            int[] ATArray = parseStringArray(String.valueOf(mapTask.get("prerequisiteTasks")));
            if (ATArray.length > 0) {
                for (int prt : ATArray) {
                    task.addNewDependencyConstraint(taskList.get(prt));
                }
            }
        }

                /*
                Status kan niet geupdated worden als de start en eindtijd nog niet gezet zijn.
                Zet daarom eerst start en endtime.
                 */

        if (mapTask.get("startTime") != null) {
            LocalDateTime startTime = parseTime(mapTask.get("startTime"));
//            task.setStartTime(startTime);
        }


        if (mapTask.get("endTime") != null) {
            LocalDateTime endTime = parseTime(mapTask.get("endTime"));
//            task.setEndTime(endTime);
        }


    }

    /**
     * Zorgt voor de parsing van een string naar een TaskStatus
     *
     * @param strStatus de status in stringvorm
     * @return de taskstatus
     * @throws IllegalArgumentException indien er string wordt meegegeven waar geen status mee overeenkomt
     *                                  (is niet hoofdlettergevoelgi)
     */
    private TaskStatus stringToStatus(String strStatus) throws IllegalArgumentException {

        TaskStatus result = null;

       /* try {
            for (TaskStatus status : TaskStatus.values()){
                if (strStatus.equalsIgnoreCase(status.toString())){
                    result = status;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        */
        return result;
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

    private void addTask(Map<String, String> propertiesList) {

        // TODO: planning er in steken.
        // TODO: reservations er nog in steken.
        // TODO: Status nog eens goed controlleren

        String description = propertiesList.get("description");
        Duration duration = Duration.ofMinutes(Long.valueOf(String.valueOf(propertiesList.get("estimatedDuration"))));
        Double acceptableDeviation = Double.valueOf(String.valueOf(propertiesList.get("acceptableDeviation"))) / 100;
        Project project = projectList.get(Integer.valueOf(propertiesList.get("project")));
        project.addNewTask(description, acceptableDeviation, duration);
        taskList.add(project.getTasks().get(project.getTasks().size() - 1));
        Task task = taskList.get(taskList.size() - 1);

        if(propertiesList.containsKey("startTime")){
            task.execute(parseTime(propertiesList.get("startTime")));
        }

        if(propertiesList.containsKey("endTime")){
            if(propertiesList.get("status").equals("finished")) {
                task.finish(parseTime(propertiesList.get("endTime")));
            } else if(propertiesList.get("status").equals("failed")){
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
//        for(int i: parseStringArray(requires)){
//            req.add(resourceTypeList.get(i));
//        }
//        for(int i: parseStringArray(conflictsWith)){
//            con.add(resourceTypeList.get(i));
//        }

        // Voeg de IResourceType eindelijk toe.
        if(dailyAvailability.isEmpty()){
            // Er is geen dailyAvailability.
            resourceManager.addNewResourceType(name, req, con);
        } else {
            // Er is wel een dailyAvailability.
            DailyAvailability da = dailyAvailabilityList.get(Integer.valueOf(dailyAvailability));
            resourceManager.addNewResourceType(name, da, req, con);
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
     * Gebruikt een aangemaakt project om een taak aan toe te voegen
     *
     * @param propertiesList de eigenschappen van de taak in tman formaat
     * @param project        het project waar de taak aan wordt toegevoegd.
     */
    private void addTaskToProject(Map<String, String> propertiesList, Project project) {

        String description = propertiesList.get("description");
        Duration duration = Duration.ofMinutes(Long.valueOf(String.valueOf(propertiesList.get("estimatedDuration"))));
        Double acceptableDeviation = Double.valueOf(String.valueOf(propertiesList.get("acceptableDeviation"))) / 100;
        project.addNewTask(description, acceptableDeviation, duration);
        taskList.add(project.getTasks().get(project.getTasks().size() - 1));
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
