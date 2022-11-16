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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return id == city.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
