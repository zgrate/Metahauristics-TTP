package org.dzing.itemchoicealgorithms;

import org.dzing.base.City;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;

import java.util.ArrayList;

public class NoItems implements ItemChoiceAlgorithm {
    @Override
    public TTP.ItemsResponse selectItemsAndScore(TTP ttp, int[] citiesInOrder) {
        City[] cities = ttp.getAsCities(citiesInOrder);
        double distance = 0;
        for (int i = 0; i < cities.length - 1; i++) {
            distance += ttp.distanceMatrix[cities[i].getId() - 1][cities[i + 1].getId() - 1];
        }
        return new TTP.ItemsResponse(new ArrayList<>(), cities, 0, 0, 0, distance);
    }
}
