package be.swop.groep11.main.resource;

import be.swop.groep11.main.core.TimeSpan;
import com.google.common.collect.ImmutableList;
import org.mockito.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Stelt een interface voor die elke instantie van een resource type moet implementeren.
 */
public interface ResourceInstance {

    /**
     * Geeft de naam van de resource instantie terug.
     */
    String getName();

    /**
     * Geeft het resource type van de resource instantie.
     */
    IResourceType getResourceType();

    /**
     * Berekent de eindtijd als de resource instantie vanaf een bepaalde starttijd voor een bepaalde duur zou
     * gereserveerd worden.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     */
    LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration);

}
