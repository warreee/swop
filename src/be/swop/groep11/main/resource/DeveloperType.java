package be.swop.groep11.main.resource;

import java.time.LocalTime;

public class DeveloperType extends AResourceType{
    /**
     * Maakt een DeveloperType aan
     **/
    protected DeveloperType() throws IllegalArgumentException {
        super("Developer");
        setDailyAvailability(new DailyAvailability(LocalTime.of(8, 0), LocalTime.of(17, 0)));
    }

    @Override
    public void addResourceInstance(String name) throws IllegalArgumentException {
        Developer dev = new Developer(name,this);
        addResourceInstance(dev);
    }
}
