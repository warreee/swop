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
     * Controleert of een status een geldige nieuwe status is voor een taak
     * @param status De nieuwe status om te checken
     * @param task De taak
     * @return true alss status een geldige nieuwe status is voor een taak
     */
    public static boolean canChangeStatus(TaskStatus status, Task task) {
        TaskStatus currentStatus = task.getStatus();
        switch (currentStatus) {
            case AVAILABLE:
                return true;
            case UNAVAILABLE:
                return status == AVAILABLE || status == UNAVAILABLE;
            case FINISHED:
                return status == FINISHED;
            case FAILED:
                return status == FAILED;
        }
        return false;
    }

}
