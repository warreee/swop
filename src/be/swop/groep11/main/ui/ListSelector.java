package be.swop.groep11.main.ui;

import java.util.List;

/**
 * Created by Ronald on 20/04/2015.
 */
public interface ListSelector<T>  {

    T select(List<T> list);
}
