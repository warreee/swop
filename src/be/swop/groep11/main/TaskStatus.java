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
     * @param status De nieuwe status om te checken
     * @param task De taak
     * @return true alss status een geldige nieuwe status is voor een taak
     */
    public static boolean isValidNewStatus(TaskStatus status, Task task) {
        TaskStatus currentStatus = task.getStatus();
        switch (currentStatus) {
            case AVAILABLE:
                if (status == FINISHED || status == FAILED)
                    return task.getStartTime() != null && task.getEndTime() != null;
                return true;
            case UNAVAILABLE:
                if (status == AVAILABLE) {
                    for (Task dependingOn : task.getDependingOnTasks())
                        if (task.getStatus() != FINISHED)
                            return false;
                    return true;
                }
                return status == UNAVAILABLE;
            case FINISHED:
                return status == FINISHED;
            case FAILED:
                return status == FAILED;
        }
        return false;
    }

}
