package be.swop.groep11.main.util;

/**
 * Created by Ronald on 14/05/2015.
 */
public interface Observer<T extends Observable<T>> {

    /**
     * Notify/update deze Observer.
     * @param t De observable die deze Observer notified.
     */
    void update(T t);

}
