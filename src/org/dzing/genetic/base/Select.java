package org.dzing.genetic.base;

import org.dzing.base.City;
import org.dzing.base.TTP;

public interface Select {

    City[][] select(City[][] population, TTP.ItemsResponse[] scores, int selectNumber);

}
