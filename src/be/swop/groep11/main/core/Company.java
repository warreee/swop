package be.swop.groep11.main.core;

import be.swop.groep11.main.resource.ResourceTypeRepository;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Deze klasse stelt een company voor,
 * Een company heeft een naam, heeft een resource type repository
 * en bestaat uit verschillende branch offices.
 *
 * Heeft de verantwoordelijkheid voor het bijhouden van BranchOffices & het aanmaken ervan.
 */
public class Company {

    private String name;
    private final List<BranchOffice> branchOffices;

    private final ResourceTypeRepository resourceTypeRepository;

    /**
     * Constructor om een nieuwe company te maken.
     *
     * @param name                   Naam van de company
     * @param resourceTypeRepository Resource type repository van de company
     * @throws IllegalArgumentException Ongeldige naam, resource type repository of branch offices
     */
    public Company(String name, ResourceTypeRepository resourceTypeRepository, SystemTime systemTime) throws IllegalArgumentException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Ongeldige naam");
        }
        if (resourceTypeRepository == null) {
            throw new IllegalArgumentException("Ongeldige resource type repository");
        }
        this.name = name;
        this.resourceTypeRepository = resourceTypeRepository;
        this.branchOffices = new ArrayList<>();
        setSystemTime(systemTime);
    }

    /**
     * Voegt een branch office toe aan deze company.
     * @throws IllegalArgumentException Ongeldige branch office
     */
    public void addBranchOffice(BranchOffice branchOffice) {
        if (! canAddBranchOffice(branchOffice)) {
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
     * Zet de naam van deze Company.
     * @param name de naam
     */
    public void setName(String name){
        if(!isValidName(name)){
            throw new IllegalArgumentException("Geen geldige naam aan Company gegeven.");
        }
        this.name = name;
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
    public boolean canAddBranchOffice(BranchOffice branchOffice) {
        return branchOffice != null && !getBranchOffices().contains(branchOffice); // en heeft corresponderende typeRepository?
    }

    /**
     * Haalt de systeemtijd van deze Company op.
     */
    public SystemTime getSystemTime() {
        return systemTime;
    }

    /**
     * Zet de systeemtijd van deze Company.
     * @param systemTime De systeemtijd
     */
    public void setSystemTime(SystemTime systemTime) throws IllegalArgumentException{
        if (!isValidSystemTime(systemTime)) {
            throw new IllegalArgumentException("Ongeldige systemTime");
        }
        this.systemTime = systemTime;
    }

    private boolean isValidSystemTime(SystemTime systemTime) {
        return systemTime != null && getSystemTime() == null;
    }

    private SystemTime systemTime;

    /**
     * Haalt alle projecten op die binnen deze Company bekend zijn.
     */
    public ImmutableList<Project> getAllProjects() {
        ArrayList<Project> projs = new ArrayList<>();
        getBranchOffices().forEach(bo -> projs.addAll(bo.getProjects()));
        return ImmutableList.copyOf(projs);
    }

}
