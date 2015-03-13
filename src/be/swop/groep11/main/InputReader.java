package be.swop.groep11.main;



import be.swop.groep11.main.ui.UserInterface;
import com.google.common.collect.ImmutableList;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Created by warreee on 2/03/15.
 */
public class InputReader  {

    User user = new User("InputReader");
    TMSystem TMSystem;
    ProjectRepository projectRepository;
    ImmutableList<Project> projectList;
    ArrayList<Task> taskList = new ArrayList<>();


    public InputReader(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.projectList = this.projectRepository.getProjects();
    }

    public static void main(String[] args) throws FileNotFoundException {
        TMSystem TMSystem = new TMSystem();
        ProjectRepository pr = TMSystem.getProjectRepository();
        InputReader io = new InputReader(pr);
        io.runInputReader();
        io.runInputReader();


    }

    @SuppressWarnings("unchecked")
    public void runInputReader() throws FileNotFoundException {


        Yaml yaml = new Yaml();
        String path = Paths.get("input/input.tman").toAbsolutePath().toString();
        //System.out.println(yaml.dump(yaml.load(new FileInputStream(new File(path)))));

        // In de tman file staan twee soorten values: projects en tasks
        Map<String, Map<String, String>> values = (Map<String, Map<String, String>>) yaml
                .load(new FileInputStream(new File(path)));

        // Voegt eerst de projecten toe, daarna de taken.
        for (String key : values.keySet()) {
            ArrayList subList;

            if (key.equals("projects")){
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapProject = (Map<String, String>) subList.get(i);
                    addProject(mapProject);
                }
            }

            if (key.equals("tasks")){
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapTask = (Map<String, String>) subList.get(i);
                    int projectIndex = Integer.valueOf(String.valueOf(mapTask.get("project")));
                    int s2 = Integer.valueOf(String.valueOf(mapTask.get("project")));
                    //String st = mapTask.get("project");
                    //int t = Integer.valueOf(mapTask.get("project"));
                    Project projectX = projectRepository.getProjects().get(projectIndex);
                    addTaskToProject(mapTask, projectX); //De taak wordt in project aangemaakt
                    addOtherDetails(mapTask, projectX.getTasks().get(projectX.getTasks().size() - 1)); //het laatst toegevoegde

                }
            }

        }
        System.out.print("test");

    }

    private void addOtherDetails(Map<String, String> mapTask, Task task) {


                    if (mapTask.get("alternativeFor") != null) {
                        int alternativeTask = Integer.valueOf(String.valueOf(mapTask.get("alternativeFor")));
                        taskList.get(alternativeTask).setAlternativeTask(task);
                    }


                    if (mapTask.get("prerequisiteTasks") != null) {
                        int[] ATArray = parseStringArray(String.valueOf(mapTask.get("prerequisiteTasks")));
                        if (ATArray.length > 0) {
                            for (int prt : ATArray) {
                                task.addNewDependencyConstraint(taskList.get(prt));
                            }
                        }
                    }

                /*
                Status kan moeilijk geupdated worden als de start en eindtijd nog niet gezet zijn
                 */

                    if (mapTask.get("startTime") != null) {
                        LocalDateTime startTime = parseTime(mapTask.get("startTime"));
                        task.setStartTime(startTime);
                    }


                    if (mapTask.get("endTime") != null) {
                        LocalDateTime endTime = parseTime(mapTask.get("endTime"));
                        task.setEndTime(endTime);
                    }

                    if (mapTask.get("status") != null) {
                        TaskStatus status = stringToStatus(mapTask.get("status"));
                        if (!status.equals(TaskStatus.UNAVAILABLE)) {
                            task.setNewStatus(status);
                        }
                    }


    }

    private TaskStatus stringToStatus(String strStatus) throws IllegalArgumentException{

        TaskStatus result = null;

        try {
            for (TaskStatus status : TaskStatus.values()){
                if (strStatus.equalsIgnoreCase(status.toString())){
                    result = status;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return result;
    }


    private LocalDateTime parseTime(String date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(date, dateTimeFormatter);

        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void addProject(Map<String, String> propertiesList) {

            String name = propertiesList.get("name");
            String description = propertiesList.get("description");
            LocalDateTime creationTime = parseTime(propertiesList.get("creationTime"));
            LocalDateTime dueTime = parseTime(propertiesList.get("dueTime"));

            projectRepository.addNewProject(name, description, creationTime, dueTime, user);

    }

    private void addTaskToProject(Map<String, String> propertiesList, Project project) {

            String description = propertiesList.get("description");
            Duration duration = Duration.ofMinutes(Long.valueOf(String.valueOf(propertiesList.get("estimatedDuration"))));
            Double acceptableDeviation = Double.valueOf(String.valueOf(propertiesList.get("acceptableDeviation")));
            project.addNewTask(description, acceptableDeviation, duration);
            taskList.add(project.getTasks().get(project.getTasks().size() - 1));
    }


    /**
     * Geeft de nummer van het project terug waarbij de taak hoort.
     * @param propertiesList
     * @return
     */
    private int projectOfTask(Map<String, String> propertiesList){

        return Integer.valueOf(propertiesList.get("project"));
    }

    private int[] parseStringArray(String array) {

        String[] stringArray = array.replace("[", "").replace("]", "").replace(" ", "").trim().split(",");

        int[] intArray = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.valueOf(stringArray[i]);
        }
        return intArray;
    }



}
