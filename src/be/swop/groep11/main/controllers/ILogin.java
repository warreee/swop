package be.swop.groep11.main.controllers;

import be.swop.groep11.main.core.BranchOffice;
import be.swop.groep11.main.resource.Developer;
import be.swop.groep11.main.resource.ProjectManager;

/**
 * Created by Ronald on 14/05/2015.
 */
public interface ILogin {

    /**
     * @return  Waar indien er een ProjectManager ge?dentificeerd is, uit de huidige BranchOffice. Anders niet waar.
     */
    boolean hasIdentifiedProjectManager();

    /**
     * @return  Waar indien er een Developer ge?dentificeerd is, uit de huidige BranchOffice. Anders niet waar.
     */
    boolean hasIdentifiedDeveloper();

    /**
     * @return  Waar indien er een BranchOffice ge?dentificeerd. Anders niet waar.
     */
    boolean hasIdentifiedBranchOffice();

    /**
     * @return  Waar indien er een BranchOffice is geselecteerd, en een ProjectManager of
     *          Developer die deel uit maken van de geselecteerde BranchOffice. Anders niet waar
     */
    boolean hasIdentifiedUserAtBranchOffice();

    /**
     * Geeft de geselecteerde Project Manager terug.
     * @return  Een projectManager of null indien er een developer geselecteerd is, of geen van beide.
     */
    ProjectManager getProjectManager();

    /**
     * Geeft de geselecteerde Developer terug.
     * @return  Een Developer of null indien er een projectManager geselecteerd is, of geen van beide.
     */
    Developer getDeveloper();

    /**
     * Geeft de geselecteerde BranchOffice
     * @return  Indien hasIdentifiedUserAtBranchOffice() geeft het de geselecteerde BranchOffice(), anders null.
     */
    BranchOffice getBranchOffice();
}
