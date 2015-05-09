package be.swop.groep11.main.core;

/**
 * Deze klasse stelt een Branch office voor.
 * Een branch office behoort tot een company.
 */
public class BranchOffice {

    private String name;
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isValidName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("De naam van een Branchoffice mag niet leeg zijn!");
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (isValidName(location)) {
            this.location = name;
        } else {
            throw new IllegalArgumentException("De naam van een Branchoffice mag niet leeg zijn!");
        }
    }

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }

    private boolean isValidLocation(String location) {
        return location != null && !location.isEmpty();
    }
}
