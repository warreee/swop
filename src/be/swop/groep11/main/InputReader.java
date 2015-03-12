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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by warreee on 2/03/15.
 */
public class InputReader  {

    User user = new User("InputReader");
    TMSystem TMSystem;
    ProjectRepository projectRepository;
    ImmutableList<Project> projectList = projectRepository.getProjects();
    ArrayList<Task> taskList = new ArrayList<>();

    public InputReader(ProjectRepository projectRepository) {

        this.projectRepository = projectRepository;
        Map<Integer, Project> projectList = new HashMap<>();
        Map<Integer, Task> taskList = new HashMap<>();
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

            if (key.equals("task")){
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapTask = (Map<String, String>) subList.get(i);
                    Project projectX = projectList.get(Integer.valueOf(mapTask.get("project")));
                    addTaskToProject(mapTask, projectX); //De taak wordt in project aangemaakt
                    addOtherDetails(mapTask, projectX.getTasks().get(projectX.getTasks().size() - 1)); //het laatst toegevoegde

                }
            }

        }

    }

    private void addOtherDetails(Map<String, String> mapTask, Task task) {
        for (String property : mapTask.keySet()){
            switch (property){
                case "alternativeFor" :
                    int alternativeTask = Integer.valueOf(mapTask.get("alternativeFor"));
                    taskList.get(alternativeTask).setAlternativeTask(task);
                    break;
                case "prerequisiteTasks" :
                    int[] ATArray = parseStringArray(mapTask.get("prerequisiteTasks"));
                    if (ATArray.length > 0) {
                        for (int prt : ATArray){
                            task.addNewDependencyConstraint(taskList.get(prt));
                        }
                    }
                    break;
                /*
                Status kan moeilijk geupdated worden als de start en eindtijd nog niet gezet zijn
                 */
                case "startTime" :
                    LocalDateTime creationTime = LocalDateTime.parse(
                    break;

                case "endTime" :

                case "status" :

            }
        }


    }

    private LocalDateTime parseTime(String date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(date);

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
            Duration duration = Duration.ofMinutes(Long.parseLong(propertiesList.get("estimatedDuration")));
            Double acceptableDeviation = Double.valueOf(propertiesList.get("acceptableDeviation"));
            project.addNewTask(description, acceptableDeviation, duration);

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
        String[] stringArray = array.replaceAll("\\[", "").replaceAll("\\]", "").split(",");

        int[] intArray = new int[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.valueOf(stringArray[i]);
        }
        return intArray;
    }



}
