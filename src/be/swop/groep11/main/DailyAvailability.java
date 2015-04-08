package be.swop.groep11.main;

import java.time.*;

/**
 * Stelt de dagelijkse beschikbaarheid van een resource voor met een starttijd en een eindtijd.
 * Een resource met een dagelijkse beschikbaarheid is slechts beschikbaar van maandag tot en met vrijdag, telkens
 * tussen de starttijd en eindtijd.
 */
public class DailyAvailability {

    private final LocalTime startTime, endTime;

    /**
     * Constructor om een nieuwe dagelijkse beschikbaarheid aan te maken.
     * @param startTime De starttijd
     * @param endTime   De eindtijd
     * @throws IllegalArgumentException Ongeldige starttijd en/of eindtijd
     */
    public DailyAvailability(LocalTime startTime, LocalTime endTime) throws IllegalArgumentException {
        if (! isValidStartTimeEndTime(startTime, endTime))
            throw new IllegalArgumentException("Ongeldige starttijd en/of eindtijd");
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Controleert of een tijdstip tot deze dagelijkse beschikbaarheid behoort.
     * @param dateTime Het te controleren tijdstip
     * @return True als het tijdstip tussen de starttijd en eindtijd van deze dagelijkse beschikbaarheid ligt
     *                  en het tijdstip niet op zaterdag of zondag is.
     */
    public boolean containsDateTime(LocalDateTime dateTime) {
        if (dateTime == null)
            return false;
        else if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY || dateTime.getDayOfWeek() == DayOfWeek.SUNDAY)
            return false;
        else
            return this.containsTime(dateTime.toLocalTime());
    }

    /**
     * Geeft de starttijd van de dagelijkse beschikbaarheid.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Geeft de eindtijd van de dagelijkse beschikbaarheid.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Controleert of een start- en eindtijd geldig zijn.
     * @param startTime De starttijd die gecontroleerd moet worden
     * @param endTime   De eindtijd die gecontroleerd moet worden
     * @return          True als de starttijd en eindtijd niet null zijn, en de starttijd voor de eindtijd ligt, en er minstens 1 uur tussen zit.
     */
    public boolean isValidStartTimeEndTime(LocalTime startTime, LocalTime endTime){
        return startTime != null && endTime != null && startTime.isBefore(endTime)
                && Duration.between(startTime, endTime).compareTo(Duration.ofHours(1)) >= 0;
    }

    private boolean containsTime(LocalTime time) {
        return time != null && time.isAfter(this.getStartTime()) && time.isBefore(this.getEndTime());
    }

    /**
     * Geeft het eerst volgende uur dat in deze dagelijkse beschikbaarheid valt voor een gegeven tijdstip.
     * @param dateTime Het gegeven tijdstip
     */
    public LocalDateTime getNextTime(LocalDateTime dateTime) {
        LocalDateTime dateTimeHours = this.getNextHour(dateTime);
        if (this.containsDateTime(dateTimeHours))
            return dateTimeHours;
        else
            return this.getNextHour(this.getNextStartTime(dateTime));
    }

    private LocalDateTime getNextHour(LocalDateTime dateTime) {
        if (dateTime.getMinute() == 0)
            return dateTime;
        else
            return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour()+1,0));
    }

    /**
     * Geeft de tijdsduur van een gegeven tijdstip tot de eerst volgende eindtijd (van deze dagelijkse beschikbaarheid).
     * @param dateTime Het gegeven tijdstip
     */
    public Duration getDurationUntilNextEndTime(LocalDateTime dateTime) {
        return Duration.between(dateTime, this.getNextEndTime(dateTime));
    }

    /**
     * Geeft het tijdstip van de eerst volgende starttijd voor een gegeven tijdstip.
     * @param dateTime Het gegeven tijdstip
     */
    public LocalDateTime getNextStartTime(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        LocalDate nextStartDate = date;

        DayOfWeek day = dateTime.getDayOfWeek();

        if (day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY || day == DayOfWeek.THURSDAY) {
            if (time.isAfter(this.getStartTime())) {
                nextStartDate = date.plusDays(1);
            }
        }

        else if (day == DayOfWeek.FRIDAY) {
            if (time.isAfter(this.getStartTime())) {
                nextStartDate = date.plusDays(3);
            }
        }

        else if (day == DayOfWeek.SATURDAY) {
            nextStartDate = date.plusDays(2);
        }

        else if (day == DayOfWeek.SUNDAY) {
            nextStartDate = date.plusDays(1);
        }

        return LocalDateTime.of(nextStartDate, this.getStartTime());
    }

    /**
     * Geeft het tijdstip van de eerst volgende eindtijd voor een gegeven tijdstip.
     * @param dateTime Het gegeven tijdstip
     */
    public LocalDateTime getNextEndTime(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        LocalDate nextEndDate = date;

        DayOfWeek day = dateTime.getDayOfWeek();

        if (day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY || day == DayOfWeek.THURSDAY) {
            if (time.isAfter(this.getEndTime())) {
                nextEndDate = date.plusDays(1);
            }
        }

        else if (day == DayOfWeek.FRIDAY) {
            if (time.isAfter(this.getEndTime())) {
                nextEndDate = date.plusDays(3);
            }
        }

        else if (day == DayOfWeek.SATURDAY) {
            nextEndDate = date.plusDays(2);
        }

        else if (day == DayOfWeek.SUNDAY) {
            nextEndDate = date.plusDays(1);
        }

        return LocalDateTime.of(nextEndDate, this.getEndTime());
    }

}