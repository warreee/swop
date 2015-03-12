package be.swop.groep11.main;



import be.swop.groep11.main.ui.UserInterface;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    UserInterface ui;
    TMSystem TMSystem;
    ProjectRepository projectRepository;
    ArrayList<Project> projectList = new ArrayList<>();
    ArrayList<Task> taskList = new ArrayList<>();

    public InputReader(UserInterface ui, ProjectRepository projectRepository) {
        this.ui = ui;
        this.projectRepository = projectRepository;
        Map<Integer, Project> projectList = new HashMap<>();
        Map<Integer, Task> taskList = new HashMap<>();
    }

    public static void main(String[] args) throws FileNotFoundException {
        TMSystem TMSystem = new TMSystem();
        ProjectRepository pr = TMSystem.getProjectRepository();
        InputReader io = new InputReader(null, pr);
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
                    Project projectX = createProjectObject(mapProject);
                    projectList.add(projectX);
                }
            }

            if (key.equals("task")){
                subList = (ArrayList) values.get(key);
                for (int i = 0; i < subList.size(); i++) {
                    Map<String, String> mapTask = (Map<String, String>) subList.get(i);
                    Project projectX = projectList.get(Integer.valueOf(mapTask.get("project")));
                    addTaskToProject(mapTask, projectX); //De taak wordt in project aangemaakt
                    addOtherDetails(mapTask, projectX.getTasks().get(i));

                }
            }


/*            for (String subValueKey : test.keySet()) {
                System.out.println(String.format("\t%s = %s",
                        subValueKey, test.get(subValueKey)));
            }*/


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
            }
        }


    }

    private Project createProjectObject(Map<String, String> propertiesList) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            LocalDateTime creationTime = LocalDateTime.parse(propertiesList.get("creationTime"), formatter);
            LocalDateTime dueTime = LocalDateTime.parse(propertiesList.get("dueTime"), formatter);
            return new Project(propertiesList.get("name"), propertiesList.get("description"), creationTime, dueTime, user);

        } catch (DateTimeParseException e) {
            ui.printException(e);
            return null;
        }



    }

    private Task addTaskToProject(Map<String, String> propertiesList, Project project) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            String description = propertiesList.get("description");
            Duration duration = Duration.ofMinutes(Long.parseLong(propertiesList.get("estimatedDuration")));
            Double acceptableDeviation = Double.valueOf(propertiesList.get("acceptableDeviation"));
            return new Task(description, duration, acceptableDeviation, project);

        } catch (DateTimeParseException e) {
            ui.printException(e);
            return null;
        }
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
