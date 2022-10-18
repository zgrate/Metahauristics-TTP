package org.dzing.itemchoicealgorithms;

import org.dzing.base.City;
import org.dzing.base.Item;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GreedyPriceOverWeightWithChangeInSpeed implements ItemChoiceAlgorithm {

    private double changeInWeightWithItem(TTP ttp, Item item) {
        return (item.getProfit() * ttp.calculateSpeed(item.getWeight())) / item.getWeight();
    }

    @Override
    public TTP.ItemsResponse selectItemsAndScore(TTP ttp, int[] citiesIds) {
        int weight = 0;
        List<Item> items = new ArrayList<>();
        City[] citiesInOrder = ttp.getAsCities(citiesIds);
        for (City c : citiesInOrder) {
            List<Item> set = Arrays.stream(c.getItems()).sorted(Comparator.comparingDouble(it -> changeInWeightWithItem(ttp, it))).collect(Collectors.toList());
            for (Item i : set) {
                if (weight + i.getWeight() > ttp.capacity) {
                    return new TTP.ItemsResponse(ttp, items, (citiesInOrder));
                } else {
                    items.add(i);
                    weight += i.getWeight();
                }
            }

        }
        return new TTP.ItemsResponse(ttp, items, (citiesInOrder));
    }
}
