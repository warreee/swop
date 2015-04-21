package be.swop.groep11.main.core;

import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;

/**
 * Created by warreee on 4/20/15.
 */
public class DependencyGraph {

    private HashMap<Task, ArrayList<Task>> dependingOnMap;
    private HashMap<Task, ArrayList<Task>> dependentMap;

    public DependencyGraph() {
        dependingOnMap = new HashMap<>();
        dependentMap = new HashMap<>();
    }

    /**
     * Dependent hangt af van de dependingOn. Dus dependingOn moet eerst worden uitgevoerd.
     * @param dependent
     * @param dependingOn
     */

    public void addDependency(Task dependent, Task dependingOn) {

        if (dependingOnMap.containsKey(dependent)){
            dependingOnMap.get(dependent).add(dependingOn);
        } else {
            ArrayList<Task> dependingOnList = new ArrayList<>();
            dependingOnList.add(dependingOn);
            dependingOnMap.put(dependent, dependingOnList);
        }

        if (dependentMap.containsKey(dependent)){
            dependentMap.get(dependent).add(dependingOn);
        } else {
            ArrayList<Task> dependentList = new ArrayList<>();
            dependentList.add(dependingOn);
            dependentMap.put(dependent, dependentList);
        }
    }

    public Set<Task> getDependentTasks(Task task){
        ImmutableList<Task> copy = ImmutableList.copyOf(dependentMap.get(task));
        return new HashSet<>(copy);
    }

    public Set<Task> getDependingOnTasks(Task task){
        ImmutableList<Task> copy = ImmutableList.copyOf(dependingOnMap.get(task));
        return new HashSet<>(copy);
    }

    public void changeDepeningOnAlternativeTask(Task failedTask, Task alternativeTask) {
        ArrayList<Task> dependentTasksFailedTask = dependentMap.get(failedTask);
        for (Task dtft : dependentTasksFailedTask) {
            addDependency(dtft, alternativeTask);
        }
        removeDependency(failedTask);

    }

    private void removeDependency(Task failedTask) {
        dependentMap.remove(failedTask);
        dependingOnMap.entrySet().stream().filter(entry -> entry.getValue().contains(failedTask)).forEach(entry -> entry.getValue().remove(failedTask));
    }


}
