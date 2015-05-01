package be.swop.groep11.main.core;

import be.swop.groep11.main.exception.IllegalDependencyException;
import be.swop.groep11.main.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Klasse om depencyConstraints bij te houden.
 */
public class DependencyGraph {

    private HashMap<Task, ArrayList<Task>> dependingOnMap;
    private HashMap<Task, ArrayList<Task>> dependentMap;

    /**
     * DepencyGraph heeft 2 interne Maps
     * depedingOnMap: K = taak die van de taken in V afhangt
     * dependentMap: K = taak die taken in V heeft die van hem afhangen.
     */
    public DependencyGraph() {
        dependingOnMap = new HashMap<>();
        dependentMap = new HashMap<>();
    }



    /**
     * Dependent hangt af van de dependingOn. Dus dependingOn moet eerst worden uitgevoerd.
     * @param dependent De supertaak.
     * @param dependingOn de taak die moet moet gedaan worden voor de supertaak kan gedaan worden.
     */
    public void addNewDependency(Task dependent, Task dependingOn) throws IllegalDependencyException {
        isValidDependency(dependent, dependingOn); // gooit een exception indien de dependency niet valid is, daardoor wordt addDepency niet uitgevoerd.
        addDependency(dependent, dependingOn);

    }

    /**
     * Voegt een dependency toe aan deze dependency graph indien deze dependency nog niet bestaat. Indien deze al wel
     * bestaat faalt het toevoegen zonder error.
     * @param dependent De supertaak.
     * @param dependingOn de taak die moet moet gedaan worden voor de supertaak kan gedaan worden.
     */
    private void addDependency(Task dependent, Task dependingOn) {
        if (dependingOnMap.containsKey(dependent)) {
            if (!dependingOnMap.get(dependent).contains(dependingOn)) {
                dependingOnMap.get(dependent).add(dependingOn);

            }
            addToMap(dependingOn, dependingOnMap);
        } else {
            ArrayList<Task> dependingOnList = new ArrayList<>();
            dependingOnList.add(dependingOn);
            dependingOnMap.put(dependent, dependingOnList);
            addToMap(dependingOn, dependingOnMap);
        }

        if (dependentMap.containsKey(dependingOn) ) {
            if(!dependentMap.get(dependingOn).contains(dependent)) {
                dependentMap.get(dependingOn).add(dependent);

            }
            addToMap(dependent, dependentMap);
        } else {
            ArrayList<Task> dependentList = new ArrayList<>();
            dependentList.add(dependent);
            dependentMap.put(dependingOn, dependentList);
            addToMap(dependent, dependentMap);
        }
    }

    /**
     * Verwijdert de laatst toegevoegde dependency
     * @param dependent De supertaak.
     * @param dependingOn de taak die moet moet gedaan worden voor de supertaak kan gedaan worden.
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


    /**
     * Geeft alle taken die moeten gedaan worden voor de doorgegeven taak gedaan kan worden.
     * @param task De taak waarvoor dit gecontrolleerd moet worden.
     * @return De taken die gedaan moeten worden/zijn voor de doorgegeven taak.
     */
    public Set<Task> getDependingOnTasks(Task task){

        if (dependingOnMap.containsKey(task)) {
            return new HashSet<>(dependingOnMap.get(task));
        }
        return new HashSet<>();
    }

    /**
     * Verandert in de afhankelijke taken van failedTask de afhankelijkheden naar de alternativeTask
     * @param failedTask De Task die gefailed is.
     * @param alternativeTask De Task die de gefailde Task vervangt.
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
     * @param failedTask De taak waarvan de dependencies moeten verwijderd worden.
     */
    private void removeDependency(Task failedTask) {
        dependentMap.remove(failedTask);
        dependingOnMap.remove(failedTask);
        dependingOnMap.entrySet().stream().filter(entry -> entry.getValue().contains(failedTask)).forEach(entry -> entry.getValue().remove(failedTask));
        dependentMap.entrySet().stream().filter(entry -> entry.getValue().contains(failedTask)).forEach(entry -> entry.getValue().remove(failedTask));

    }

    /**
     * Controlleert of voor de 2 gegeven taken er een dependency mogelijk is.
     * @param dependent De supertaak.
     * @param dependingOn De taak die gedaan moet worden voor de supertaak.
     */
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

    /**
     * Controlleert of de 2 taken geen loop veroorzaken.
     * @param dependent De supertaak
     * @param dependingOn de Task die moet uitgevoegd worden voor de supertaak.
     * @return True als dit een loop veroorzaakt anders False.
     */
    private boolean containsLoop(Task dependent, Task dependingOn) {
        if (getAllTasksWithDependencies().size() == 0) { // er zitten nog geen depencies in
            return false;
        }
        ArrayList<Task> tempMarked = new ArrayList<>();
        ArrayList<Task> allTasks = getAllTasksWithDependencies();
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
        undoAddDependency(dependent, dependingOn);
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


    protected ArrayList<Task> getAllTasksWithDependencies(){
        return new ArrayList<>(this.dependentMap.keySet());
    }


    public ArrayList<Task> getLeafs() {
        ArrayList<Task> leafs = new ArrayList<>();
        getAllTasksWithDependencies().stream().filter(node -> dependentMap.get(node).size() == 0).forEach(leafs::add);
        return leafs;
    }

    /**
     * Zoekt recursief een pad dat begint met een rootnode
     * @param task De Task waar het pad moet eindigen.
     * @return Een lijst met een lijst van alle paden.
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
