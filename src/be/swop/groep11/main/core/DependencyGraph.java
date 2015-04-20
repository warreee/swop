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


    public void addDependency(Task task, Task dependingOn) {

        if (dependingOnMap.containsKey(task)){
            dependingOnMap.get(task).add(dependingOn);
        } else {
            ArrayList<Task> dependingOnList = new ArrayList<>();
            dependingOnList.add(dependingOn);
            dependingOnMap.put(task, dependingOnList);
        }

        if (dependentMap.containsKey(task)){
            dependentMap.get(task).add(dependingOn);
        } else {
            ArrayList<Task> dependedList = new ArrayList<>();
            dependedList.add(dependingOn);
            dependentMap.put(task, dependedList);
        }
    }

    public Set<Task> getDependentTasks(Task task){
        return new HashSet<>(dependentMap.get(task));
    }

    public Set<Task> getDependingOnTasks(Task task){
        return new HashSet<>(dependingOnMap.get(task));
    }


    //private void addDependingOn(Ta)

}
