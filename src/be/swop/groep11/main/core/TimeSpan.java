package be.swop.groep11.main.core;

import java.time.LocalDateTime;

/**
 * Een TimeSpan stelt een tijdsspanne voor met een starttijd en een eindtijd.
 */
public class TimeSpan {

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
     * Controleert of een starttijd en een eindtijd geldig zijn.
     * @param startTime De starttijd die gecontroleerd moet worden.
     * @param endTime   De eindtijd die gecontroleerd moet worden.
     * @return          Waar indien startTime, endTime niet null zijn. En bovendien startTime.isBefore(endTime)
     */
    public static boolean isValidStartTimeEndTime(LocalDateTime startTime, LocalDateTime endTime){
        return startTime !=null && endTime != null && startTime.isBefore(endTime);
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    private final LocalDateTime startTime, endTime;

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

}
