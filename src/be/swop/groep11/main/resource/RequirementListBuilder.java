package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import be.swop.groep11.main.exception.IllegalRequirementAmountException;
import be.swop.groep11.main.exception.UnsatisfiableRequirementException;
import be.swop.groep11.main.resource.constraint.ResourceTypeConstraint;
import com.google.common.collect.ImmutableList;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

        /**
         * Berekend hoelang een Reservatie van alle Resources zou duren voor deze RequirementsList.
         * @param selectedStartTime De gegeven startTijd.
         * @param estimatedDuration De gegeven geschatte werk duration
         * @return Een Timespan van hoelang het zou duren.
         */
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

        /**
         *
         * @param startTime De gekozen starttijd
         * @return
         */
        @Override
        public LocalDateTime calculateNextPossibleStartTime(LocalDateTime startTime) {

            Set<AResourceType> types = requirements.keySet();
            LocalTime latestStartTime = getLatestStartTime(types);
            LocalTime earliestEndTime = getEarliestEndTime(types);

            LocalDateTime fullHourStartTime = getNextHour(startTime);

            if (fullHourStartTime.toLocalTime().isAfter(latestStartTime) && fullHourStartTime.toLocalTime().isBefore(earliestEndTime)){
                return checkWeekend(fullHourStartTime);
            }

            if (fullHourStartTime.toLocalTime().isBefore(latestStartTime)) {
                fullHourStartTime = fullHourStartTime.with(latestStartTime);
                return checkWeekend(fullHourStartTime);
            }

            if (fullHourStartTime.toLocalTime().isAfter(earliestEndTime)
                    || fullHourStartTime.toLocalTime() == earliestEndTime) {
                fullHourStartTime = fullHourStartTime.with(latestStartTime);
                fullHourStartTime = fullHourStartTime.plusDays(1);
                return checkWeekend(fullHourStartTime);
            }

            if (fullHourStartTime.toLocalTime() == latestStartTime) {
                return checkWeekend(fullHourStartTime);
            }



            throw new UnsupportedOperationException("Oeps hier mochten we niet komen");
        }

        private LocalDateTime checkWeekend(LocalDateTime startTime){
            if (startTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
                return startTime.plusDays(2);
            } else if (startTime.getDayOfWeek() == DayOfWeek.SUNDAY){
                return startTime.plusDays(1);
            } else {
                return startTime;
            }
        }

        /**
         * Geeft de laatste starttijd terug van een set van dailyavailabilties van resourcetypes
         * @param types
         * @return
         */
        private LocalTime getLatestStartTime(Set<AResourceType> types) {
            Optional<LocalTime> latestStartTime = types.stream().map(entry -> entry.getDailyAvailability().getStartTime()).max(Comparator.<LocalTime>naturalOrder());
            return latestStartTime.get();
        }


        private LocalTime getEarliestEndTime(Set<AResourceType> types) {
            Optional<LocalTime> earliestEndTime = types.stream().map(entry -> entry.getDailyAvailability().getEndTime()).min(Comparator.<LocalTime>naturalOrder());
            return earliestEndTime.get();
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
         * Haal het volgende volledige uur op van de gegeven tijd. Of de huidige tijd als dit al een volledig uur is.
         * @param dateTime Te te controleren tijd.
         * @return Het volledig uur.
         */
        private LocalDateTime getNextHour(LocalDateTime dateTime) {
            if (dateTime.getMinute() == 0){
                return dateTime;
            }
            else{
                return dateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
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

        /**
         * Geeft alle ResourceRequirements in deze lijst.
         */
        @Override
        public ImmutableList<ResourceRequirement> getRequirements() {
            return ImmutableList.copyOf(requirements.values());
        }

        /**
         * Geeft hoeveel resources er nodig zijn voor een bepaalde ResourceType.
         * @param type  Het AResourceType waarvan de hoeveelheid opgevraagd wordt.
         */
        @Override
        public int getRequiredAmountFor(AResourceType type) {
            if (containsRequirementFor(type)) {
                return getRequirementFor(type).getAmount();
            } else {
                return 0;
            }
        }

        /**
         * Geeft hoeveel developers er nodig zijn in deze lijst.
         */
        @Override
        public int getRequiredDevelopers() {
            int result = 0;
            for (ResourceRequirement resourceRequirement : requirements.values()) {
                if (resourceRequirement.isDeveloperRequirement()) {
                    result = resourceRequirement.getAmount();
                }
            }
            return result;
        }
    }
}
