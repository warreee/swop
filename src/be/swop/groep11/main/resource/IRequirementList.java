package be.swop.groep11.main.resource;

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
