package org.dzing.base;

import org.dzing.itemchoicealgorithms.GreedyWithTravelCostNextCity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class TTP {

    public City[] cities;
    public double[][] distanceMatrix;
    public String name;
    public String datatype;
    public int dimension;
    public int itemsNumber;
    public int capacity;
    public double minSpeed;
    public double maxSpeed;
    public double rentingRatio;
    public String weight_type;
    public Item[] globalItemsArray;

    public static TTP loadTTP(String filename) {
        TTP ttp = new TTP();

        String[] readFile = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            readFile = reader.lines().toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        for (int i = 0; i < readFile.length; i++) {
            String[] split = readFile[i].split("\t");
            System.out.println(Arrays.toString(split));
            switch (split[0].strip()) {
                case "PROBLEM NAME:" -> {
                    ttp.name = split[1];
                    continue;
                }
//                case "KNAPSACK DATA TYPE" -> {
//                    ttp.datatype = split[1];
//                    continue;
//                }
                case "DIMENSION:" -> {
                    ttp.dimension = Integer.parseInt(split[1]);
                    ttp.cities = new City[ttp.dimension];
                    ttp.distanceMatrix = new double[ttp.dimension][ttp.dimension];
                    continue;
                }
                case "NUMBER OF ITEMS:" -> {
                    ttp.itemsNumber = Integer.parseInt(split[1]);
                    ttp.globalItemsArray = new Item[ttp.itemsNumber];
                    continue;
                }
                case "CAPACITY OF KNAPSACK:" -> {
                    ttp.capacity = Integer.parseInt(split[1]);
                    continue;
                }
                case "MIN SPEED:" -> {
                    ttp.minSpeed = Double.parseDouble(split[1]);
                    continue;
                }
                case "MAX SPEED:" -> {
                    ttp.maxSpeed = Double.parseDouble(split[1]);
                    continue;
                }
                case "RENTING RATIO:" -> {
                    ttp.rentingRatio = Double.parseDouble(split[1]);
                    continue;
                }
                case "EDGE_WEIGHT_TYPE:" -> {
                    ttp.weight_type = (split[1]);
                    continue;
                }
            }
            if (split[0].equals("NODE_COORD_SECTION")) {
                for (int j = 0; j < ttp.dimension; j++) {
                    String[] split2 = readFile[i + j + 1].split("\t");
                    City city = new City((int) Double.parseDouble(split2[0]), (int) Double.parseDouble(split2[1]), (int) Double.parseDouble(split2[2]));
                    ttp.cities[city.getId() - 1] = city;
                }
                i += ttp.dimension;
            } else if (split[0].equals("ITEMS SECTION")) {
                List<Item> items = new ArrayList<Item>();
                for (int j = 0; j < ttp.itemsNumber; j++) {
                    String[] split2 = readFile[i + j + 1].split("\t");
                    Item item = new Item(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]), Integer.parseInt(split2[2]), Integer.parseInt(split2[3]));
                    items.add(item);
                }
                i += ttp.itemsNumber;
                for (City c : ttp.cities) {
                    c.items = items.stream().filter(it -> it.getCity() == c.getId()).toArray(Item[]::new);
                }
                for (int j = 0; j < items.size(); j++) {
                    items.get(j).setGlobalItemID(j);
                    ttp.globalItemsArray[j] = items.get(j);
                }
            }

        }

        for (int i = 0; i < ttp.dimension; i++) {
            for (int j = 0; j < ttp.dimension; j++) {
                ttp.distanceMatrix[i][j] = ttp.cities[i].distanceTo(ttp.cities[j]);
            }
        }

        return ttp;
    }

    public City[] getAsCities(int[] solution) {
        City[] city = new City[solution.length];
        for (int i = 0; i < city.length; i++) {
            city[i] = cities[solution[i]];
        }
        return city;
    }

    public Results calculateResults(List<Item> items, City[] cities) {

        Set<Item> set = new HashSet<>(items);
        int currentWeight = 0, currentValue = 0;
        double currentTime = 0.0, currentResult = 0.0;
        City nextCity = null;

        for (int cityIndex = 0; cityIndex < cities.length - 1; cityIndex++) {
            City currentCity = cities[cityIndex];
            for (Item i : currentCity.getItems()) {
                if (set.contains(i)) {
                    currentWeight += i.getWeight();
                    currentValue += i.getProfit();
                    set.remove(i);
                }
            }
            nextCity = cities[cityIndex + 1];
            currentTime += this.distanceMatrix[currentCity.getId() - 1][nextCity.getId() - 1] / calculateSpeed(currentWeight);
        }
        if (nextCity != null) {
            for (Item i : nextCity.getItems()) {
                if (items.contains(i)) {
                    currentWeight += i.getWeight();
                    currentValue += i.getProfit();
                }
            }
        }
        assert currentWeight <= capacity;
        return new Results(currentWeight, currentValue, currentTime, currentValue - currentTime);

    }

    public double calculateSpeed(int weight) {
        return maxSpeed - weight * ((maxSpeed - minSpeed) / capacity);
    }

    public int getDimension() {
        return dimension;
    }

    public ItemsResponse calculateFunctionValueWithGreedyItemSelection(City[] cities) {
        assert cities.length == this.cities.length && cities.length == this.dimension;
        //TODO
        return new GreedyWithTravelCostNextCity(0.5).selectItemsAndScore(this, cities);
    }

    public ItemCountResponse calculateWeight(List<Item> items) {
        int lastIndex = 0;
        int weight = 0;
        int price = 0;

        for (lastIndex = 0; lastIndex < items.size(); lastIndex++) {
            Item item = this.globalItemsArray[lastIndex];
            int currentWeight = item.getWeight() + weight;
            if (currentWeight >= this.capacity) {
                break;
            }
            weight += item.getWeight();
            price += item.getProfit();
        }
        return new ItemCountResponse(lastIndex, weight, price);
    }

    public record Results(int currentWeight, int currentValue, double currentTime, double currentResult) {
    }

    public static class ItemsResponse {

        public List<Item> items;
        public City[] cities;
        public double currentTime;
        public int currentValue, currentWeight;
        public double currentResult;

        public List<Item> getItems() {
            return items;
        }

        public City[] getCities() {
            return cities;
        }

        public double getCurrentTime() {
            return currentTime;
        }

        public int getCurrentValue() {
            return currentValue;
        }

        public int getCurrentWeight() {
            return currentWeight;
        }

        public double getCurrentResult() {
            return currentResult;
        }

        public ItemsResponse(List<Item> items, City[] cities, double currentTime, int currentValue, int currentWeight, double currentResult) {
            this.items = items;
            this.cities = cities;
            this.currentTime = currentTime;
            this.currentValue = currentValue;
            this.currentWeight = currentWeight;
            this.currentResult = currentResult;
        }

        public ItemsResponse(TTP ttp, List<Item> items, City[] cities) {
            this.items = items;
            this.cities = cities;
            Results results = ttp.calculateResults(items, cities);
            this.currentWeight = results.currentWeight;
            this.currentValue = results.currentValue;
            this.currentResult = results.currentResult;
            this.currentTime = results.currentTime;
        }

        @Override
        public String toString() {
            return "ItemsResponse{" +
                    "items=" + items +
                    ", currentTime=" + currentTime +
                    ", currentValue=" + currentValue +
                    ", currentWeight=" + currentWeight +
                    ", currentResult=" + currentResult +
                    '}';
        }

        public String getCSVString() {
            return currentResult +
                    ";" +
                    currentTime +
                    ";" +
                    currentWeight +
                    ";" +
                    currentValue +
                    ";" +
                    String.join(",", items.stream().map(it -> "" + it.getIndex()).toArray(String[]::new)) +
                    ";" +
                    String.join(",", Arrays.stream(cities).map(it -> "" + it.getId()).toArray(String[]::new));
        }
    }

    public class ItemCountResponse {
        public final int itemsLength;
        public final int itemsWeight;
        public final int price;


        public ItemCountResponse(int itemsLength, int itemsWeight, int price) {
            this.itemsLength = itemsLength;
            this.itemsWeight = itemsWeight;
            this.price = price;
        }
    }

}
