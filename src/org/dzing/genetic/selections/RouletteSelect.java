package org.dzing.genetic.selections;

import org.dzing.base.TTP;
import org.dzing.genetic.base.Select;

import java.util.*;

public class RouletteSelect implements Select {

    int pressure = 0;


    public RouletteSelect(int pressure) {
        this.pressure = pressure;
    }

    public static int randomWeightSelect(TTP.ItemsResponse[] scores) {

        double minWeight = Arrays.stream(scores).map(TTP.ItemsResponse::getCurrentResult).min(Comparator.naturalOrder()).orElseThrow();
        if (minWeight < 0) {
            minWeight = -minWeight;
        }
        // Compute the total weight of all items together.
        // This can be skipped of course if sum is already 1.
        double totalWeight = 0.0;
        for (TTP.ItemsResponse i : scores) {
            totalWeight += i.getCurrentResult() + minWeight;
        }

        // Now choose a random item.
        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < scores.length - 1; ++idx) {
            r -= scores[idx].getCurrentResult() + minWeight;
            if (r <= 0.0) break;
        }
        return idx;
    }

    @Override
    public int[][] select(int[][] population, TTP.ItemsResponse[] scores, int selectNumber) {
        double minWeight = Arrays.stream(scores).map(TTP.ItemsResponse::getCurrentResult).min(Comparator.naturalOrder()).orElseThrow() * pressure;
        if (minWeight < 0) {
            minWeight = -minWeight;
        }
        WeightedRandomBag<int[]> bag = new WeightedRandomBag<>();
        for (int j = 0; j < population.length; j++) {
//            System.out.println(scores[j].getCurrentResult() + " is now " + Math.pow((scores[j].getCurrentResult()+minWeight)*pressure, 1));
            bag.addEntry(population[j], Math.pow((scores[j].getCurrentResult() + minWeight) * pressure, 1));
        }

//        List<Integer> selectedIds = new ArrayList<>();
        int[][] arra = new int[selectNumber][];
        for (int i = 0; i < selectNumber; i++) {
            arra[i] = bag.getRandom();
//            if(i == 0){
//                selectedIds.add(randomWeightSelect(scores));
//            }
//            else{
//                int id = randomWeightSelect(scores);
////                while(!selectedIds.contains(id = randomWeightSelect(scores)))
////                {}
//                selectedIds.add(id);
//
//            }
        }
//        return selectedIds.stream().map(it -> population[it]).toArray(int[][]::new);

        return arra;
    }

    public class WeightedRandomBag<T extends Object> {

        private List<Entry> entries = new ArrayList<>();
        private double accumulatedWeight;
        private Random rand = new Random();

        public void addEntry(T object, double weight) {
            accumulatedWeight += weight;
            Entry e = new Entry();
            e.object = object;
            e.accumulatedWeight = accumulatedWeight;
            entries.add(e);
        }

        public T getRandom() {
            double r = rand.nextDouble() * accumulatedWeight;

            for (Entry entry : entries) {
                if (entry.accumulatedWeight >= r) {
                    return entry.object;
                }
            }
            return null; //should only happen when there are no entries
        }

        private class Entry {
            double accumulatedWeight;
            T object;
        }
    }
}
