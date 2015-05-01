package be.swop.groep11.main.resource;

import java.time.LocalTime;

/**
 * Stelt het DeveloperType voor.
 */
public class DeveloperType extends AResourceType{
    /**
     * Maakt een DeveloperType aan
     **/
    protected DeveloperType() throws IllegalArgumentException {
        super("Developer");
        setDailyAvailability(new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(17, 0)));
    }

    /**
     * Voegt een nieuwe DeveloperInstance toe.
     * @param name De naam van de ResourceInstance die moet worden toegevoegd.
     * @throws IllegalArgumentException Wordt gegooid wanneer de naam foute waarden bevat.
     */
    @Override
    protected void addResourceInstance(String name) throws IllegalArgumentException {
        Developer dev = new Developer(name,this);
        addResourceInstance(dev);
    }
}
