package be.swop.groep11.main.core;

import be.swop.groep11.main.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by warreee on 4/20/15.
 */
public class DependencyGraph {

    public DependencyGraph() {

    }


    private HashMap<Task, ArrayList<Task>> dependingOnMap = new HashMap<>();
    private HashMap<Task, ArrayList<Task>> dependedMap = new HashMap<>();

    public void addDependency(Task task, Task dependingOn) {

        if (dependingOnMap.containsKey(task)){
            dependingOnMap.get(task).add(dependingOn);
        } else {
            ArrayList<Task> dependingOnList = new ArrayList<>();
            dependingOnList.add(dependingOn);
            dependingOnMap.put(task, dependingOnList);
        }

        if (dependedMap.containsKey(task)){
            dependedMap.get(task).add(dependingOn);
        } else {
            ArrayList<Task> dependedList = new ArrayList<>();
            dependedList.add(dependingOn);
            dependedMap.put(task, dependedList);
        }
    }

    public ArrayList getDependendTasks(Task task){
        return dependedMap.get(task);
    }

    public ArrayList getDependingOnTasks(Task task){
        return dependingOnMap.get(task);
    }


    //private void addDependingOn(Ta)

}
