package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.ResourceTypeRepository;

import java.time.LocalDateTime;

/**
 * TMSystem bevat de systeemtijd en houdt een project repository bij.
 */
public class TMSystem {


    /**
     * Initialiseren van TaskMan men huidige systeem tijd (machine)
     * alsook een nieuwe ProjectRepository & nieuwe ResourceTypeRepository
     */
    public TMSystem(){
        this(LocalDateTime.now());
    }
    /**
     * Initialiseren van TaskMan met als huidige systeem tijd de gegeven systeemtijd
     * alsook een nieuwe ProjectRepository.
     * @param currentSystemTime de systeemtijd
     * @throws java.lang.IllegalArgumentException
     *           | De gegeven systeemtijd is niet geldig.
     *           | !canHaveAsSystemTime(systemTime)
     */
    public TMSystem(LocalDateTime currentSystemTime) throws IllegalArgumentException {
        SystemTime systemTime = new SystemTime();
        this.projectRepository = new ProjectRepository(systemTime);
        this.resourceTypeRepository = new ResourceTypeRepository();
        setCurrentSystemTime(currentSystemTime);
    }

    /**
     * @return Geeft de projectRepository terug.
     */
    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    private final ProjectRepository projectRepository;
    private final ResourceTypeRepository resourceTypeRepository;


    /**
     * Wijzigt huidige systeemtijd naar de gegeven systeemtijd.
     * @param systemTime De nieuwe systeemtijd
     * @throws java.lang.IllegalArgumentException
     *           | De gegeven systeemtijd is niet geldig.
     *           | !canHaveAsSystemTime(systemTime)
     */
    public void updateSystemTime(LocalDateTime systemTime) throws IllegalArgumentException{
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
    private boolean canHaveAsSystemTime(LocalDateTime newSystemTime) {
        if(newSystemTime == null){
            return false;
        }
        if(this.currentSystemTime == null){
            return true;
        }
        //waar indien currentSystemTime vroeger is als newSystemTime
        return currentSystemTime.isBefore(newSystemTime);
    }

    private void setCurrentSystemTime(LocalDateTime currentSystemTime) throws IllegalArgumentException {
        if(!canHaveAsSystemTime(currentSystemTime)) {
            throw new IllegalArgumentException("Ongeldige systeemtijd");
        }

        this.currentSystemTime = currentSystemTime;
    }

    /**
     * @return Geeft de huidige systeemtijd terug.
     */
    public LocalDateTime getCurrentSystemTime() {
        return currentSystemTime;
    }

    private LocalDateTime currentSystemTime;
}
