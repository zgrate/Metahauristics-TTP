package org.dzing.itemchoicealgorithms;

import org.dzing.base.City;
import org.dzing.base.Item;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;

import java.util.*;
import java.util.stream.Collectors;


public class GreedyWithTravelCostNextCity implements ItemChoiceAlgorithm {

    double distanceInfluence;

    public GreedyWithTravelCostNextCity(double distanceInfluence) {
        this.distanceInfluence = distanceInfluence;
    }

    //Wejdź do miasta
    //Policz, który przedmiot opłaca się brać najbardziej (najwięszy wzrost w stosunku do ceny - wszstkie z miasta + 1 z drugiego)
    //Jeżeli z tego, powtórz
    //W przeciwnym razie idz do nastepnego
    @Override
    public TTP.ItemsResponse selectItemsAndScore(TTP ttp, City[] cities) {
        List<Item> items = new ArrayList<>();
        double currentTime = 0;
        int currentValue = 0;
        int currentWeight = 0;
        for (int i = 0; i < cities.length - 1; i++) {
            City currentCity = cities[i];
            City nextCity = cities[i + 1];
            double distance = ttp.distanceMatrix[currentCity.getId() - 1][nextCity.getId() - 1];
            for (int j = i + 2; nextCity.items.length == 0 && j < cities.length - 1; j++) {
                City nextCity2 = cities[j];
                distance += ttp.distanceMatrix[nextCity.getId() - 1][nextCity2.getId() - 1];
                nextCity = nextCity2;
            }
            Set<Item> itemsInCity = Arrays.stream(currentCity.items).collect(Collectors.toSet());
            Item firstItemInNextCity = nextCity.items.length > 0 ? nextCity.items[0] : null;


            while (itemsInCity.size() > 0) {
                double finalCurrentValue = currentValue;
                int finalCurrentWeight = currentWeight;
                double finalCurrentTime = currentTime;
                Item maxItem = itemsInCity.stream().filter(it -> finalCurrentWeight + it.getWeight() < ttp.capacity)
                        .max(Comparator.comparingDouble(it -> (finalCurrentValue + it.getProfit() - finalCurrentTime) / ttp.calculateSpeed(finalCurrentWeight + it.getWeight()))).orElse(null);
                if (maxItem == null) {
                    itemsInCity.clear();
                } else {
                    //CurrentValue + ChangeInProfit - currentTimeToTravel
                    //Speed Change after adding an item
                    double thisItem = (double) maxItem.getProfit() / (double) maxItem.getWeight();
                    double thatItem = Double.MIN_VALUE;
                    if (firstItemInNextCity != null)
                        //CurrentValue + ChangeInProfitInSecondItem - currentTimeToTrave - (distance/current_speed)
                        //Speed change after adding an item
//                        thatItem = (currentValue + firstItemInNextCity.getProfit() - currentTime - distance / ttp.calculateSpeed(currentWeight)) / ttp.calculateSpeed(currentWeight + firstItemInNextCity.getWeight());
                        thatItem = (firstItemInNextCity.getProfit() - (distance * distanceInfluence)) / firstItemInNextCity.getWeight();
                    if (thisItem >= thatItem) {
                        items.add(maxItem);
                        itemsInCity.remove(maxItem);
                        currentValue += maxItem.getProfit();
                        currentWeight += maxItem.getWeight();
                    } else {
                        itemsInCity.clear();
                    }
                }
            }
            currentTime += distance / ttp.calculateSpeed(currentWeight);
        }
        if (currentWeight < ttp.capacity) {
            City lastCity = cities[cities.length - 1];
            int finalCurrentWeight1 = currentWeight;
            List<Item> sortedItems = Arrays.stream(lastCity.items).filter(item -> item.getWeight() + finalCurrentWeight1 > ttp.capacity).sorted(Comparator.comparingInt(c -> (c.getProfit() / c.getWeight()))).collect(Collectors.toList());
            boolean notOverfilled = true;
            while (notOverfilled) {
                if (sortedItems.size() == 0) {
                    notOverfilled = false;
                } else {
                    Item i = sortedItems.get(0);
                    sortedItems.remove(0);
                    if (i.getWeight() + currentWeight <= ttp.capacity) {
                        items.add(i);
                        currentWeight += i.getWeight();
                        currentValue += i.getProfit();
                    }
                }
            }
        }
        return new TTP.ItemsResponse(ttp, items, (cities));
    }
}
