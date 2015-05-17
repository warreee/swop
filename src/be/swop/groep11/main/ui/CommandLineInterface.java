package be.swop.groep11.main.ui;

import be.swop.groep11.main.actions.Action;
import be.swop.groep11.main.actions.ControllerStack;
import be.swop.groep11.main.controllers.AbstractController;
import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.core.Project;
import be.swop.groep11.main.exception.CancelException;
import be.swop.groep11.main.exception.EmptyListException;
import be.swop.groep11.main.exception.IllegalActionException;
import be.swop.groep11.main.task.Task;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Commandline gebruikersinterface die UserInterface implementeert.
 */
public class CommandLineInterface implements UserInterface {

    /**
     * Constructor om een nieuwe commandline gebruikersinterface te maken.
     * Maakt een nieuw TaskMan object aan en initialiseert de controllers.
     */
    public CommandLineInterface(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
        this.exit = false;
    }

    public void run() {
        try {
            while (!exit) {
                try {
                    String commandString = bufferedReader.readLine();
                    Action com = Action.getAction(commandString);
//                    executeCommand(com);
//                    controllerStack.executeAction(action)
                    getControllerStack().executeAction(com);
                } catch (IllegalActionException ec) {
                    printException(ec);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean exit;
    private BufferedReader bufferedReader;
    private ControllerStack controllerStack;

    @Override
    public void wantsToExit() {
        this.exit = true;
    }

    private boolean canHaveAsActionMapping(ControllerStack controllerStack) {
        return controllerStack != null && this.controllerStack == null;
    }

    public void setControllerStack(ControllerStack controllerStack) {
        if (!canHaveAsActionMapping(controllerStack)) {
            throw new IllegalArgumentException("Gegeven actionMapping is geen geldige voor deze UI.");
        }
        this.controllerStack = controllerStack;
    }

    @Override
    public ControllerStack getControllerStack() {
        return controllerStack;
    }

//    private void executeCommand(Action action) {
//        controllerStack.executeAction(action);
//    }

    @Override
    public void printMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void printException(Exception e) {
        System.out.printf(e.getMessage() + "\n");
    }

    /**
     * Toont een tekstweergave van een lijst projecten.
     * Implementeert showProjectList in UserInterface
     */
    @Override
    public void showProjectList(ImmutableList<Project> projects) {
        printList(projects, showProjectEntry);
    }

    /**
     * Toont een tekstversie van een lijst van taken aan de gebruiker
     *
     * @param tasks Lijst van taken
     */
    @Override
    public void showTaskList(ImmutableList<Task> tasks) {
        printList(tasks, showTaskEntry);
    }

    private String taskFormatStr = "%-35s %-20s %-15s %n";
    Function<Task, String> showTaskEntry = (task -> {
        String asterix = (task.isUnacceptablyOverTime()) ? "*" : "";
        String onTime = task.isOverTime() ? "nee (" + Math.round(task.getOverTimePercentage() * 100) + "%)" : "ja";
        return String.format(taskFormatStr, task.getDescription() + asterix, task.getStatusString(), onTime);
    });

    private String projectFormatStr = "%-35s %-20s %-20s %n";
    Function<Project, String> showProjectEntry = (project -> {
        String overTime = (project.isOverTime()) ? "over time" : "on time";
        // TODO: project overtime Method?
        return String.format(projectFormatStr, project.getName(), project.getProjectStatus().name(), "(" + overTime + ")");
    });
    Function<LocalDateTime, String> showLocalDateTimeEntry = (dateTime -> dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    Function<BranchOffice, String> showBranchOfficeEntry = (BranchOffice::getName);

    /**
     * Laat de gebruiker een taak selecteren uit een lijst van taken
     * en geeft het nummer van de geselecteerde taak in de lijst terug.
     * Implementeert selectTaskFromList in UserInterface
     */
    @Override
    public Task selectTaskFromList(ImmutableList<Task> tasks) throws EmptyListException, CancelException {
        printMessage(String.format(taskFormatStr, "Omschrijving", "Status", "On time?"));
        return selectFromList(tasks, showTaskEntry);
    }

    /**
     * Laat de gebruiker een project selecteren uit een lijst van projecten
     * en geeft het nummer van het geselecteerde project in de lijst terug.
     * Implementeert selectProjectFromList in UserInterface
     */
    @Override
    public Project selectProjectFromList(ImmutableList<Project> projects) throws EmptyListException, CancelException {
        return selectFromList(projects, showProjectEntry);
    }

    @Override
    public LocalDateTime selectLocalDateTimeFromList(List<LocalDateTime> dateTimes) throws EmptyListException, CancelException {
        return selectFromList(dateTimes, showLocalDateTimeEntry);
    }

    /**
     * Toont een tekstweergave van de details van een project.
     * Implementeert showProjectDetails uit UserInterface
     */
    @Override
    public void showProjectDetails(Project project) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        java.lang.System.out.println("*** Project details ***");
        String format = "%-25s %s %n";
        java.lang.System.out.printf(format, "Naam: ", project.getName());
        java.lang.System.out.printf(format, "Beschrijving: ", project.getDescription());
        java.lang.System.out.printf(format, "Status: ", project.getProjectStatus().name());
        String onTime = "ja";
        if (project.isOverTime()) {
            onTime = "nee";
        }
        java.lang.System.out.printf(format, "Op tijd: ", onTime);
        java.lang.System.out.printf(format, "Creation time: ", project.getCreationTime().format(formatter));
        java.lang.System.out.printf(format, "Due time: ", project.getDueTime().format(formatter));
        java.lang.System.out.printf(format, "Geschatte eindtijd: ", project.getEstimatedEndTime().format(formatter));
    }

    /**
     * Toont een tekstweergave van de details van een taak.
     * Implementeert showTaskDetails uit UserInterface
     */
    @Override
    public void showTaskDetails(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("*** Taak details ***");
        String format = "%-25s %s %n";
        System.out.printf(format, "Beschrijving: ", task.getDescription());

        String finishedStatus = task.getFinishedStatus();
        System.out.printf(format, "Status: ", task.getStatusString() + " " + finishedStatus); // TODO: hier stond getStatus.name(), we hadden toch nooit een functie .name() ?

        // on time?
        String onTime = "ja";
        if (task.isOverTime()) {
            long percentage = Math.round(task.getOverTimePercentage() * 100);
            onTime = "nee (" + percentage + "%)";
        }
        java.lang.System.out.printf(format, "Momenteel on time: ", onTime);

        Duration estimatedDuration = task.getEstimatedDuration();
        long estimatedDurationHours = estimatedDuration.getSeconds() / (60 * 60);
        long estimatedDurationMinutes = (estimatedDuration.getSeconds() % (60 * 60)) / 60;
        System.out.printf(format, "Geschatte duur: ", estimatedDurationHours + "u" + estimatedDurationMinutes + "min");

        double acceptDevPercent = task.getAcceptableDeviation() * 100;
        System.out.printf(format, "Aanvaardbare afwijking: ", acceptDevPercent + "%");

        if (task.getStartTime() != null) {
            System.out.printf(format, "Starttijd: ", task.getStartTime().format(formatter));
        }

        if (task.getEndTime() != null) {
            System.out.printf(format, "Eindtijd: ", task.getEndTime().format(formatter));
        }

        java.lang.System.out.println("Afhankelijk van:");
        if (!task.getDependingOnTasks().isEmpty()) {
            showTaskList(ImmutableList.copyOf(task.getDependingOnTasks()));
        } else {
            java.lang.System.out.println("Deze taak hangt niet van andere taken af");
        }

        if (task.getAlternativeTask() != null) {
            java.lang.System.out.printf(format, "Alternatieve taak: ", task.getAlternativeTask().getDescription());
        }
    }

    /**
     * Geeft de teksweergave weer van alle branchoffices.
     * @param branchOffices de lijst van branchoffices die in teksweergave moet weergeven worden.
     */
   @Override
    public BranchOffice selectBranchOfficeFromList(ImmutableList<BranchOffice> branchOffices) throws EmptyListException, CancelException {
        return selectFromList(branchOffices, showBranchOfficeEntry);
    }




    //    @Override
    public <T> T requestUserInput(String request, UserInput<T> userInput) throws CancelException {
        return userInput.getUserInput(request);
    }

    private void resolveCancel(String str) throws CancelException {
        if (Action.checkCancel(str)) {
            throw new CancelException("Canceled");
        }
    }

    private void resolveEmpty(String string) throws CancelException {
        if (string == null || string.isEmpty()) {
            throw new CancelException("Canceled omwille van lege response.");
        }
    }

    /**
     * Laat de gebruiker een geheel getal ingeven.
     * Implementeert requestNumber in UserInterface
     */
    @Override
    public int requestNumber(String request) throws CancelException {
        return getIntFromUser().getUserInput(request);
    }

    /**
     * Laat de gebruiker een double ingeven.
     * Implementeert requestDouble in UserInterface
     */
    @Override
    public double requestDouble(String request) throws CancelException {
        return getDoubleFromUser().getUserInput(request);
    }

    /**
     * Laat de gebruiker een string ingeven.
     * Implementeert requestString in UserInterface
     */
    @Override
    public String requestString(String request) throws CancelException {
        return getStringFromUser().getUserInput(request);
    }

    /**
     * Laat de gebruiker een dataum ingeven.
     * Indien de gebruikers niets invult, geeft deze methode null terug.
     * Implementeert requestDatum in UserInterface
     */
    @Override
    public LocalDateTime requestDatum(String request) throws CancelException {
        return getDateFromUser().getUserInput(request);
    }

    @Override
    public boolean requestBoolean(String request) throws CancelException {
        return getBooleanFromUser().apply(request + " (y/n)");
    }

    /**
     * Vraag een getal aan de user tussen een min en max waarde.
     *
     * @param userInput de functie waarmee de invoer aan de gebruiker gevraagd wordt.
     * @param min       De minimum toegelaten waarde (inclusief)
     * @param max       De maximum toegelaten waarde (inclusief)
     * @param <T>       Het Type van het gevraagde getal tussen min en max.
     * @return Een getal van Type <T> dat uit [min,max] komt.
     * @throws CancelException gooi indien de gebruiker het Command.CANCEL in geeft.
     */
    private <T extends Number & Comparable<T>> T numberBetween(UserInput<T> userInput, T min, T max) throws CancelException {
//        getMultipleNumberBetween(request,userInput,min,max,1,true)
        boolean correct = false;
        T response = null;
        while(!correct){
            try {
                response = userInput.apply("Gelieve een getal tussen " + min + " & " + max + " te geven.");
                correct = ((response.compareTo(min) > 0 || response.compareTo(min) == 0) && (response.compareTo(max) < 1 || response.compareTo(max) == 0));
            } catch (NumberFormatException e) {
                correct = false;
            }
            if (!correct) {
                printMessage("Verkeerde input, probeer opnieuw.");
            }
        }
        return response;
    }
    /**
     * Vraag meerdere getallen aan de user tussen een min en max waarde.
     *
     * @param <T>       Het Type van het gevraagde getal tussen min en max.
     * @param userInput de functie waarmee de invoer aan de gebruiker gevraagd wordt.
     * @param min       De minimum toegelaten waarde (inclusief)
     * @param max       De maximum toegelaten waarde (inclusief)
     * @param amount    Hoeveel getallen er gevraagd worden aan de gebruiker.
     * @param exactAmount Moet er minstens "amount" nummers gegeven worden. (ja of nee)
     * @return          Een lijst getallen van Type <T> die uit [min,max] komen.
     *                  Indien exactAmount == true, is de grootte van de lijst gelijk aan amount.
     *                  Anders bezit de lijst maximum "amount" nummers.
     * @throws CancelException gooi indien de gebruiker het Command.CANCEL in geeft.
     */
    public <T extends Number & Comparable<T>> List<T> getMultipleNumberBetween(String request,UserInput<List<T>> userInput, T min, T max,int amount,boolean exactAmount) throws CancelException {
        final boolean[] correct = {true};
        List<T> result = new ArrayList<>();

        do {
            try {
                correct[0] = true;
                result = userInput.apply(request);

                result.forEach(t -> correct[0] &= (t.compareTo(min) > 0 || t.compareTo(min) == 0) && (t.compareTo(max) < 1 || t.compareTo(max) == 0));
                correct[0] &= (exactAmount)?result.size() == amount:result.size() <= amount;
            } catch (NumberFormatException e) {
                correct[0] = false;
            }
            if (!correct[0]) {
                printMessage("Verkeerde inputb, probeer opnieuw.");
            }
        } while (!correct[0]);
        return result;
    }

    private UserInput<String> getStringFromUser(){
        UserInput<String> getStringFromUser = request -> {
            printMessage(request);
            String result = "";
            try {
                result = bufferedReader.readLine();
                resolveCancel(result);
                resolveEmpty(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        };
        return getStringFromUser;
    }
    
    private UserInput<Double> getDoubleFromUser(){
        UserInput<Double> getDoubleFromUser = request -> {
            boolean correct = false;
            Double result = null;
            do {
                try {
                    String response = getStringFromUser().apply(request);
                    result = new Double(response);
                    correct = true;
                } catch (NumberFormatException e) {
                    printMessage("Verkeerde input, probeer opnieuw.");
                    correct = false;
                }
            } while (!correct);
            return result;
        };
        return getDoubleFromUser;
    }

    private UserInput<Integer> getIntFromUser(){
        UserInput<Integer> getIntFromUser = request -> {

            boolean correct = false;
            Integer result = null;
            do {
                try {
                    String response = getStringFromUser().apply(request);
                    result = new Integer(response);
                    correct = true;
                } catch (NumberFormatException e) {
                    printMessage("Verkeerde input, probeer opnieuw.");
                    correct = false;
                }
            } while (!correct);

            return result;
        };
        return getIntFromUser;
    }

    /**
     * @return userInput<List<Integer>> die meerdere int inputs vraag aan de gebruiker
     * Input gescheiden door een komma
     */
    private UserInput<List<Integer>> getMultipleIntFromUser(){
        UserInput<List<Integer>> getMultipleIntFromUser = request ->{
            boolean correct = false;
            ArrayList<Integer> result = new ArrayList<>();
            do {
                try {
                    String response = getStringFromUser().apply(request);
                    List<String> numbers = Arrays.asList(response.split(","));
                    final ArrayList<Integer> finalResult = result;
                    numbers.stream().forEach(num -> finalResult.add(new Integer(num)));
                    correct = true;
                } catch (NumberFormatException e) {
                    printMessage("Verkeerde input, probeer opnieuw.");
                    correct = false;
                }
            } while (!correct);

            return result;
        };
        return getMultipleIntFromUser;
    }
    private UserInput<LocalDateTime> getDateFromUser() {
        UserInput<LocalDateTime> getDateFromUser = request -> {
            LocalDateTime result = null;
            boolean correct = false;
            do {
                String response = getStringFromUser().apply(request + " formaat: yyyy-mm-dd hh:mm");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                try {
                    result = LocalDateTime.parse(response, formatter);
                    correct = true;
                } catch (Exception e) {
                    printMessage("Verkeerde input, komt niet overeen met het formaat. Probeer opnieuw");
                    correct = false;
                }
            } while (!correct);
            return result;
        };
        return getDateFromUser;
    }

    private UserInput<Boolean> getBooleanFromUser(){
        UserInput<Boolean> getBooleanFromUser = request -> {
            boolean result = false;
            boolean quit = false;
            do {
                String response = getStringFromUser().apply(request);
                if (response.equalsIgnoreCase("y")) {
                    result = true;
                    quit = true;
                } else if (response.equalsIgnoreCase("n")) {
                    result = false;
                    quit = true;
                } else {
                    quit = false;
                    printMessage("Verkeerde input, probeer opnieuw.");
                }
            } while (!quit);
            return result;
        };
        return getBooleanFromUser;
    }

    @Override
    public <T> List<T> selectMultipleFromList(String request, List<T> list, List<T> preselectedList, int maxSelected,boolean exactAmount, Function<T, String> listEntryPrinter) {
        if(list.isEmpty()){//Lijst is leeg, kan niet selecteren.
            throw new EmptyListException("Lege lijst: er kan dus niet geselecteerd worden uit de lijst.");
        }else if (!list.containsAll(preselectedList)) {
            //Indien preselectedList niet in list zit, gooi cancelexception
            throw new CancelException("De voor geselecteerde lijst, komt niet helemaal voor in de totale lijst");
        }
        //BiConsumer printen van de lijst, met geselecteerde items
        BiConsumer<List<T>, List<T>> printListAndSelection = (totalList, selectedList) -> {
            ArrayList<Integer> selectedIndexes = new ArrayList<>();
            selectedList.forEach(element -> selectedIndexes.add(list.indexOf(element)));

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < totalList.size(); i++) {
                String isSelected = "[" + (selectedIndexes.contains(i) ? "x" : ".") + "]";
                sb.append(String.format("%3d. %s %s\n", i, isSelected, listEntryPrinter.apply(totalList.get(i))));
            }
            printMessage(sb.toString());
        };
        //Functie om de selectie te maken
        Function<List<T>,List<T>> listSelector = (totalList) -> {
            int max = list.size() - 1;
            int min = 0;
            String req = "Gelieve "+ (!exactAmount?"(tot)":"") + maxSelected + " getallen tussen " + min + " & " + max + " te geven. (gescheiden met een komma)";

            List<Integer> indexSelected = getMultipleNumberBetween(req,getMultipleIntFromUser(), min, max, maxSelected,exactAmount);
            ArrayList<T> newSelection = new ArrayList<>();
            indexSelected.stream().forEach(i -> newSelection.add(totalList.get(i)));
            return newSelection;
        };

        boolean done;
        List<T> selection;
        printListAndSelection.accept(list, preselectedList);
        //Print request
        printMessage(request);
        do {
            //Printen van lijst met zijn geselecteerde items.
            selection = listSelector.apply(list);
            printListAndSelection.accept(list, selection);
            done = requestBoolean("gedaan met selecteren?");
        } while (!done);
        return selection;
    }

    /**
     * Laat de gebruiker een element kiezen uit de gegeven lijst.
     *
     * @param tList            De lijst waaruit men kan kiezen
     * @param listEntryPrinter De manier waarop ieder element wordt voorgesteld
     * @param <T>              Het type van het geselecteerde element
     * @return Geeft het element dat de gebruiker selecteerde.
     * @throws CancelException    indien gebruiker Command.CANCEL ingeeft als invoer.
     * @throws EmptyListException gooi indien de gegeven lijst leeg is.
     */
    @Override
    public <T> T selectFromList(List<T> tList, Function<T, String> listEntryPrinter) throws CancelException, EmptyListException {
        Function<List<T>, T> listSelector = list -> {
            if (list == null || list.isEmpty()) {
                throw new CancelException("Lege lijst: er kan dus niet geselecteerd worden uit de lijst.");
            }
            int max = list.size() - 1;
            int min = 0;
            int selection = numberBetween(getIntFromUser(), min, max);
            return list.get(selection);
        };
        if (!tList.isEmpty()) {
            printList(tList, listEntryPrinter);
            T selection = listSelector.apply(tList);
            return selection;
        } else {
            throw new EmptyListException("Lege lijst: er kan dus niet geselecteerd worden uit de lijst.");
        }
    }

    /**
     * Gegeven een lijst, geef een grafische voorstelling.
     * Plaatst een cijfer voor ieder element van de lijst!!
     *
     * @param list             De weer te geven lijst.
     * @param listEntryPrinter De manier waarop ieder element van de lijst wordt voorgesteld.
     * @param <T>              Het type van de elementen
     */
    private <T> void printList(List<T> list, Function<T, String> listEntryPrinter) {
        Consumer<List<T>> listPrint = listTemp -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listTemp.size(); i++) {
                sb.append(String.format("%3d. %s", i, listEntryPrinter.apply(listTemp.get(i))));
                sb.append("\n");
            }
            printMessage(sb.toString());
        };
        listPrint.accept(list);
    }

    @Override
    public void showHelp(AbstractController abstractController) throws IllegalArgumentException {
        ArrayList<Action> list = new ArrayList<>(controllerStack.getAcceptableActions(abstractController));

        StringBuilder sb = new StringBuilder();
        for (Action cmd : list) {
            sb.append(String.format("%-2s%s%1s", "|", cmd.getActionStr(), " "));
        }
        sb.append("| \n");
        printMessage(abstractController.getClass().getSimpleName() + ">  " + sb.toString());
    }
}