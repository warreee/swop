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
        // TODO mag deze eerste test hier wel?
        if (currentStatus == null)
            return true;
        switch (currentStatus) {
            case AVAILABLE:
                if (newStatus == FINISHED || newStatus == FAILED)
                    return task.getStartTime() != null && task.getEndTime() != null;
                return true;
            case UNAVAILABLE:
                if (newStatus == AVAILABLE) {
                    for (Task dependingOn : task.getDependingOnTasks())
                        if (dependingOn.getStatus() != FINISHED)
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
