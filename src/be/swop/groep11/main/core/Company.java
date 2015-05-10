package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.ResourceTypeRepository;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Deze klasse stelt een company voor,
 * Een company heeft een naam, heeft een resource type repository
 * en bestaat uit verschillende branch offices.
 */
public class Company {

    private final String name;
    private final List<BranchOffice> branchOffices;

    private final ResourceTypeRepository resourceTypeRepository;

    /**
     * Constructor om een nieuwe company te maken.
     * @param name                   Naam van de company
     * @param resourceTypeRepository Resource type repository van de company
     * @param branchOffices          Branch offices van de company
     * @throws IllegalArgumentException Ongeldige naam, resource type repository of branch offices
     */
    public Company(String name, ResourceTypeRepository resourceTypeRepository, BranchOffice... branchOffices) throws IllegalArgumentException {
        if (! isValidName(name)) {
            throw new IllegalArgumentException("Ongeldige naam");
        }
        this.name = name;

        if (resourceTypeRepository == null) {
            throw new IllegalArgumentException("Ongeldige resource type repository");
        }
        this.resourceTypeRepository = resourceTypeRepository;

        this.branchOffices = new ArrayList<>();
        for (BranchOffice branchOffice : branchOffices) {
            this.addBranchOffice(branchOffice);
        }
    }

    /**
     * Voegt een branch office toe aan deze company.
     * @throws IllegalArgumentException Ongeldige branch office
     */
    public void addBranchOffice(BranchOffice branchOffice) {
        if (! isValidBranchOffice(branchOffice)) {
            throw new IllegalArgumentException("Ongeldige branch office");
        }
        this.branchOffices.add(branchOffice);
    }

    /**
     * Geeft een immutable list van de branch offices van deze company.
     */
    public ImmutableList<BranchOffice> getBranchOffices() {
        return ImmutableList.copyOf(this.branchOffices);
    }

    /**
     * Geeft de naam van deze company.
     */
    public String getName() {
        return name;
    }

    /**
     * Geeft de resource type repository van deze company.
     */
    public ResourceTypeRepository getResourceTypeRepository() {
        return resourceTypeRepository;
    }

    /**
     * Controleert of een naam geldig is.
     * @return True als de naam niet null en niet leeg is.
     */
    public static boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    /**
     * Controleert of een branch office geldig is.
     * @return True als de branchOffice niet null is.
     */
    public static boolean isValidBranchOffice(BranchOffice branchOffice) {
        return branchOffice != null;
    }

}
