package org.dzing.itemchoicealgorithms;

import org.dzing.base.City;
import org.dzing.base.Item;
import org.dzing.base.ItemChoiceAlgorithm;
import org.dzing.base.TTP;

import java.util.*;

public class GreedyPriceOverWeight implements ItemChoiceAlgorithm {

    private TTP lastTTP = null;
    private Map<Integer, List<Item>> reversedSortedItemsSet;

    public static int hit;

    private void recalculateLists(TTP ttp) {
        Map<Integer, List<Item>> internal = new HashMap<>();
        lastTTP = ttp;
        for (City c : ttp.cities) {
            Item[] cityItems = c.getItems();
            Arrays.sort(cityItems, (o1, o2) -> Double.compare(o2.getProfitToWeight(), o1.getProfitToWeight()));
//            Arrays.stream(cityItems).forEach(it -> System.out.println(it.getProfitToWeight()));
//            List<Item> items = Arrays.stream(c.getItems())
//                    .sorted(Comparator.comparingDouble(Item::getProfitToWeight).reversed())
//                    .collect(Collectors.toList());
            internal.put(c.getId(), Arrays.stream(cityItems).toList());
        }
//        System.out.println(internal);
        reversedSortedItemsSet = internal;
    }

    private List<Item> getSortedInCity(TTP ttp, City city) {
        if (lastTTP != ttp || lastTTP == null || reversedSortedItemsSet == null) {
            recalculateLists(ttp);
        }
        return reversedSortedItemsSet.get(city.getId());
    }

    private Map<Integer, TTP.ItemsResponse> responses = new HashMap<>();

    private TTP.ItemsResponse checkResponse(City[] cities) {
        hit += 1;
        return responses.get(Arrays.hashCode(cities));
    }

    @Override
    public TTP.ItemsResponse selectItemsAndScore(TTP ttp, City[] citiesInOrder) {
//        TTP.ItemsResponse check = checkResponse(citiesInOrder);
//        if(check != null){
//            return check;
//        }
        int weight = 0;
        List<Item> items = new ArrayList<>();
        for (City c : citiesInOrder) {
            List<Item> set = getSortedInCity(ttp, c);
            for (Item i : set) {
                if (weight + i.getWeight() > ttp.capacity) {
                    TTP.ItemsResponse res = new TTP.ItemsResponse(ttp, items, (citiesInOrder));
//                    responses.put(Arrays.hashCode(citiesInOrder), res);
                    return res;
                } else {
                    items.add(i);
                    weight += i.getWeight();
                }
            }

        }
        TTP.ItemsResponse res = new TTP.ItemsResponse(ttp, items, (citiesInOrder));
//        responses.put(Arrays.hashCode(citiesInOrder), res);
        return res;
    }
}
