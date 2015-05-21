package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.UnsatisfiableRequirementException;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Aanmaken van requirements en bijhouden in een lijst
 */
public class RequirementListBuilder {

    private RequirementList reqList;
    private ResourceRepository resourceRepository;

    /**
     * Constructor voor nieuwe RequirementListBuilder
     */
    public RequirementListBuilder(ResourceRepository resourceRepository) {
        this.reqList = new RequirementList();
        setResourceRepository(resourceRepository);
    }

    /**
     * Voeg een nieuwe Requirement toe voor het gegeven type en de gevraagde amount
     * @param type      Het IResourceType voor de Requirement
     * @param amount    De hoeveelheid voor de Requirement
     * @throws IllegalArgumentException
     *
     * @throws IllegalRequirementAmountException
     *
     */
    public void addNewRequirement(AResourceType type, int amount) throws IllegalRequirementAmountException,IllegalArgumentException,UnsatisfiableRequirementException {
        this.reqList.addRequirement(type,amount);
    }

    /**
     * Geeft alle requirements terug die al in deze builder zitten.
     * @return Een lijst met requirements.
     */
    public IRequirementList getRequirements(){
        return this.reqList;
    }

    private void setResourceRepository(ResourceRepository resourceRepository) {
        checkResourceRepository(resourceRepository);
        this.resourceRepository = resourceRepository;
    }

    private void checkResourceRepository(ResourceRepository resourceRepository){
        if(resourceRepository == null){
            throw new IllegalArgumentException("ResourceRepository mag niet 'null' zijn.");
        }
    }

    /**
     * Private klasse die de interface IRequirementList implementeerd.
     */
    private class RequirementList implements IRequirementList {

        /**
         * Package accesable constructor voor bescherming.
         */
        RequirementList() {
        }

        private final HashMap<AResourceType,ResourceRequirement> requirements = new HashMap<>();

        /**
         * Controleer of er voor het gegeven AResourceType een requirements aanwezig is.
         * @param type  Het type waarvoor we controleren
         * @return True als dit aanwezig is anders False.
         */
        @Override
        public boolean containsRequirementFor(AResourceType type) {
            return requirements.containsKey(type);
        }

        /**
         * Controleer of deze lijst met Requirements geldig blijft. Indien er een requirement voor het gegeven IResourceType
         * en hoeveelheid zou worden toegevoegd.
         *
         * @param requestedType Het aangevraagde IResourceType
         * @param amount        De aangevraagde hoeveelheid
         * @return
         *        | Niet waar indien de DailyAvailability voor het aangevraagde ResourceType niet (min. 1 uur) overlapt
         *        met de huidige DailyAvailabilities geassocieerd met iedere Requirement in de lijst.
         *        | Niet waar indien aangevraagde hoeveelheid niet aanvaard kan worden
         *        door de aanwezige ResourceTypes geassocieerd met de requirements.
         *        | anders wel waar
         */
        @Override
        public boolean isSatisfiableFor(AResourceType requestedType,int amount) {
            // Niet waar indien de DailyAvailability voor het aangevraagde ResourceType niet overlapt (min. 1 uur)
            // met de huidige DailyAvailabilities overeenkomstig met iedere Requirement in de lijst
            if(!hasOverlappingAvailability(requestedType)){
                return false;
            }
            ArrayList<ResourceTypeConstraint> cons = new ArrayList<>();
            for (AResourceType type : requirements.keySet()) {
                cons.addAll(type.getTypeConstraints());
            }

            for (ResourceTypeConstraint constraint : cons) {
                if (!constraint.isAcceptableAmount(requestedType, amount)) {
                    return false;
                }
                //Nog nodig?
        /*        for (ResourceTypeConstraint requestedCon : requestedType.getTypeConstraints()) {
                    if (constraint.contradictsWith(requestedCon, amount)) {
                        return false;
                    }
                }*/
            }

            return true;
        }

        /**
         * Geeft een iterator terug om over alle waarden in deze lijst te itereren.
         * @return De iterator.
         */
        @Override
        public Iterator<ResourceRequirement> iterator() {
            return this.requirements.values().iterator();
        }


        @Override
        public TimeSpan calculateReservationTimeSpan(LocalDateTime selectedStartTime, Duration estimatedDuration) {
            Set<AResourceType> types = requirements.keySet();

            Optional<LocalDateTime> date = types.stream().map(type -> {
                LocalDateTime max = type.calculateEndTime(selectedStartTime, estimatedDuration);
                return max;
            }).max((o1, o2) -> o1.compareTo(o2));

            LocalDateTime end = date.get();

            return new TimeSpan(selectedStartTime, end);
        }

        @Override
        public LocalDateTime calculateNextPossibleStartTime(LocalDateTime startTime) {
            Set<AResourceType> types = requirements.keySet();

            for(AResourceType type: types){
                if(!type.getDailyAvailability().containsDateTime(startTime) || type.getDailyAvailability().getEndTime().equals(startTime.toLocalTime())){
                    if(type.getDailyAvailability().getStartTime().isAfter(startTime.toLocalTime())){
                        // De starttijd ligt voor het beschikbare tijdsbestek van de DailyAvailability. We nemen dus de
                        // begintijd van de DailyAvailability.
                        startTime = startTime.with(type.getDailyAvailability().getStartTime());
                    } else {
                        // De starttijd ligt na het beschikbare tijdbestek van de DailyAvailability. We nemen dus
                        // de begintijd van DailyAvailability, maar dan de volgende dag.
                        startTime = startTime.with(type.getDailyAvailability().getStartTime()).plusDays(1);
                    }
                }
            }
            return startTime;
        }

        /**
         * Controleer of alle DailyAvailabilities van deze lijst overlappen met de DailyAvailability van requestedType.
         * @param requestedType Het te controleren type.
         * @return True als dit zo is anders False.
         */
        private boolean hasOverlappingAvailability(AResourceType requestedType){
            List<DailyAvailability> availabilities = new ArrayList<>();
            for(AResourceType type : requirements.keySet()){
                availabilities.add(type.getDailyAvailability());
            }
            if(availabilities.isEmpty()){
                // Er zijn geen requirements, dus het requestedType is het enige dat beschikbaar moet zijn.
                return true;
            }else{
                return requestedType.getDailyAvailability().overlapsWith(availabilities);
            }
        }

        /**
         * Voegt een Requirement toe aan deze lijst.
         * @param requiredType Het AResourceType dat required is.
         * @param amount Hoeveel er required zijn.
         * @throws IllegalRequirementAmountException
         * @throws IllegalArgumentException
         * @throws UnsatisfiableRequirementException Wordt gegooid indien er conflicten zijn met het toevoegen van requiredType.
         */
        private void addRequirement(AResourceType requiredType,int amount) throws IllegalRequirementAmountException,IllegalArgumentException,UnsatisfiableRequirementException{
            if(!isSatisfiableFor(requiredType, amount)){
                throw new UnsatisfiableRequirementException();
            }else {
                //Indien er al een requirement is voor type zijn we reeds zeker dat een nieuwe requirement de plaats mag vervangen.
                // Plus de gevraagde hoeveelheid is geen probleem
                ResourceRequirement requirement = new ResourceRequirement(resourceRepository, requiredType, amount);
                requirements.put(requiredType,requirement);
                requiredType.getTypeConstraints().forEach(constraint -> {
                    if (constraint.getMin() > 0 && constraint.getMax() >= constraint.getMin()) {
                        addRequirement(constraint.getConstrainingType(), constraint.getMin());
                    }
                });
            }
        }

        /**
         * Geeft de ResourceRequirement terug voor het gegeven AResourceType.
         * @param type      Het Required AResourceType van de gevraagde requirement.
         * @return De ResourceRequirement.
         */
        public ResourceRequirement getRequirementFor(AResourceType type) {
            return requirements.get(type);
        }
    }
}
