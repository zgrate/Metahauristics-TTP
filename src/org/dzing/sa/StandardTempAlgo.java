package org.dzing.sa;

import org.dzing.base.TTP;

import java.util.Random;

public class StandardTempAlgo implements TemperatureAlgo {

    private final double cutoff;
    double dividor;
    double startTemp;
    Random random = new Random();

    public StandardTempAlgo(int dividor, int startTemp, double cutoff) {
        this.startTemp = startTemp;
        this.dividor = dividor;
        this.cutoff = cutoff;
    }

    @Override
    public boolean temperatureChance(TTP ttp, TTP.ItemsResponse best, TTP.ItemsResponse current) {
        startTemp++;
//        System.out.println(dividor/startTemp);

        double res = (dividor / startTemp);
        return !(res < cutoff) && random.nextFloat() < res;
    }
}
