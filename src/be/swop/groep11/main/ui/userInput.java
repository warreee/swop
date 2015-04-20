package be.swop.groep11.main.ui;

/**
 * Created by Ronald on 20/04/2015.
 * @param <T> Het type van de gevraagde userInput.
 */
public interface userInput<T> {
    /**
     * Vraag input van de gebruiker.
     *
     * @param request   De beschrijving die de gebruiker te zien krijgt.
     * @return  T       De response op de vraag.
     */
    T getUserInput(String request);
}
