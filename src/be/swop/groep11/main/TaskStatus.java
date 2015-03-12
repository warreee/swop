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

            case UNAVAILABLE:
                if (newStatus == AVAILABLE) {
                    for (Task dependingOn : task.getDependingOnTasks())
                        if (dependingOn.getStatus() != FINISHED)
                            // TODO: alternatieve taak wordt vergeten.
                            return false;
                    return true;
                }
                return newStatus == UNAVAILABLE;
            case FINISHED:
                return newStatus == FINISHED;
            case FAILED:
                return newStatus == FAILED;
        }
        return false;
    }



}
