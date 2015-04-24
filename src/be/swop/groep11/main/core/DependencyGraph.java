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
        if (isValidDependency(dependent, dependingOn)) {
            if (dependingOnMap.containsKey(dependent)) {
                dependingOnMap.get(dependent).add(dependingOn);
                addToMap(dependingOn, dependingOnMap);
            } else {
                ArrayList<Task> dependingOnList = new ArrayList<>();
                dependingOnList.add(dependingOn);
                dependingOnMap.put(dependent, dependingOnList);
                addToMap(dependingOn, dependingOnMap);
            }

            if (dependentMap.containsKey(dependingOn)) {
                dependentMap.get(dependingOn).add(dependent);
                addToMap(dependent, dependentMap);
            } else {
                ArrayList<Task> dependentList = new ArrayList<>();
                dependentList.add(dependent);
                dependentMap.put(dependingOn, dependentList);
                addToMap(dependent, dependentMap);
            }
        } else {
            throw new IllegalArgumentException("ongeldige dependency");
        }

    }

    /**
     * Verwijdert de laatst toegevoegde dependency
     * @param dependent
     * @param dependingOn
     */
    private void undoAddDependency(Task dependent, Task dependingOn) {

        dependingOnMap.get(dependent).remove(dependingOn);
        if (dependingOnMap.get(dependent).size() == 0){
            dependingOnMap.remove(dependent);
        }

        dependentMap.get(dependingOn).remove(dependent);
        if (dependentMap.get(dependingOn).size() == 0){
            dependentMap.remove(dependingOn);
        }


    }

    private void addToMap(Task toBeAdded, HashMap map) {
        if (!map.containsKey(toBeAdded)){
            ArrayList<Task> emptyList = new ArrayList<>();
            map.put(toBeAdded, emptyList);
        }
    }

    public Set<Task> getDependentTasks(Task task){
        if (dependentMap.containsKey(task)) {
            return new HashSet<>(dependentMap.get(task));

        }
        return new HashSet<>();
    }

    public Set<Task> getDependingOnTasks(Task task){

        if (dependingOnMap.containsKey(task)) {
            return new HashSet<>(dependingOnMap.get(task));
        }
        return new HashSet<>();
    }

    /**
     * Verandert in de afhankelijke taken van failedTask de afhankelijkheden naar de alternativeTask
     * @param failedTask
     * @param alternativeTask
     */
    public void changeDependingOnAlternativeTask(Task failedTask, Task alternativeTask) {
        ArrayList<Task> dependentTasksFailedTask = dependentMap.get(failedTask);
        for (Task dtft : dependentTasksFailedTask) {
            addDependency(dtft, alternativeTask);
        }
        removeDependency(failedTask);

    }

    /**
     * Verwijdert elk voorkomen van een bepaalde taak in de depencyGraph
     * @param failedTask
     */
    private void removeDependency(Task failedTask) {
        dependentMap.remove(failedTask);
        dependingOnMap.remove(failedTask);
        dependingOnMap.entrySet().stream().filter(entry -> entry.getValue().contains(failedTask)).forEach(entry -> entry.getValue().remove(failedTask));
        dependentMap.entrySet().stream().filter(entry -> entry.getValue().contains(failedTask)).forEach(entry -> entry.getValue().remove(failedTask));

    }

    private boolean isValidDependency(Task dependent, Task dependingOn) {

        if (dependent == dependingOn) {  // dependencies mogen niet gelijk zijn
            return false;
        }
        if (dependent == null || dependingOn == null){
            return false;
        }
        if (containsLoop(dependent, dependingOn)) {
            return false;
        }

        return true;
    }


    private boolean containsLoop(Task dependent, Task dependingOn) {
        ArrayList<Task> tempMarked = allTasks();
        ArrayList<Task> finalMarked = new ArrayList<>();
        ArrayList<Task> result = new ArrayList<>();
        addDependency(dependent, dependingOn);
        try {
            while (!finalMarked.isEmpty()) {
                visit(finalMarked.get(0), tempMarked, finalMarked, result);
            }
        } catch (IllegalArgumentException e) {
                undoAddDependency(dependent, dependingOn);
                return false;
        }
        return true;
    }

    private void visit(Task t, ArrayList<Task> tempMarked, ArrayList<Task> finalMarked, ArrayList<Task> result){
        if (tempMarked.contains(t)){
            throw new IllegalArgumentException();
        }
        if (!finalMarked.contains(t)){
            tempMarked.add(t);
            this.dependingOnMap.get(t).forEach((Task x) -> visit(x, tempMarked, finalMarked, result));
            finalMarked.add(t);
            tempMarked.remove(t);
            result.add(t);
        }
    }

    private ArrayList<Task> allTasks(){
       return new ArrayList<>(this.dependentMap.keySet());
    }
}
