package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import com.google.common.collect.ImmutableList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * Interface voor RequirementsList.
 */
public interface IRequirementList{

    /**
     * Controleer of deze IRequirementList een requirement bevat voor het gegeven IResourceType
     * @param type  Het type waarvoor we controleren
     * @return      Waar indien deze IRequirementList een Requirement bevat voor het gegeven type.
     *              Anders niet waar.
     */
    boolean containsRequirementFor(AResourceType type);

    /**
     * Geef de ResourceRequirement met het RequiredType gelijk aan het gegeven AResourceType.
     * @param type      Het Required AResourceType van de gevraagde requirement.
     * @return          De ResourceRequirement geassocieerd met het gegeven type.
     *                  Indien die er niet is, null.
     */
    ResourceRequirement getRequirementFor(AResourceType type);

    /**
     * Geeft de nodige hoeveelheid voor het gegeven AResourceType terug.
     * @param type  Het AResourceType waarvan de hoeveelheid opgevraagd wordt.
     * @return      0 indien er geen requirement voor type is.
     *              Anders getRequirementFor(type).getAmount()
     */
    int getRequiredAmountFor(AResourceType type);

    /**
     * Geeft aan hoeveel Developers er nodig zijn.
     */
    int getRequiredDevelopers();

    /**
     * Controleer of deze lijst met Requirements geldig blijft. Indien er een requirement voor het gegeven IResourceType
     * en hoeveelheid zou worden toegevoegd.
     *
     * @param type      Het aangevraagde IResourceType
     * @param amount    De aangevraagde hoeveelheid
     * @return
     *        | Niet waar
     *          indien de DailyAvailability voor het aangevraagde ResourceType niet (min. 1 uur) overlapt
     *          met de huidige DailyAvailabilities geassocieerd met iedere Requirement in de lijst.
     *        | Niet waar
     *          indien aangevraagde hoeveelheid niet aanvaard kan worden
     *          door de aanwezige ResourceTypes geassocieerd met de requirements.
     *        | anders wel waar
     */
    boolean isSatisfiableFor(AResourceType type, int amount) ;

    /**
     * @return Een Iterator<ResourceRequirement>
     */
    Iterator<ResourceRequirement> iterator();

    /**
     * Bereken voor de gegeven StartTijd & geschatte werk duration hoe lang een resource in gebruik zal zijn
     * @param selectedStartTime De gegeven startTijd.
     * @param estimatedDuration De gegeven geschatte werk duration
     * @return  Een nieuwe TimeSpan berekent a.d.h.v. de getShortestDailyAvailability() en de estimatedDuration,
     *          eerst berekenen we de RequiredDuration dewelke men optelt met de gegeven startTijd om de eindTijd te vormen.
     */
    TimeSpan calculateReservationTimeSpan(LocalDateTime selectedStartTime, Duration estimatedDuration);

    /**
     * Bereken voor de gegeven starttijd wanneer de volgende mogelijke starttijd is rekeninghoudend met alle
     * DailyAvailabilities van de gekozen Requirements.
     * @param startTime De gekozen starttijd
     * @return De starttijd wanneer er gestart kan worden.
     */
    LocalDateTime calculateNextPossibleStartTime(LocalDateTime startTime);

    /**
     * Geeft een RequirementsList met ResourceRequirements terug die voldoen aan de vereisten.
     * @return
     */
    ImmutableList<ResourceRequirement> getRequirements();
}
