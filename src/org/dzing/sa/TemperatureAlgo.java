package org.dzing.sa;

import org.dzing.base.TTP;

public interface TemperatureAlgo {
    boolean temperatureChance(TTP ttp, TTP.ItemsResponse best, TTP.ItemsResponse current);
}
