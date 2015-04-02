package be.swop.groep11.main;

import java.time.Duration;
import java.time.LocalDateTime;

/**
<<<<<<< HEAD
 * Stelt een interface voor die elke instantie van een ResourceType moet implementeren.
 */
public interface ResourceInstance {

    /**
     * Controleert of deze resource instantie beschikbaar is vanaf een gegeven starttijd voor een gegeven duur.
     * Hierbij wordt ook rekening gehouden met de huidige reservaties voor de resource instantie.
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return True als deze resource instantie beschikbaar is.
     */
    public boolean isAvailable(LocalDateTime startTime, Duration duration);

    /**
     * Berekent de "eindtijd" van deze resource instantie: dit is het moment waarop de resource wordt vrijgegeven wanneer
     * die vanaf een gegeven starttijd voor een gegeven duur zou gereserveerd worden.
     * Hierbij wordt GEEN rekening gehouden met de huidige reservaties voor de resource instantie!
     * @param startTime De gegeven starttijd
     * @param duration  De gegeven duur
     * @return De eindtijd van deze resource instantie.
     */
    public LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration);

=======
 * Created by robin on 2/04/15.
 */
public interface ResourceInstance {

    public boolean isAvailable(LocalDateTime start, Duration duration);
>>>>>>> Nieuwe klassen aangemaakt.
}
