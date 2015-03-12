package be.swop.groep11.main;

/**
 * Stelt de status van een taak voor.
 */
public enum TaskStatus {

    AVAILABLE,
    UNAVAILABLE,
    FINISHED,
    FAILED;

    /**
     * Controleert of een status een geldige nieuwe status is voor een taak.
     * De geldige overgangen zijn:
     *
     *      AVAILABLE -> AVAILABLE
     *      AVAILABLE -> UNAVAILABLE
     *      AVAILABLE -> FINISHED/FAILED indien taak een start- en eindtijd heeft
     *
     *      UNAVAILABLE -> UNAVAILABLE
     *      UNAVAILABLE -> AVAILABLE indien alle taken waarvan task afhankelijk is beëindigd zijn
     *
     *      FINISHED -> FINISHED
     *
     *      FAILED -> FAILED
     *
     * @param newStatus De nieuwe status om te checken
     * @param task De taak
     * @return true als status een geldige nieuwe status is voor een taak
     */
    public static boolean isValidNewStatus(TaskStatus newStatus, Task task) {
        TaskStatus currentStatus = task.getStatus();
        if (currentStatus == null)
            return newStatus == AVAILABLE; // status van nieuwe taak = AVAILABLE
        switch (currentStatus) {
            case AVAILABLE:
                return checkAvailable(newStatus, task);

            case UNAVAILABLE:
                return checkUnavailable(newStatus, task);
            case FINISHED:
                return newStatus == FINISHED;
            case FAILED:
                return newStatus == FAILED;
        }
        return false;
    }

    /**
     * Controleer de overgang van UNVAILABLE -> *
     *
     * @param newStatus
     * @param task
     * @return true als het mag, anders false.
     */
    private static boolean checkUnavailable(TaskStatus newStatus, Task task) {
        if (newStatus == UNAVAILABLE) {
            for (Task dependingOn : task.getDependingOnTasks()) {
                Task t = dependingOn;

                while(true){
                    if(t.getStatus() == AVAILABLE){
                        return true; // Als er een taak is die nog bezig is, dan mag het nog.
                    }
                    if(t.getStatus() == UNAVAILABLE){
                        return true; // Een afhankelijke taak is nog niet klaar, dan deze zeker niet.
                    }
                    if(t.getStatus() == FINISHED){
                        break;
                    }
                    if(t.getStatus() == FAILED){
                        t = t.getAlternativeTask();
                        if(t == null){
                            return true; // Voor een gefaalde taak is geen alternatieve taak ingesteld.
                        }
                    }
                }
            }
            return false;
        }

        if (newStatus == AVAILABLE){

            for (Task dependingOn : task.getDependingOnTasks()) {
                Task t = dependingOn;
                while(true){
                    if(t.getStatus() == FINISHED){
                        break;
                    } else if(t.getStatus() == FAILED){
                        t = t.getAlternativeTask();
                        if(t == null){
                            return false; // Voor een gefaalde taak is geen alternatieve taak ingesteld.
                        }
                    } else {
                        return false; // Als de status van t niet FAILED of FINISHED is, mag er niet worden overgegaan.
                    }
                }
            }
            return true;
        }
        return false; // De enige mogelijke overgang is UNAVAILABLE -> AVAILABLE || UNAVAILABLE -> UNAVAILABLE
    }

    /**
     * Controleert de overgang Available -> *
     *
     * @param newStatus
     * @param task
     * @return true als het mag, anders false.
     */
    private static boolean checkAvailable(TaskStatus newStatus, Task task) {
        if (newStatus == FINISHED || newStatus == FAILED)
            return task.getStartTime() != null && task.getEndTime() != null;

        if (newStatus == UNAVAILABLE){

            if(task.getDependingOnTasks().size() == 0){
                // De taak hangt van niks af. Unavailable is niet mogelijk.
                return false;
            }

            for(Task dependingOn: task.getDependingOnTasks()){

                Task t = dependingOn;
                while(true){
                    if (t.getStatus() == FINISHED){
                        // Deze taak is gefinished, Controlleer de volgende.
                        break;
                    }
                    if(t.getStatus() == UNAVAILABLE){
                        // Deze taak is nog niet beschikbaar. Controlleer de volgende.
                        break;
                    }
                    if(t.getStatus() == AVAILABLE){
                        // Er is een afhankelijke taak die nog gedaan moet worden.
                        return true;
                    }
                    if (t.getStatus() == FAILED){
                        t = t.getAlternativeTask();
                        if (t == null){
                            // Er is geen alternatieve taak voor een gefaalde taak. De gegeven taak moet dus op unavailable komen te staan.
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }


}
