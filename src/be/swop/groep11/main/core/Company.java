package be.swop.groep11.main.core;

import java.util.List;

/**
 * Deze klasse stelt een Company voor,
 * Een company bestaat uit verschillende Branchoffices.
 */
public class Company {

    private String name;
    private List branchOffices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isValidName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Een Company name kan niet leeg zijn!");
        }
    }


    private boolean isValidName(String name) {
        return name != null && !name.isEmpty();
    }


}
