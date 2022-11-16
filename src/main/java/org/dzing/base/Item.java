package org.dzing.base;

public class Item {

    private int index, profit, weight, city;

    private double profitToWeight;

    private int globalItemID;

    public Item(int index, int profit, int weight, int city) {
        this.index = index;
        this.profit = profit;
        this.weight = weight;
        this.city = city;
        this.profitToWeight = (double) profit / (double) weight;
    }

    public double getProfitToWeight() {
        return profitToWeight;
    }

    public int getGlobalItemID() {
        return globalItemID;
    }

    public void setGlobalItemID(int globalItemID) {
        this.globalItemID = globalItemID;
    }

    public int getIndex() {
        return index;
    }

    public int getProfit() {
        return profit;
    }

    public int getWeight() {
        return weight;
    }

    public int getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "" +
                "" + index +
                ":" + profit +
                ":" + weight +
                ":" + city +
                ":" + globalItemID;
    }
}
