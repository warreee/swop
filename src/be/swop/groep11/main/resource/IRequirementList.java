package be.swop.groep11.main.resource;

import java.util.Iterator;

/**
 * Created by Ronald on 9/04/2015.
 */
public interface IRequirementList{

    /**
     * Controleer of deze IRequirementList een requirement bevat voor het gegeven IResourceType
     * @param type  Het type waarvoor we controleren
     * @return      Waar indien deze IRequirementList een Requirement bevat voor het gegeven type.
     *              Anders niet waar.
     */
    boolean containsRequirementFor(AResourceType type);

    ResourceRequirement getRequirementFor(AResourceType type);

    /**
     * @param type  Het te controleren type.
     * @return      Geeft aan hoeveel ResourcesInstances er required zijn voor het gegeven type.
     */
    int countRequiredInstances(AResourceType type);

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
}
