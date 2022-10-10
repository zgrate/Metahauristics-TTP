package org.dzing.base;

public class City {

    public Item[] items;
    private int id;
    private int x, y;

    public City(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Item[] getItems() {
        return items;
    }

    public double distanceTo(City city) {
        return Math.sqrt(Math.pow(city.x - this.x, 2) + Math.pow(city.y - this.y, 2));
    }
}
