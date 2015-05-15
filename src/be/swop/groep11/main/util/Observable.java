package be.swop.groep11.main.util;

import java.util.ArrayList;

/**
 * Created by Ronald on 14/05/2015.
 */
public abstract class Observable<T extends Observable<T>> {

    protected Observable() {
    }

    //observable implementation

    /**
     * Voeg een Observer toe aan de lijst van observers.
     * @param observer  De toe te voegen observer
     */
    public void addObserver(Observer<T> observer) {
        if (!canHaveAsObserver(observer)) {
            throw new IllegalArgumentException("Illegal observer");
        }
        observers.add(observer);
    }

    /**
     * Controleer of de gegeven observer een geldige observer is voor deze Observable
     * @param observer  De te controleren observer
     * @return          Waar indien observer niet null is en deze Observable de gegeven observer nog niet bevat.
     */
    public boolean canHaveAsObserver(Observer<T> observer) {
        return observer != null && !containsObserver(observer);
    }

    /**
     * Controleer of de gegeven observer reeds aanwezig is in de lijst van observers voor deze Observable
     * @param observer  de te controleren observer
     * @return          Waar indien de observer aanwezig is in de lijst van observers.
     */
    public boolean containsObserver(Observer<T> observer) {
        return observers.contains(observer);
    }

    /**
     * Verwijder de gegeven Observer uit de lijst van observers voor deze Observable.
     * @param observer  De te verwijderen observer
     */
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    /**
     * Notify/update alle observers van deze Observable.
     */
    public void updateObservers() {
        getObservers().forEach(resourcePlannerObserver -> resourcePlannerObserver.update((T)this));
    }

    private ArrayList<Observer<T>> getObservers() {
        return observers;
    }

    private ArrayList<Observer<T>> observers = new ArrayList<>();
}
