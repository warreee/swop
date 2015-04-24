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
        // TODO: check: is valid...
        if (dependingOnMap.containsKey(dependent)){
            dependingOnMap.get(dependent).add(dependingOn);
            addToMap(dependingOn, dependingOnMap);
        } else {
            ArrayList<Task> dependingOnList = new ArrayList<>();
            dependingOnList.add(dependingOn);
            dependingOnMap.put(dependent, dependingOnList);
            addToMap(dependingOn, dependingOnMap);
        }

        if (dependentMap.containsKey(dependingOn)){
            dependentMap.get(dependingOn).add(dependent);
            addToMap(dependent, dependentMap);
        } else {
            ArrayList<Task> dependentList = new ArrayList<>();
            dependentList.add(dependent);
            dependentMap.put(dependingOn, dependentList);
            addToMap(dependent, dependentMap);
        }
    }

    private void addToMap(Task toBeAdded, HashMap map) {
        if (!map.containsKey(toBeAdded)){
            ArrayList<Task> emptyList = new ArrayList<>();
            map.put(toBeAdded, emptyList);
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
        removeFailedDependency(failedTask);

    }

    private void removeFailedDependency(Task failedTask) {
        dependentMap.remove(failedTask);
        dependingOnMap.entrySet().stream().filter(entry -> entry.getValue().contains(failedTask)).forEach(entry -> entry.getValue().remove(failedTask));
        dependingOnMap.remove(failedTask);
    }

    private boolean isValidDependency(Task dependent, Task dependingOn) {

        if (dependent == dependingOn) {  // dependencies mogen niet gelijk zijn
            return false;
        }
        if (dependent == null || dependingOn == null){
            return false;
        }

        return true;
    }

    private HashSet<Task> Unmarked = allTasks();
    ArrayList<Task> tempMarked;
    ArrayList<Task> finalMarked;
    ArrayList<Task> result;
    private boolean containsLoop(Task dependent, Task dependingOn) {
        
        addDependency(dependent, dependingOn);
        try {
            while (!finalMarked.isEmpty()) {
                visit(finalMarked.get(0));
            }
        } catch (IllegalArgumentException e) {
            // remove degelijk vorige dependency
        }
        return true;
    }

    private void visit(Task t){
        if (tempMarked.contains(t)){
            throw new IllegalArgumentException();
        }
        if (!finalMarked.contains(t)){
            tempMarked.add(t);
            this.dependingOnMap.get(t).forEach(this::visit);
            finalMarked.add(t);
            tempMarked.remove(t);
            result.add(t);
        }
    }

    private HashSet<Task> allTasks(){
       return (HashSet<Task>) this.dependentMap.keySet();
    }
}
