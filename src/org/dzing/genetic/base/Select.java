package org.dzing.genetic.base;

import org.dzing.base.City;
import org.dzing.base.TTP;

public interface Select {

    City[][] select(TTP.ItemsResponse[] population, int selectNumber);

}
