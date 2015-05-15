package be.swop.groep11.test;

import be.swop.groep11.main.util.Observable;
import be.swop.groep11.main.util.Observer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Ronald on 14/05/2015.
 */
public class ObservableTest {

    private Observer<FOO> observer;
    private FOO observable;
    @Before
    public void setUp() throws Exception {
        observer = (Observer<FOO>) mock(Observer.class);
        observable = new FOO();
    }

    @Test
    public void addAndRemoveObserver_valid() throws Exception {
        observable.addObserver(observer);
        assertTrue(observable.containsObserver(observer));
        observable.removeObserver(observer);
        assertFalse(observable.containsObserver(observer));
    }

    @Test (expected = IllegalArgumentException.class)
    public void addObserver_twice_invalid() throws Exception {
        observable.addObserver(observer);
        observable.addObserver(observer);
    }

    @Test (expected = IllegalArgumentException.class)
    public void addObserver_null_invalid() throws Exception {
        observable.addObserver(null);
    }

    @Test
    public void updateAllObservers_correct() throws Exception {
        Observer<FOO> o1 =(Observer<FOO>) mock(Observer.class);
        Observer<FOO> o2 =(Observer<FOO>) mock(Observer.class);
        Observer<FOO> o3 =(Observer<FOO>) mock(Observer.class);

        observable.addObserver(o1);
        observable.addObserver(o2);
        observable.addObserver(o3);

        observable.change();
        verify(o1,atLeastOnce()).update(observable);
        verify(o2,atLeastOnce()).update(observable);
        verify(o3,atLeastOnce()).update(observable);
    }


    class FOO extends Observable<FOO> {
        private int i = 0;
        public void change(){
            i++;
            updateObservers();
        }
    }
}