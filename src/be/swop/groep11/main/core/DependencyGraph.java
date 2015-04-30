package be.swop.groep11.main.core;

import be.swop.groep11.main.exception.IllegalDependencyException;
import be.swop.groep11.main.task.Task;

import java.util.*;
import java.util.stream.Collectors;

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

    public void addNewDependency(Task dependent, Task dependingOn) throws IllegalDependencyException {
        isValidDependency(dependent, dependingOn);
        addDependency(dependent, dependingOn);

    }

    private void addDependency(Task dependent, Task dependingOn) {
        if (dependingOnMap.containsKey(dependent)) {
            if (!dependingOnMap.get(dependent).contains(dependingOn)) {
                dependingOnMap.get(dependent).add(dependingOn);
                addToMap(dependingOn, dependingOnMap);
            }
        } else {
            ArrayList<Task> dependingOnList = new ArrayList<>();
            dependingOnList.add(dependingOn);
            dependingOnMap.put(dependent, dependingOnList);
            addToMap(dependingOn, dependingOnMap);
        }

        if (dependentMap.containsKey(dependingOn) ) {
            if(!dependentMap.get(dependingOn).contains(dependent)) {
                dependentMap.get(dependingOn).add(dependent);
                addToMap(dependent, dependentMap);
            }
        } else {
            ArrayList<Task> dependentList = new ArrayList<>();
            dependentList.add(dependent);
            dependentMap.put(dependingOn, dependentList);
            addToMap(dependent, dependentMap);
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
        if (dependentTasksFailedTask != null) {
            for (Task dtft : dependentTasksFailedTask) {
                addNewDependency(dtft, alternativeTask);
            }
            removeDependency(failedTask);
        }

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

    private void isValidDependency(Task dependent, Task dependingOn) {

        if (dependent == dependingOn) {  // dependencies mogen niet gelijk zijn
            throw new IllegalDependencyException("Een taak kan niet afhankelijk zijn van zichzelf");
        }
        if (dependingOnMap.containsKey(dependent) && dependingOnMap.get(dependent).contains(dependingOn)){ // dezelfde constraint werd toegevoegd
            throw new IllegalDependencyException("Deze dependency bestond al");
        }

        if (dependentMap.containsKey(dependingOn) && dependentMap.get(dependingOn).contains(dependent)){ // dezelfde constraint werd toegevoegd
            throw new IllegalDependencyException("Deze dependency bestond al");
        }

        if (dependent == null || dependingOn == null){
            throw new IllegalDependencyException("De dependency mag geen null bevatten");
        }
        if (containsLoop(dependent, dependingOn)) {
            throw new IllegalDependencyException("Deze dependency zorgt voor een loop!");
        }

    }


    private boolean containsLoop(Task dependent, Task dependingOn) {
        if (allTasks().size() == 0) { // er zitten nog geen depencies in
            return false;
        }
        ArrayList<Task> tempMarked = new ArrayList<>();
        ArrayList<Task> allTasks = allTasks();
        ArrayList<Task> finalMarked = new ArrayList<>();
        ArrayList<Task> result = new ArrayList<>();
        addDependency(dependent, dependingOn);
        try {
            while (allTasks.stream().filter(entry -> !tempMarked.contains(entry)).filter(entry -> !finalMarked.contains(entry)).count() != 0) {
                // de vorige lamda expressie gaat na of unmarked niet leeg is
                ArrayList<Task> unMarked = (ArrayList<Task>) allTasks.stream().filter(entry -> !tempMarked.contains(entry)).filter(entry -> !finalMarked.contains(entry)).collect(Collectors.toList());
                visit(unMarked.get(0), tempMarked, finalMarked, result);
            }
        } catch (IllegalArgumentException e) {
            undoAddDependency(dependent, dependingOn);
            return true;
        }
        return false;
    }

    private void visit(Task t, ArrayList<Task> tempMarked, ArrayList<Task> finalMarked, ArrayList<Task> result){
        if (tempMarked.contains(t)){
            throw new IllegalArgumentException();
        }
        if (!finalMarked.contains(t)){
            tempMarked.add(t);
            for (Task x : this.dependentMap.get(t)){
                visit(x, tempMarked, finalMarked, result);
            }
            finalMarked.add(t);
            tempMarked.remove(t);
            result.add(t);
        }
    }

    private ArrayList<Task> allTasks(){
        return new ArrayList<>(this.dependentMap.keySet());
    }


    public ArrayList<Task> getLeafs() {
        ArrayList<Task> leafs = new ArrayList<>();
        allTasks().stream().filter(node -> dependentMap.get(node).size() == 0).forEach(leafs::add);
        return leafs;
    }

    /**
     * Zoekt recursief een pad dat begint met een rootnode
     * @param task
     * @return
     */
    public ArrayList<ArrayList<Task>> getPathsTo(Task task) {
        ArrayList<Task> currentPath = new ArrayList<>();
        ArrayList<ArrayList<Task>> paths = new ArrayList<>();
        currentPath.add(task);

        return getPathsTo(task, currentPath, paths, task);

    }

    /**
     * Zoekt recursief een pad dat begint met een rootnode
     * @param task
     * @return
     */
    private ArrayList<ArrayList<Task>> getPathsTo(Task task, ArrayList<Task> currentPath, ArrayList<ArrayList<Task>> paths, Task leaf) {
        Set<Task> dependingon = task.getDependingOnTasks();
        if (task.getDependingOnTasks().isEmpty()){
            ArrayList<Task> path = new ArrayList<>(3);
            currentPath.forEach(path::add);
            paths.add(path);
            //currentPath.remove(currentPath.size() - 1);
        } else {

            for (Task T : task.getDependingOnTasks()) {

                currentPath.add(T);
                getPathsTo(T, currentPath, paths, leaf);
                currentPath.remove(T);

            }
        }
        return paths;
    }
}
