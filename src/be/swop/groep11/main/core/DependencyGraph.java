package be.swop.groep11.main.core;

import be.swop.groep11.main.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
            ArrayList<Task> dependedList = new ArrayList<>();
            dependedList.add(dependingOn);
            dependentMap.put(dependent, dependedList);
        }
    }

    public Set<Task> getDependentTasks(Task task){
        return new HashSet<>(dependentMap.get(task));
    }

    public Set<Task> getDependingOnTasks(Task task){
        return new HashSet<>(dependingOnMap.get(task));
    }


}
