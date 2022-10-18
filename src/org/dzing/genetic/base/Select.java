package org.dzing.genetic.base;

import org.dzing.base.TTP;

public interface Select {

    int[][] select(int[][] population, TTP.ItemsResponse[] scores, int selectNumber);

}
