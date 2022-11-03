package org.dzing.sa;

import org.dzing.base.TTP;

import java.util.Random;

public class StandardTempAlgo implements TemperatureAlgo {

    int startTemp;
    Random random = new Random();

    public StandardTempAlgo(int startTemp) {
        this.startTemp = startTemp;
    }

    @Override
    public boolean temperatureChance(TTP ttp, TTP.ItemsResponse best, TTP.ItemsResponse current) {
        startTemp++;
//        System.out.println((current.getCurrentResult())/(startTemp*10));
        return random.nextFloat() < (1.0) / (startTemp);
    }
}
