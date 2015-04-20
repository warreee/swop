package be.swop.groep11.main.ui;

import java.util.function.Function;

/**
 * Created by Ronald on 20/04/2015.
 * @param <T> Het type van de gevraagde userInput.
 */
public interface userInput<T> extends Function<String,T> {
    /**
     * Vraag input van de gebruiker.
     *
     * @param request   De beschrijving die de gebruiker te zien krijgt.
     * @return  T       De response op de vraag.
     */
    default T getUserInput(String request){
        return apply(request);
    }
}
