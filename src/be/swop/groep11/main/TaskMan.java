package be.swop.groep11.main;

import java.time.Instant;

/**
 * Created by Ronald on 7/03/2015.
 */
public class TaskMan {

    /**
     * Initialiseren van TaskMan men huidige systeem tijd (machine)
     * alsook een nieuwe ProjectRepository.
     */
    public TaskMan(){
        this(Instant.now());
    }
    /**
     * Initialiseren van TaskMan met als huidige systeem tijd de gegeven systeemtijd
     * alsook een nieuwe ProjectRepository.
     * @param currentSystemTime de systeemtijd
     * @throws java.lang.IllegalArgumentException
     *           | De gegeven systeemtijd is niet geldig.
     *           | !canHaveAsSystemTime(systemTime)
     */
    private TaskMan(Instant currentSystemTime) throws IllegalArgumentException {
        this.projectRepository = new ProjectRepository();
        setCurrentSystemTime(currentSystemTime);
    }

    /**
     * @return Geeft de projectRepository terug.
     */
    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    private ProjectRepository projectRepository;

    /**
     * Wijzigt huidige systeemtijd naar de gegeven systeemtijd.
     * @param systemTime De nieuwe systeemtijd
     * @throws java.lang.IllegalArgumentException
     *           | De gegeven systeemtijd is niet geldig.
     *           | !canHaveAsSystemTime(systemTime)
     */
    public void updateSystemTime(Instant systemTime) throws IllegalArgumentException{
            setCurrentSystemTime(systemTime);
    }

    /**
     * Geeft aan of de gegeven systeemtijd kan gebruikt worden als nieuwe systeemtijd.
     * @param newSystemTime De te valideren systeemtijd
     * @return  Waar indien CurrentSystemTime null is en newSystemTime niet null is.
     *          Of waar indien CurrentSystemTime en newSystemTime niet null zijn
     *          en bovendien currentSystemTime voor newSystemTime valt.
     *          | result ==
     *          | ( getCurrentSystemTime() == null && newSystemTime != null) ||
     *          | ( getCurrentSystemTime() != null &&
     *          | newSystemTime != null &&
     *          | getCurrentSystemTime().isBefore(newSystemTime))
     */
    private boolean canHaveAsSystemTime(Instant newSystemTime) {
        if(newSystemTime == null){
            return false;
        }
        if(this.currentSystemTime == null){
            return true;
        }
        if(this.currentSystemTime != null){
            //waar indien currentSystemTime vroeger is als newSystemTime
            return currentSystemTime.isBefore(newSystemTime);
        }
        return false;
    }

    private void setCurrentSystemTime(Instant currentSystemTime) throws IllegalArgumentException {
        if(canHaveAsSystemTime(currentSystemTime)) {
            this.currentSystemTime = currentSystemTime;
        }
        else{
            throw new IllegalArgumentException("Ongeldige systeemtijd");
        }
    }

    /**
     * @return Geeft de huidige systeemtijd terug.
     */
    public Instant getCurrentSystemTime() {
        return currentSystemTime;
    }

    private Instant currentSystemTime;
}
