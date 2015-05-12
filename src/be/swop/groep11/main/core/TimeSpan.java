package be.swop.groep11.main.core;

import java.time.LocalDateTime;

/**
 * Een TimeSpan stelt een tijdsspanne voor met een starttijd en een eindtijd.
 */
public class TimeSpan{

    /**
     * Constructor om een nieuwe timespan aan te maken met een starttijd en eindtijd.
     * @param startTime De starttijd
     * @param endTime   De eindtijd
     * @throws java.lang.IllegalArgumentException Ongeldige start- en/of eindtijd
     */
    public TimeSpan(LocalDateTime startTime, LocalDateTime endTime) throws IllegalArgumentException {
        if (! isValidStartTimeEndTime(startTime, endTime))
            throw new IllegalArgumentException("Ongeldige start- en/of eindtijd");
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Controleert of een starttijd en een eindtijd geldig zijn.
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime   De eindtijd die gecontroleerd moet worden.
     * @return          Waar indien startTime, endTime niet null zijn. En bovendien startTime.isBefore(endTime)
     */
    private static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime){
        return startTime !=null && endTime != null && startTime.isBefore(endTime);
    }

    /**
     * Haalt de starttijd van deze TimeSpan op.
     * @return De starttijd.
     */

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * Haalt de eindtijd van deze TimeSpan op.
     * @return De eindtijd.
     */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    private final LocalDateTime startTime, endTime;


    /**
     * Controleert of deze tijdsspanne *strikt* overlapt met een andere tijdsspanne.
     * Een tijdsspanne die begint op het moment dat een andere eindigt, overlapt niet met de andere, en omgekeerd.
     * @param other De andere tijdsspanne.
     * @return      True als deze tijdsspanne strikt overlapt met de andere tijdsspanne.
     */
    public boolean overlapsWith(TimeSpan other) {
        if (other == null)
            return false;
        LocalDateTime startTime1 = this.getStartTime();
        LocalDateTime endTime1   = this.getEndTime();
        LocalDateTime startTime2 = other.getStartTime();
        LocalDateTime endTime2   = other.getEndTime();

        return ! (startTime1.isAfter(endTime2) || startTime1.equals(endTime2) || startTime2.isAfter(endTime1) || startTime2.equals(endTime1));
    }

    /**
     * Controleer of deze TimeSpan voor een andere TimeSpan valt
     * @param other De andere TimeSpan
     * @return      Niet waar indien other niet ge?nitialiseerd is.
     *              Waar indien de EndTime van deze TimeSpan voor de EndTime van other is.
     */
    public boolean isBefore(TimeSpan other) {
        return other != null && this.getEndTime().isBefore(other.getStartTime());
    }

    /**
     * Controleer of deze TimeSpan na een andere TimeSpan valt
     * @param other De andere TimeSpan
     * @return      Niet waar indien other niet ge?nitialiseerd is.
     *              Waar indien de StartTime van deze TimeSpan na de EndTime van other is.
     */
    public boolean isAfter(TimeSpan other) {
        return other != null && this.getStartTime().isAfter(other.getEndTime());
    }

    /**
     * Bereken een ITimeSpan die de tijdsperiode voorstelt die tussen deze en de gegeven ITimeSpan zit.
     * @param other De andere ITimeSpan
     * @return      null indien other overlapt met deze ITimeSpan.
     *              indien deze ITimeSpan niet overlapt met other geeft het een nieuwe ITimeSpan met
     *              StartTime het EndTime van de vroegste ITimeSpan en als EndTime de StartTime van de latere ITimeSpan
     */
    public TimeSpan timeBetween(TimeSpan other) {
        if (other == null) {
            return null;
        }else if (!this.overlapsWith(other)) {
            return this.isBefore(other) ? new TimeSpan(this.getEndTime(), other.getStartTime()) : new TimeSpan(other.getEndTime(), this.getStartTime());
        }else{
            return null;
        }
    }

    /**
     * Controle of dat de gegeven localDateTime aanwezig is in de ITimeSpan
     * @param dateTime De te controleren LocalDateTime
     * @return  Waar indien getStartTime.isBefore(dateTime) && getEndTime.isAfter(dateTime)
     */
    public boolean containsLocalDateTime(LocalDateTime dateTime) {
        return dateTime!= null && getStartTime().isBefore(dateTime) && getEndTime().isAfter(dateTime);
    }

    /**
     * @param other De andere tijdsspanne
     * @return True als deze tijdsspanne dezelfde start- en eindtijd heeft als een andere tijdsspanne
     */
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (! (other instanceof TimeSpan))
            return false;
        return this.getStartTime().equals(((TimeSpan) other).getStartTime()) && this.getEndTime().equals(((TimeSpan) other).getEndTime());
    }

    @Override
    public String toString() {
        return getStartTime().toString() + "  -  " + getEndTime().toString();
    }

    //TODO override hashCode
}
